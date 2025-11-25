package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.common.LoginResponse;
import com.example.yddmall.config.ResponseCode;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.utils.SessionUserUtils;
import com.example.yddmall.entity.User;
import com.example.yddmall.service.UserService;
import com.example.yddmall.utils.JwtUtil;
import com.example.yddmall.utils.UploadPathUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;



@RestController
@RequestMapping("/user")
@Tag(name = "商城权限用户表接口")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    // 注入路径工具类
    @Resource
    private UploadPathUtil uploadPathUtil;

    @Value("${upload.access-url}")
    private String accessUrlPrefix;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody @Validated User user, HttpServletRequest request) {
//        System.out.println(user);
        User user1 = userService.login(user);
//        System.out.println(user1);
        if(user1 != null){
            // 生成JWT token
            String token = JwtUtil.generateToken(user1.getUserId());
            // 将token和用户信息一起返回
            return ResponseUtils.success(new LoginResponse(user1, token));
        }else {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST);
        }
    }

    @PostMapping("/loginByPhone")
    public ApiResponse<LoginResponse> loginByPhone(@RequestBody @Validated User user) {
        User dbUser = userService.loginByPhone(user);
        if (dbUser != null) {
            String token = JwtUtil.generateToken(dbUser.getUserId());
            return ResponseUtils.success(new LoginResponse(dbUser, token));
        } else {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "手机号或密码错误");
        }
    }

    //分页查询
    @GetMapping
    public ApiResponse<Page<User>> page(Page<User> page) {
        return ResponseUtils.success(userService.page(page));
    }

    //通过id查询单条数据
    @GetMapping("/{id}")
    public ApiResponse<User> getById(@PathVariable Long id) {
        return ResponseUtils.success(userService.getById(id));
    }

    //新增数据
    @PostMapping("/register")
    public ApiResponse<User> save(@RequestBody User user) {
        return ResponseUtils.success(userService.addUser(user));
    }

    //修改数据
    @PutMapping
    public ApiResponse<Boolean> updateById(@RequestBody User user) {
        return ResponseUtils.success(userService.updateById(user));
    }

    //删除数据
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return userService.removeById(id);
    }

    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "id", required = false) Long id,
                                           @RequestParam(value = "userNo", required = false) String userNo) {
        if (file.isEmpty()) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "上传文件不能为空");
        }

        try {
            // 目录优先使用用户编号，其次使用ID
            String dirKey = org.apache.commons.lang3.StringUtils.isNotBlank(userNo)
                    ? userNo
                    : (id != null ? String.valueOf(id) : null);
            if (org.apache.commons.lang3.StringUtils.isBlank(dirKey)) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "缺少用户编号或ID");
            }

            String uploadDir = uploadPathUtil.getAbsoluteDir(dirKey);
            log.info("用户目录Key {} 的上传目录: {}", dirKey, uploadDir);

            // 安全地取扩展名
            String original = file.getOriginalFilename();
            String ext = Optional.ofNullable(original)
                    .filter(f -> f.lastIndexOf('.') > 0)
                    .map(f -> f.substring(f.lastIndexOf('.')))
                    .orElse(".jpg");
            String fileName = UUID.randomUUID() + ext;

            // 确保目录存在
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                boolean created = directory.mkdirs();
                if (!created) {
                    log.error("无法创建目录: {}", uploadDir);
                    return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "无法创建上传目录");
                }
            }

            // 保存文件
            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            log.info("文件成功保存到: {}", dest.getAbsolutePath());

            // 拼装回显URL（基于编号或ID的目录）
            String imageUrl = accessUrlPrefix + "/" + dirKey + "/" + fileName;
            return ResponseUtils.success(imageUrl);
        } catch (IOException e) {
            log.error("保存文件失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("上传过程中发生错误", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "上传过程中发生错误: " + e.getMessage());
        }
    }

    // 删除图片接口
    @DeleteMapping("/image")
    public ApiResponse<String> deleteImage(@RequestParam("imageUrl") String imageUrl,
                                           @RequestParam("fileName") String fileName,
                                           @RequestParam(value = "userId", required = false) String userId,
                                           @RequestParam(value = "userNo", required = false) String userNo,
                                           HttpServletRequest request) {
        try {

            // 用户身份验证
            Long UId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }
            
            // 如前端仍传 userId，则需匹配；否则仅校验会话存在
            if (org.apache.commons.lang3.StringUtils.isNotBlank(userId)
                    && !String.valueOf(UId).equals(userId)) {
                return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "无权删除该图片");
            }

            // 获取上传目录（优先编号）
            String dirKey = org.apache.commons.lang3.StringUtils.isNotBlank(userNo) ? userNo : userId;
            if (org.apache.commons.lang3.StringUtils.isBlank(dirKey)) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "缺少用户编号或ID");
            }
            String uploadDir = uploadPathUtil.getAbsoluteDir(dirKey);
            File fileToDelete = new File(uploadDir + fileName);

            // 检查文件是否存在并删除
            if (fileToDelete.exists() && fileToDelete.isFile()) {
                boolean deleted = fileToDelete.delete();
                if (deleted) {
                    log.info("图片删除成功: {}", fileToDelete.getAbsolutePath());
                    return ResponseUtils.success("图片删除成功");
                } else {
                    log.error("图片删除失败: {}", fileToDelete.getAbsolutePath());
                    return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "图片删除失败");
                }
            } else {
                log.warn("图片文件不存在: {}", fileToDelete.getAbsolutePath());
                return ResponseUtils.success("图片文件不存在，可能已被删除");
            }
        } catch (Exception e) {
            log.error("删除图片过程中发生错误", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "删除图片失败: " + e.getMessage());
        }
    }
}

package com.example.yddmall.controller;


import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.config.ResponseCode;
import com.example.yddmall.service.ItemService;
import com.example.yddmall.service.UserService;
import com.example.yddmall.entity.Item;
import com.example.yddmall.entity.User;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.utils.SessionUserUtils;
import com.example.yddmall.utils.UploadPathUtil;
import com.example.yddmall.vo.ItemSkuVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/item")
@Tag(name = "商品基础信息表接口")
public class ItemController {

    private final ItemService itemService;

    @Resource
    private UploadPathUtil uploadPathUtil;

    @Value("${upload.access-url}")
    private String accessUrlPrefix;

    public ItemController(ItemService itemService){
        this.itemService = itemService;
    }

    @Autowired
    private UserService userService;

    //分页查询（支持批量cidList）
    @GetMapping
    public ApiResponse<Page<ItemSkuVO>> pageItemSku(
            Page<Item> page,
            Item item,
            @RequestParam(value = "cidList", required = false) String cidList
    ) {
        try {
            if (org.springframework.util.StringUtils.hasText(cidList)) {
                java.util.List<Long> cids = java.util.Arrays.stream(cidList.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .map(Long::valueOf)
                        .collect(java.util.stream.Collectors.toList());
                return ResponseUtils.success(itemService.pageItemSkuByCids(page, item, cids));
            }
            return ResponseUtils.success(itemService.pageItemSku(page, item));
        } catch (Exception e) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "cidList 参数解析失败: " + e.getMessage());
        }
    }

    @Operation(summary = "新品上架（按上架时间）")
    @GetMapping("/newest")
    public ApiResponse<Page<ItemSkuVO>> pageNewest(Page<Item> page, Item item) {
        return ResponseUtils.success(itemService.pageNewest(page, item));
    }

    @Operation(summary = "热门商品（按销量聚合）")
    @GetMapping("/popular")
    public ApiResponse<Page<ItemSkuVO>> pagePopular(@RequestParam(value = "current", defaultValue = "1") long current,
                                                    @RequestParam(value = "size", defaultValue = "10") long size,
                                                    @RequestParam(value = "sellerId") Long sellerId,
                                                    @RequestParam(value = "status", required = false) Integer status,
                                                    @RequestParam(value = "title", required = false) String title) {
        return ResponseUtils.success(itemService.pagePopular(current, size, sellerId, status, title));
    }

    @Operation(summary = "店铺分页查询商品SKU", description = "根据条件分页查询商品SKU信息")
    @GetMapping("merchant")
    public ApiResponse<Page<ItemSkuVO>> pageItemIdSku(
            @Parameter(description = "分页参数") Page<Item> page,
            @Parameter(description = "查询条件") Item item,
            HttpServletRequest request) {
        
        try {
            // 用户身份验证
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }

            User user = userService.getById(userId);
            
            // 设置查询条件中的卖家ID，确保只能查询自己的商品
            item.setSellerId(user.getMerchantId());
            
            // 执行分页查询
            Page<ItemSkuVO> result = itemService.pageItemSku(page, item);
            
            log.info("用户ID: {} 查询商品成功, 总数: {}", userId, result.getTotal());
            return ResponseUtils.success("查询成功", result);
            
        } catch (Exception e) {
            log.error("分页查询商品失败", e);
            return ResponseUtils.error(500, "系统错误，请稍后重试");
        }
    }

    @GetMapping("/{id}")
    public ApiResponse<Item> getItem(@PathVariable Long id) {
        return ResponseUtils.success(itemService.getById(id));
    }

    //新增数据
    @PostMapping
    public ApiResponse<Item> save(@RequestBody Item item) {
        return ResponseUtils.success(itemService.saveItem(item));
    }

    @PutMapping
    public ApiResponse<Item> updateById(@RequestBody Item item) {
        // item 里必须带 id，其余字段为要更新的值
        boolean ok = itemService.updateById(item);
        return ResponseUtils.success(ok ? item : null);
    }

    //删除数据
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> removeById(@PathVariable Long id) {
        return ResponseUtils.success(itemService.removeById(id));
    }

    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "merchantId", required = false) Long merchantId,
                                           @RequestParam(value = "merchantNo", required = false) String merchantNo) {
        if (file.isEmpty()) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "上传文件不能为空");
        }

        try {
            // 目录优先使用商品编号，其次使用ID
            String dirKey = org.apache.commons.lang3.StringUtils.isNotBlank(merchantNo)
                    ? merchantNo
                    : (merchantId != null ? String.valueOf(merchantId) : null);
            if (org.apache.commons.lang3.StringUtils.isBlank(dirKey)) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "缺少商品编号或ID");
            }

            String uploadDir = uploadPathUtil.getAbsoluteDir(dirKey);
            log.info("商品目录Key {} 的上传目录: {}", dirKey, uploadDir);

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

            // 拼装回显URL - 返回单个图片URL字符串
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
                                           @RequestParam(value = "merchantId", required = false) String merchantId,
                                           @RequestParam(value = "merchantNo", required = false) String merchantNo,
                                           HttpServletRequest request) {
        try {
            // 基于会话校验（至少需登录），更细致的商家/商品归属校验可后续补充
            Long userId = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }

            // 目录优先使用商品编号，其次使用ID
            String dirKey = org.apache.commons.lang3.StringUtils.isNotBlank(merchantNo)
                    ? merchantNo
                    : (org.apache.commons.lang3.StringUtils.isNotBlank(merchantId) ? merchantId : null);
            if (org.apache.commons.lang3.StringUtils.isBlank(dirKey)) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "缺少商品编号或ID");
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

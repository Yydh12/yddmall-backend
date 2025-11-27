package com.example.yddmall.controller;

import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.service.MerchantService;
import com.example.yddmall.entity.Merchant;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.utils.SessionUserUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.commons.lang3.StringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.Resource;
import org.springframework.web.multipart.MultipartFile;
import com.example.yddmall.utils.UploadPathUtil;
import com.example.yddmall.config.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import com.example.yddmall.mapper.FavoriteMerchantMapper;
import com.example.yddmall.mapper.ItemMapper;
import com.example.yddmall.mapper.CommentMapper;
import com.example.yddmall.vo.MerchantStatsVO;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@Slf4j
@RestController
@RequestMapping("/merchant")
@Tag(name = "商家表接口")
public class MerchantController {

    private final MerchantService merchantService;

    @Resource
    private UploadPathUtil uploadPathUtil;

    @Resource
    private FavoriteMerchantMapper favoriteMerchantMapper;

    @Resource
    private ItemMapper itemMapper;

    @Resource
    private CommentMapper commentMapper;

    @Value("${upload.access-url}")
    private String accessUrlPrefix;

    public MerchantController(MerchantService merchantService){
        this.merchantService = merchantService;
    }

    // 分页查询（支持按店铺名/联系人/地址模糊筛选）
    @GetMapping
    public ApiResponse<Page<Merchant>> page(Page<Merchant> page, Merchant merchant) {
        QueryWrapper<Merchant> qw = new QueryWrapper<>();
        if (merchant != null) {
            // 按主键ID精确筛选（可选）
            if (merchant.getMerchantId() != null) {
                qw.eq("merchant_id", merchant.getMerchantId());
            }
            if (StringUtils.isNotBlank(merchant.getMerchantName())) {
                qw.like("merchant_name", merchant.getMerchantName());
            }
            // 支持按店铺编号模糊查询（如 M00000006 或部分编号片段）
            if (StringUtils.isNotBlank(merchant.getMerchantNo())) {
                qw.like("merchant_no", merchant.getMerchantNo());
            }
            if (StringUtils.isNotBlank(merchant.getContactPerson())) {
                qw.like("contact_person", merchant.getContactPerson());
            }
            if (StringUtils.isNotBlank(merchant.getAddress())) {
                qw.like("address", merchant.getAddress());
            }
            if (merchant.getStatus() != null) {
                qw.eq("status", merchant.getStatus());
            }
        }
        return ResponseUtils.success(merchantService.page(page, qw));
    }

    // 店铺统计信息：粉丝数、在售商品数、平均评分、好评率
    @GetMapping("/{merchantId}/stats")
    public ApiResponse<MerchantStatsVO> getStats(@PathVariable Long merchantId) {
        Merchant merchant = merchantService.getById(merchantId);
        if (merchant == null) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "商户不存在");
        }

        MerchantStatsVO vo = new MerchantStatsVO();
        vo.setMerchantId(merchantId);

        long fansCount = favoriteMerchantMapper.countByMerchantId(merchantId);
        vo.setFansCount(fansCount);

        long onSaleCount = itemMapper.countOnSaleBySeller(merchantId);
        vo.setOnSaleCount(onSaleCount);

        Double avgRating = commentMapper.getAvgRatingBySeller(merchantId);
        Double positiveRate = commentMapper.getPositiveRateBySeller(merchantId);
        vo.setAvgRating(avgRating == null ? 0.0 : avgRating);
        vo.setPositiveRate(positiveRate == null ? 0.0 : positiveRate);

        return ResponseUtils.success(vo);
    }

    //通过id查询单条数据
    @GetMapping("/getMerchant/{merchantId}")
    public ApiResponse<Merchant> getById(@PathVariable Long merchantId) {
        Merchant m = merchantService.getById(merchantId);
        if (m == null) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "商户不存在");
        }
        return ResponseUtils.success(m);
    }

    //新增数据
    @PostMapping
    public ApiResponse<Merchant> save(@RequestBody @Validated Merchant merchant) {
        System.out.println(merchant);
        return ResponseUtils.success(merchantService.add(merchant));
    }

    //修改数据
    @PutMapping
    public ApiResponse<Boolean> updateById(@RequestBody Merchant merchant) {
        return ResponseUtils.success(merchantService.updateById(merchant));
    }

    // 上传店铺主图
    @PostMapping("/image")
    public ApiResponse<String> uploadImage(@RequestParam("file") MultipartFile file,
                                           @RequestParam(value = "merchantId", required = false) Long merchantId,
                                           @RequestParam(value = "merchantNo", required = false) String merchantNo) {
        if (file.isEmpty()) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "上传文件不能为空");
        }

        try {
            String dirKey = org.apache.commons.lang3.StringUtils.isNotBlank(merchantNo)
                    ? merchantNo
                    : (merchantId != null ? String.valueOf(merchantId) : null);
            if (org.apache.commons.lang3.StringUtils.isBlank(dirKey)) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "缺少商户编号或ID");
            }

            String uploadDir = uploadPathUtil.getAbsoluteDir(dirKey);
            log.info("商户目录Key {} 的上传目录: {}", dirKey, uploadDir);

            String original = file.getOriginalFilename();
            String ext = Optional.ofNullable(original)
                    .filter(f -> f.lastIndexOf('.') > 0)
                    .map(f -> f.substring(f.lastIndexOf('.')))
                    .orElse(".jpg");
            String fileName = UUID.randomUUID() + ext;

            File directory = new File(uploadDir);
            if (!directory.exists() && !directory.mkdirs()) {
                log.error("无法创建目录: {}", uploadDir);
                return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "无法创建上传目录");
            }

            File dest = new File(uploadDir + fileName);
            file.transferTo(dest);
            log.info("文件成功保存到: {}", dest.getAbsolutePath());

            String imageUrl = accessUrlPrefix + "/" + dirKey + "/" + fileName;

            // 更新商户主图
            Merchant m = null;
            if (merchantId != null) {
                m = merchantService.getById(merchantId);
            } else if (org.apache.commons.lang3.StringUtils.isNotBlank(merchantNo)) {
                m = merchantService.getOne(new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Merchant>()
                        .eq("merchant_no", merchantNo));
            }
            if (m == null) {
                return ResponseUtils.error(ResponseCode.BAD_REQUEST, "商户不存在");
            }
            m.setMerchantPic(imageUrl);
            merchantService.updateById(m);

            return ResponseUtils.success(imageUrl);
        } catch (IOException e) {
            log.error("保存文件失败", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "文件保存失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("上传过程中发生错误", e);
            return ResponseUtils.error(ResponseCode.INTERNAL_SERVER_ERROR, "上传过程中发生错误: " + e.getMessage());
        }
    }

    // 删除店铺主图
    @DeleteMapping("/image")
    public ApiResponse<String> deleteImage(@RequestParam("imageUrl") String imageUrl,
                                           @RequestParam("fileName") String fileName,
                                           @RequestParam(value = "userId", required = false) String userId,
                                           @RequestParam(value = "userNo", required = false) String userNo,
                                           HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session == null) {
                return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "会话不存在，请重新登录");
            }

            Object userIdObj = session.getAttribute("userId");
            if (userIdObj == null || !String.valueOf(userIdObj).equals(userId)) {
                return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "无权删除该图片");
            }

            String dirKey = org.apache.commons.lang3.StringUtils.isNotBlank(userNo) ? userNo : userId;
            String uploadDir = uploadPathUtil.getAbsoluteDir(dirKey);
            File fileToDelete = new File(uploadDir + fileName);

            if (fileToDelete.exists() && fileToDelete.isFile()) {
                boolean deleted = fileToDelete.delete();
                if (deleted) {
                    log.info("图片删除成功: {}", fileToDelete.getAbsolutePath());
                    // 清空商户主图
                    Merchant m = merchantService.getById(Long.valueOf(userId));
                    if (m != null) {
                        m.setMerchantPic(null);
                        merchantService.updateById(m);
                    }
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

    //删除数据
    @DeleteMapping("/{id}")
    public boolean removeById(@PathVariable Long id) {
        return merchantService.removeById(id);
    }
}

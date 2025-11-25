package com.example.yddmall.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.yddmall.config.ApiResponse;
import com.example.yddmall.config.ResponseCode;
import com.example.yddmall.service.ItemSkuService;
import com.example.yddmall.entity.ItemSku;
import com.example.yddmall.utils.ResponseUtils;
import com.example.yddmall.utils.SessionUserUtils;
import com.example.yddmall.utils.UploadPathUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

@RestController
@RequestMapping("/item-sku")
@Tag(name = "商品SKU信息表接口")
public class ItemSkuController {

    private static final Logger log = LoggerFactory.getLogger(ItemSkuController.class);

    private final ItemSkuService itemSkuService;

    public ItemSkuController(ItemSkuService itemSkuService) {
        this.itemSkuService = itemSkuService;
    }

    // 注入路径工具类
    @Resource
    private UploadPathUtil uploadPathUtil;

    @Value("${upload.access-url}")
    private String accessUrlPrefix;

    //分页查询
    @GetMapping
    public ApiResponse<Page<ItemSku>> page(Page<ItemSku> page, ItemSku itemSku) {
        return ResponseUtils.success(itemSkuService.page(page, new QueryWrapper<>(itemSku)));
    }

    //通过id查询单条数据
    @GetMapping("/{id}")
    public ApiResponse<List<ItemSku>> getById(@PathVariable Long id) {
        return ResponseUtils.success(itemSkuService.list(new QueryWrapper<ItemSku>().eq("item_id", id)));
    }

    // 新增数据
    @PostMapping
    public ApiResponse<List<ItemSku>> save(@RequestBody List<ItemSku> itemSkuList) {
        return ResponseUtils.success(itemSkuService.saveItemSku(itemSkuList));
    }

    /**
     * 修改商品上下架状态
     * PUT /item/status/{skuId}/{status}
     */
    @PutMapping("/status/{skuId}/{status}")
    public ApiResponse<Void> updateStatus(@PathVariable Long skuId,
                                          @PathVariable Integer status) {
        boolean ok = itemSkuService.lambdaUpdate()
                .eq(ItemSku::getSkuId, skuId)
                .set(ItemSku::getStatus, status)
                .update();
        return ok ? ResponseUtils.success()
                : ResponseUtils.error(500,"状态更新失败");
    }

    //删除数据
    @DeleteMapping("/{itemId}")
    public ApiResponse<Boolean> removeById(@PathVariable Long itemId) {
        // 构造条件：item_id = itemId
        LambdaQueryWrapper<ItemSku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ItemSku::getItemId, itemId);
        return ResponseUtils.success(itemSkuService.remove(wrapper));
    }

    //删除单个数据
    @DeleteMapping("sku/{skuId}")
    public ApiResponse<Boolean> delById(@PathVariable Long skuId) {
        return ResponseUtils.success(itemSkuService.removeById(skuId));
    }

    @PostMapping("/image")
    public ApiResponse<ItemSku> uploadImage(@RequestParam("file") MultipartFile file,
                                            @RequestParam("merchantId") Long merchantId,
                                            @RequestParam(value = "merchantNo", required = false) String merchantNo) {
        if (file.isEmpty()) {
            return ResponseUtils.error(ResponseCode.BAD_REQUEST, "上传文件不能为空");
        }

        try {
            // 目录优先使用商户编号，其次使用ID
            String dirKey = org.apache.commons.lang3.StringUtils.isNotBlank(merchantNo)
                    ? merchantNo
                    : String.valueOf(merchantId);
            String uploadDir = uploadPathUtil.getAbsoluteDir(dirKey);
            log.info("商户目录Key {} 的上传目录: {}", dirKey, uploadDir);

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

            // 拼装回显URL
            ItemSku vo = new ItemSku();
            vo.setSkuPic(accessUrlPrefix + "/" + dirKey + "/" + fileName);
            return ResponseUtils.success(vo);
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
                                         @RequestParam("userId") String userId,
                                         HttpServletRequest request) {
        try {
            // 验证用户身份，确保只能删除自己的图片
            Long id = SessionUserUtils.getUserId(request);
            if (userId == null) {
                return ResponseUtils.error(401, "身份校验失败，请重新登录");
            }

            if (userId.equals(id.toString())) {
                return ResponseUtils.error(ResponseCode.UNAUTHORIZED, "无权删除该图片");
            }

            // 获取上传目录
            String uploadDir = uploadPathUtil.getAbsoluteDir(userId);
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

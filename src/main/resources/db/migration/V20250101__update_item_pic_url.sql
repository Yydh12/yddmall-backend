-- 修改为 PostgreSQL 语法：将 pic_url 从文本改为 jsonb，并就地转换
-- 说明：
-- 1) 若现有值是 JSON（数组或对象）则直接转为 jsonb；
-- 2) 若是普通字符串，则包装成单元素数组 ["原字符串"]；
-- 3) 空白字符串按 NULL 处理。

ALTER TABLE item
  ALTER COLUMN pic_url TYPE jsonb USING (
    CASE
      WHEN trim(coalesce(pic_url, '')) = '' THEN NULL
      WHEN pic_url ~ '^[\s\n\r\t]*\[.*\][\s\n\r\t]*$' THEN pic_url::jsonb  -- 已是数组
      WHEN pic_url ~ '^[\s\n\r\t]*\{.*\}[\s\n\r\t]*$' THEN pic_url::jsonb  -- 已是对象
      ELSE jsonb_build_array(pic_url)                                              -- 普通字符串 → 包装为数组
    END
  );

COMMENT ON COLUMN item.pic_url IS '主图URL列表，JSON格式存储多个图片路径';

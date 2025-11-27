-- 修改item表的pic_url字段类型，从VARCHAR改为JSON以支持存储多个图片路径
ALTER TABLE item MODIFY COLUMN pic_url JSON COMMENT '主图URL列表，JSON格式存储多个图片路径';

-- 如果表中已有数据，将现有的单个URL转换为JSON数组格式
UPDATE item SET pic_url = JSON_ARRAY(pic_url) WHERE pic_url IS NOT NULL AND JSON_VALID(pic_url) = 0;
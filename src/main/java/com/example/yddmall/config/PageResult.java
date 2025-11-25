package com.example.yddmall.config;

import lombok.Data;
import java.util.List;

@Data
public class PageResult<T> {
    private long total;  // 总条数
    private List<T> list; // 数据列表
    private int pageNum; // 当前页码
    private int pageSize; // 每页条数
}

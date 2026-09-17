package com.car.rental.common.result;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<T> list;
    private long total;
    private long page;
    private long pageSize;

    /** 筛选结果总计（不受分页影响，用于表格合计行展示金额列汇总） */
    private Map<String, Object> summary;

    private PageResult() {
    }

    private PageResult(List<T> list, long total, long page, long pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
    }

    public static <T> PageResult<T> of(IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize());
    }

    /** 附带筛选结果总计（合计行数据） */
    public static <T> PageResult<T> of(IPage<T> page, Map<String, Object> summary) {
        PageResult<T> result = of(page);
        result.setSummary(summary);
        return result;
    }
}
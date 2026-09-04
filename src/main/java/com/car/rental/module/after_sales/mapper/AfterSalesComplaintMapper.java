package com.car.rental.module.after_sales.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.car.rental.entity.AfterSalesComplaint;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface AfterSalesComplaintMapper extends BaseMapper<AfterSalesComplaint> {

    @Update("UPDATE after_sales_complaint SET status = #{status}, assignee = #{assignee}, solution = #{solution}, satisfaction = #{satisfaction}, resolved_at = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Long id,
                     @Param("status") String status,
                     @Param("assignee") String assignee,
                     @Param("solution") String solution,
                     @Param("satisfaction") Integer satisfaction);

    /** 开始处理：仅更新状态为处理中 + 记录处理人，不写解决时间 */
    @Update("UPDATE after_sales_complaint SET status = 'processing', assignee = #{assignee} WHERE id = #{id}")
    int startProcessing(@Param("id") Long id, @Param("assignee") String assignee);

    /** 快捷修改优先级 */
    @Update("UPDATE after_sales_complaint SET priority = #{priority} WHERE id = #{id}")
    int updatePriority(@Param("id") Long id, @Param("priority") String priority);
}
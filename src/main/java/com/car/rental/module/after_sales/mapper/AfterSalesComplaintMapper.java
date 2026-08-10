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
}
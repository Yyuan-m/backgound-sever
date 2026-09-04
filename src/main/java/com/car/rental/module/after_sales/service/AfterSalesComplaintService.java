package com.car.rental.module.after_sales.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.AfterSalesComplaint;
import com.car.rental.entity.CarInfo;

public interface AfterSalesComplaintService {

    IPage<AfterSalesComplaint> getPageList(long pageNum, long pageSize, String ticketNo, String type, String status, String priority);

    AfterSalesComplaint getById(Long id);

    void addComplaint(AfterSalesComplaint complaint);

    void updateComplaint(AfterSalesComplaint complaint);

    void deleteComplaint(Long id);

    void handleComplaint(Long id, String status, String assignee, String solution, Integer satisfaction);

    /** 开始处理：等待处理 → 处理中，记录当前处理人 */
    void startProcessing(Long id);

    /** 快捷修改优先级 */
    void updatePriority(Long id, String priority);

    /** 根据售后工单查询其关联车辆详情（订单→车辆） */
    CarInfo getVehicleByComplaintId(Long complaintId);
}
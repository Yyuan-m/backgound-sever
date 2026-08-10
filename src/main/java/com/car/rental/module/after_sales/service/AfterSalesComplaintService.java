package com.car.rental.module.after_sales.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.car.rental.entity.AfterSalesComplaint;

public interface AfterSalesComplaintService {

    IPage<AfterSalesComplaint> getPageList(long pageNum, long pageSize, String ticketNo, String type, String status, String priority);

    AfterSalesComplaint getById(Long id);

    void addComplaint(AfterSalesComplaint complaint);

    void updateComplaint(AfterSalesComplaint complaint);

    void deleteComplaint(Long id);

    void handleComplaint(Long id, String status, String assignee, String solution, Integer satisfaction);
}
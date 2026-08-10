package com.car.rental.module.car.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.CarDocument;

import java.util.List;

public interface CarDocumentService {

    PageResult<CarDocument> getList(Integer pageNum, Integer pageSize, Long vehicleId, String docType, String status);

    CarDocument getById(Long id);

    void add(CarDocument carDocument);

    void update(CarDocument carDocument);

    void delete(Long id);

    List<CarDocument> getByVehicleId(Long vehicleId);
}
package com.car.rental.module.car.service;

import com.car.rental.common.result.PageResult;
import com.car.rental.entity.GpsTrack;

import java.util.List;

public interface GpsTrackService {

    PageResult<GpsTrack> getList(Integer pageNum, Integer pageSize, Long vehicleId, String status);

    List<GpsTrack> getByVehicleId(Long vehicleId);

    GpsTrack getLatest(Long vehicleId);

    void add(GpsTrack gpsTrack);

    void update(GpsTrack gpsTrack);

    void delete(Long id);
}
package com.car.rental.module.car.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.car.rental.common.result.PageResult;
import com.car.rental.entity.GpsTrack;
import com.car.rental.module.car.mapper.GpsTrackMapper;
import com.car.rental.module.car.service.GpsTrackService;
import com.car.rental.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GpsTrackServiceImpl implements GpsTrackService {

    private final GpsTrackMapper gpsTrackMapper;

    @Override
    public PageResult<GpsTrack> getList(Integer pageNum, Integer pageSize, Long vehicleId, String status) {
        Page<GpsTrack> page = new Page<>(pageNum != null ? pageNum : 1, pageSize != null ? pageSize : 10);
        LambdaQueryWrapper<GpsTrack> wrapper = new LambdaQueryWrapper<>();

        if (vehicleId != null) {
            wrapper.eq(GpsTrack::getVehicleId, vehicleId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(GpsTrack::getStatus, status);
        }
        wrapper.orderByDesc(GpsTrack::getTimestamp);

        IPage<GpsTrack> result = gpsTrackMapper.selectPage(page, wrapper);
        return PageResult.of(result);
    }

    @Override
    public List<GpsTrack> getByVehicleId(Long vehicleId) {
        return gpsTrackMapper.selectByVehicleId(vehicleId);
    }

    @Override
    public GpsTrack getLatest(Long vehicleId) {
        return gpsTrackMapper.selectLatest(vehicleId);
    }

    @Override
    @Transactional
    public void add(GpsTrack gpsTrack) {
        gpsTrackMapper.insert(gpsTrack);
    }

    @Override
    @Transactional
    public void update(GpsTrack gpsTrack) {
        if (gpsTrack.getId() == null || gpsTrackMapper.selectById(gpsTrack.getId()) == null) {
            throw new BusinessException("GPS轨迹记录不存在");
        }
        gpsTrackMapper.updateById(gpsTrack);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (gpsTrackMapper.selectById(id) == null) {
            throw new BusinessException("GPS轨迹记录不存在");
        }
        gpsTrackMapper.deleteById(id);
    }
}
package com.car.rental.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 车辆配置实体
 * - safety 字段存储安全配置（如气囊、ACC、车道保持等），多项目以 " / " 分隔
 * - entertainment 字段存储娱乐配置（如音响品牌、HUD、中控屏等），多项目以 " / " 分隔
 */
@Data
@TableName("car_config")
public class CarConfig {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long carId;

    /** 动力总成，如 "3.0T 涡轮增压" */
    private String power;

    /** 变速箱，如 "8挡手自一体" */
    private String transmission;

    /** 燃料类型，如 "汽油" / "纯电" / "油电混合" / "增程式电动" */
    private String fuel;

    /** 续航里程（纯电/混动车型有效），燃油车填 "-" */
    private String rangeKm;

    /** 内饰材质，如 "Nappa 真皮" */
    private String interior;

    /** 安全配置（多项目以 " / " 分隔） */
    private String safety;

    /** 娱乐配置（多项目以 " / " 分隔） */
    private String entertainment;
}

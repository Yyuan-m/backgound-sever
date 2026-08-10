package com.car.rental.module.statistics.service.impl;

import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.mapper.CustomerInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.mapper.FinanceRecordMapper;
import com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper;
import com.car.rental.module.car.mapper.CarMaintenanceMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link StatisticsServiceImpl}.
 */
@Generated
public class StatisticsServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'statisticsServiceImpl'.
   */
  private static BeanInstanceSupplier<StatisticsServiceImpl> getStatisticsServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<StatisticsServiceImpl>forConstructor(CustomerOrderMapper.class, CarInfoMapper.class, FinanceRecordMapper.class, CustomerInfoMapper.class, AfterSalesComplaintMapper.class, CarMaintenanceMapper.class)
            .withGenerator((registeredBean, args) -> new StatisticsServiceImpl(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5)));
  }

  /**
   * Get the bean definition for 'statisticsServiceImpl'.
   */
  public static BeanDefinition getStatisticsServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(StatisticsServiceImpl.class);
    beanDefinition.setInstanceSupplier(getStatisticsServiceImplInstanceSupplier());
    return beanDefinition;
  }
}

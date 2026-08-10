package com.car.rental.module.statistics.service.impl;

import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.mapper.FinanceRecordMapper;
import com.car.rental.module.car.mapper.CarMaintenanceMapper;
import com.car.rental.module.statistics.mapper.CostRecordMapper;
import com.car.rental.module.statistics.mapper.ReconciliationMapper;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;

/**
 * Bean definitions for {@link FinanceStatsServiceImpl}.
 */
@Generated
public class FinanceStatsServiceImpl__BeanDefinitions {
  /**
   * Get the bean instance supplier for 'financeStatsServiceImpl'.
   */
  private static BeanInstanceSupplier<FinanceStatsServiceImpl> getFinanceStatsServiceImplInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<FinanceStatsServiceImpl>forConstructor(FinanceRecordMapper.class, CostRecordMapper.class, CarMaintenanceMapper.class, ReconciliationMapper.class, CarInfoMapper.class, CustomerOrderMapper.class)
            .withGenerator((registeredBean, args) -> new FinanceStatsServiceImpl(args.get(0), args.get(1), args.get(2), args.get(3), args.get(4), args.get(5)));
  }

  /**
   * Get the bean definition for 'financeStatsServiceImpl'.
   */
  public static BeanDefinition getFinanceStatsServiceImplBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(FinanceStatsServiceImpl.class);
    beanDefinition.setInstanceSupplier(getFinanceStatsServiceImplInstanceSupplier());
    return beanDefinition;
  }
}

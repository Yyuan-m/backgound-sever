package com.car.rental;

import cn.hutool.extra.spring.SpringUtil__BeanDefinitions;
import com.baomidou.mybatisplus.autoconfigure.DdlAutoConfiguration__BeanDefinitions;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration__BeanDefinitions;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration__BeanDefinitions;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties__BeanDefinitions;
import com.car.rental.common.exception.GlobalExceptionHandler__BeanDefinitions;
import com.car.rental.common.security.JwtTokenFilter__BeanDefinitions;
import com.car.rental.common.security.OperationLogAspect__BeanDefinitions;
import com.car.rental.common.security.PermissionAspect__BeanDefinitions;
import com.car.rental.common.util.JwtUtil__BeanDefinitions;
import com.car.rental.common.util.SecurityUtil__BeanDefinitions;
import com.car.rental.config.GlobalCorsConfig__BeanDefinitions;
import com.car.rental.config.JacksonConfig__BeanDefinitions;
import com.car.rental.config.MybatisPlusConfig__BeanDefinitions;
import com.car.rental.config.RedisConfig__BeanDefinitions;
import com.car.rental.config.SecurityConfig__BeanDefinitions;
import com.car.rental.config.WebMvcConfig__BeanDefinitions;
import com.car.rental.mapper.AnnouncementMapper;
import com.car.rental.mapper.CarInfoMapper;
import com.car.rental.mapper.CustomerInfoMapper;
import com.car.rental.mapper.CustomerOrderMapper;
import com.car.rental.mapper.FinanceRecordMapper;
import com.car.rental.mapper.SysConfigMapper;
import com.car.rental.mapper.SysMenuMapper;
import com.car.rental.mapper.SysRoleMapper;
import com.car.rental.mapper.SysThemeConfigMapper;
import com.car.rental.mapper.SysUserMapper;
import com.car.rental.mapper.SysUserRoleMapper;
import com.car.rental.module.after_sales.controller.AfterSalesComplaintController__BeanDefinitions;
import com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper;
import com.car.rental.module.after_sales.service.impl.AfterSalesComplaintServiceImpl__BeanDefinitions;
import com.car.rental.module.auth.controller.AuthController__BeanDefinitions;
import com.car.rental.module.auth.controller.ProfileController__BeanDefinitions;
import com.car.rental.module.auth.service.impl.AuthServiceImpl__BeanDefinitions;
import com.car.rental.module.auth.service.impl.ProfileServiceImpl__BeanDefinitions;
import com.car.rental.module.car.controller.CarController__BeanDefinitions;
import com.car.rental.module.car.controller.CarDocumentController__BeanDefinitions;
import com.car.rental.module.car.controller.CarImageController__BeanDefinitions;
import com.car.rental.module.car.controller.CarMaintenanceController__BeanDefinitions;
import com.car.rental.module.car.controller.CarViolationController__BeanDefinitions;
import com.car.rental.module.car.controller.GpsTrackController__BeanDefinitions;
import com.car.rental.module.car.mapper.CarConfigMapper;
import com.car.rental.module.car.mapper.CarDocumentMapper;
import com.car.rental.module.car.mapper.CarImageMapper;
import com.car.rental.module.car.mapper.CarMaintenanceMapper;
import com.car.rental.module.car.mapper.CarViolationMapper;
import com.car.rental.module.car.mapper.GpsTrackMapper;
import com.car.rental.module.car.service.impl.CarDocumentServiceImpl__BeanDefinitions;
import com.car.rental.module.car.service.impl.CarImageServiceImpl__BeanDefinitions;
import com.car.rental.module.car.service.impl.CarMaintenanceServiceImpl__BeanDefinitions;
import com.car.rental.module.car.service.impl.CarServiceImpl__BeanDefinitions;
import com.car.rental.module.car.service.impl.CarViolationServiceImpl__BeanDefinitions;
import com.car.rental.module.car.service.impl.GpsTrackServiceImpl__BeanDefinitions;
import com.car.rental.module.customer.controller.CustomerController__BeanDefinitions;
import com.car.rental.module.customer.service.impl.CustomerServiceImpl__BeanDefinitions;
import com.car.rental.module.marketing.controller.CouponController__BeanDefinitions;
import com.car.rental.module.marketing.controller.CustomerCouponController__BeanDefinitions;
import com.car.rental.module.marketing.mapper.CouponCarMapper;
import com.car.rental.module.marketing.mapper.CouponMapper;
import com.car.rental.module.marketing.mapper.MemberCouponMapper;
import com.car.rental.module.marketing.service.impl.CouponServiceImpl__BeanDefinitions;
import com.car.rental.module.marketing.service.impl.CustomerCouponServiceImpl__BeanDefinitions;
import com.car.rental.module.order.controller.OrderController__BeanDefinitions;
import com.car.rental.module.order.service.impl.OrderServiceImpl__BeanDefinitions;
import com.car.rental.module.statistics.controller.CostController__BeanDefinitions;
import com.car.rental.module.statistics.controller.FinanceController__BeanDefinitions;
import com.car.rental.module.statistics.controller.InvoiceController__BeanDefinitions;
import com.car.rental.module.statistics.controller.ReconciliationController__BeanDefinitions;
import com.car.rental.module.statistics.controller.StatisticsController__BeanDefinitions;
import com.car.rental.module.statistics.mapper.CostRecordMapper;
import com.car.rental.module.statistics.mapper.InvoiceMapper;
import com.car.rental.module.statistics.mapper.ReconciliationMapper;
import com.car.rental.module.statistics.service.impl.CostServiceImpl__BeanDefinitions;
import com.car.rental.module.statistics.service.impl.FinanceServiceImpl__BeanDefinitions;
import com.car.rental.module.statistics.service.impl.FinanceStatsServiceImpl__BeanDefinitions;
import com.car.rental.module.statistics.service.impl.InvoiceServiceImpl__BeanDefinitions;
import com.car.rental.module.statistics.service.impl.ReconciliationServiceImpl__BeanDefinitions;
import com.car.rental.module.statistics.service.impl.StatisticsServiceImpl__BeanDefinitions;
import com.car.rental.module.system.controller.AnnouncementController__BeanDefinitions;
import com.car.rental.module.system.controller.CarouselController__BeanDefinitions;
import com.car.rental.module.system.controller.DictController__BeanDefinitions;
import com.car.rental.module.system.controller.MenuController__BeanDefinitions;
import com.car.rental.module.system.controller.OperationLogController__BeanDefinitions;
import com.car.rental.module.system.controller.RoleController__BeanDefinitions;
import com.car.rental.module.system.controller.SysConfigController__BeanDefinitions;
import com.car.rental.module.system.controller.SysFileController__BeanDefinitions;
import com.car.rental.module.system.controller.ThemeController__BeanDefinitions;
import com.car.rental.module.system.controller.UserController__BeanDefinitions;
import com.car.rental.module.system.mapper.CarouselMapper;
import com.car.rental.module.system.mapper.OperationLogMapper;
import com.car.rental.module.system.mapper.SysDictDataMapper;
import com.car.rental.module.system.mapper.SysDictTypeMapper;
import com.car.rental.module.system.mapper.SysFileMapper;
import com.car.rental.module.system.service.impl.AnnouncementServiceImpl__BeanDefinitions;
import com.car.rental.module.system.service.impl.CarouselServiceImpl__BeanDefinitions;
import com.car.rental.module.system.service.impl.DictServiceImpl__BeanDefinitions;
import com.car.rental.module.system.service.impl.MenuServiceImpl__BeanDefinitions;
import com.car.rental.module.system.service.impl.OperationLogServiceImpl__BeanDefinitions;
import com.car.rental.module.system.service.impl.RoleServiceImpl__BeanDefinitions;
import com.car.rental.module.system.service.impl.SysConfigServiceImpl__BeanDefinitions;
import com.car.rental.module.system.service.impl.SysFileServiceImpl__BeanDefinitions;
import com.car.rental.module.system.service.impl.ThemeServiceImpl__BeanDefinitions;
import com.car.rental.module.system.service.impl.UserServiceImpl__BeanDefinitions;
import com.car.rental.module.system.util.OperationLogExporter__BeanDefinitions;
import com.car.rental.module.upload.controller.UploadController__BeanDefinitions;
import java.lang.Class;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer__BeanDefinitions;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator__BeanDefinitions;
import org.springframework.aot.generate.Generated;
import org.springframework.beans.factory.aot.BeanInstanceSupplier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages__BeanDefinitions;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.context.LifecycleProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.DataSourceJmxConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.JdbcClientAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.NamedParameterJdbcTemplateConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.netty.NettyAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.netty.NettyProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.reactor.ReactorAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.reactor.ReactorProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.security.SecurityProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.sql.init.DataSourceInitializationConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.ssl.SslProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.task.TaskExecutorConfigurations__BeanDefinitions;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.task.TaskSchedulingConfigurations__BeanDefinitions;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.transaction.TransactionProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.ServerProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.WebProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties__BeanDefinitions;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration__BeanDefinitions;
import org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration__BeanDefinitions;
import org.springframework.boot.context.properties.BoundConfigurationProperties__BeanDefinitions;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinder__BeanDefinitions;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor__BeanDefinitions;
import org.springframework.boot.jackson.JsonMixinModuleEntries__BeanDefinitions;
import org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer__BeanDefinitions;
import org.springframework.boot.validation.beanvalidation.MethodValidationExcludeFilter__BeanDefinitions;
import org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor__BeanDefinitions;
import org.springframework.boot.web.server.WebServerFactoryCustomizerBeanPostProcessor__BeanDefinitions;
import org.springframework.context.event.DefaultEventListenerFactory__BeanDefinitions;
import org.springframework.context.event.EventListenerMethodProcessor__BeanDefinitions;
import org.springframework.core.ResolvableType;
import org.springframework.data.redis.core.RedisKeyValueAdapter__BeanDefinitions;
import org.springframework.data.redis.core.RedisKeyValueTemplate__BeanDefinitions;
import org.springframework.data.redis.core.convert.KeyspaceConfiguration__BeanDefinitions;
import org.springframework.data.redis.core.convert.MappingConfiguration__BeanDefinitions;
import org.springframework.data.redis.core.convert.MappingRedisConverter__BeanDefinitions;
import org.springframework.data.redis.core.convert.RedisCustomConversions__BeanDefinitions;
import org.springframework.data.redis.core.convert.ReferenceResolverImpl__BeanDefinitions;
import org.springframework.data.redis.core.index.IndexConfiguration__BeanDefinitions;
import org.springframework.data.redis.core.mapping.RedisMappingContext__BeanDefinitions;
import org.springframework.data.web.config.ProjectingArgumentResolverRegistrar__BeanDefinitions;
import org.springframework.data.web.config.SpringDataJacksonConfiguration__BeanDefinitions;
import org.springframework.data.web.config.SpringDataWebConfiguration__BeanDefinitions;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration__BeanDefinitions;
import org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration__BeanDefinitions;
import org.springframework.security.config.annotation.method.configuration.PrePostMethodSecurityConfiguration__BeanDefinitions;
import org.springframework.security.config.annotation.web.configuration.HttpSecurityConfiguration__BeanDefinitions;
import org.springframework.security.config.annotation.web.configuration.WebMvcSecurityConfiguration__BeanDefinitions;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration__BeanDefinitions;
import org.springframework.security.web.access.HandlerMappingIntrospectorRequestTransformer__BeanDefinitions;
import org.springframework.transaction.annotation.AbstractTransactionManagementConfiguration__BeanDefinitions;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration__BeanDefinitions;

/**
 * Register bean definitions for the bean factory.
 */
@Generated
public class CarRentalApplication__BeanFactoryRegistrations {
  /**
   * Get the bean instance supplier for 'announcementMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getAnnouncementMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'announcementMapper'.
   */
  private static BeanDefinition getAnnouncementMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.AnnouncementMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", AnnouncementMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getAnnouncementMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'carInfoMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCarInfoMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'carInfoMapper'.
   */
  private static BeanDefinition getCarInfoMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.CarInfoMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CarInfoMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCarInfoMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'customerInfoMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCustomerInfoMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'customerInfoMapper'.
   */
  private static BeanDefinition getCustomerInfoMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.CustomerInfoMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CustomerInfoMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCustomerInfoMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'customerOrderMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCustomerOrderMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'customerOrderMapper'.
   */
  private static BeanDefinition getCustomerOrderMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.CustomerOrderMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CustomerOrderMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCustomerOrderMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'financeRecordMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getFinanceRecordMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'financeRecordMapper'.
   */
  private static BeanDefinition getFinanceRecordMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.FinanceRecordMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", FinanceRecordMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getFinanceRecordMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sysConfigMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getSysConfigMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysConfigMapper'.
   */
  private static BeanDefinition getSysConfigMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.SysConfigMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", SysConfigMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getSysConfigMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sysMenuMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getSysMenuMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysMenuMapper'.
   */
  private static BeanDefinition getSysMenuMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.SysMenuMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", SysMenuMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getSysMenuMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sysRoleMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getSysRoleMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysRoleMapper'.
   */
  private static BeanDefinition getSysRoleMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.SysRoleMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", SysRoleMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getSysRoleMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sysThemeConfigMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getSysThemeConfigMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysThemeConfigMapper'.
   */
  private static BeanDefinition getSysThemeConfigMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.SysThemeConfigMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", SysThemeConfigMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getSysThemeConfigMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sysUserMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getSysUserMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysUserMapper'.
   */
  private static BeanDefinition getSysUserMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.SysUserMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", SysUserMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getSysUserMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sysUserRoleMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getSysUserRoleMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysUserRoleMapper'.
   */
  private static BeanDefinition getSysUserRoleMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.mapper.SysUserRoleMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", SysUserRoleMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getSysUserRoleMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'afterSalesComplaintMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getAfterSalesComplaintMapperInstanceSupplier(
      ) {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'afterSalesComplaintMapper'.
   */
  private static BeanDefinition getAfterSalesComplaintMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.after_sales.mapper.AfterSalesComplaintMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", AfterSalesComplaintMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getAfterSalesComplaintMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'carConfigMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCarConfigMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'carConfigMapper'.
   */
  private static BeanDefinition getCarConfigMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.car.mapper.CarConfigMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CarConfigMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCarConfigMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'carDocumentMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCarDocumentMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'carDocumentMapper'.
   */
  private static BeanDefinition getCarDocumentMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.car.mapper.CarDocumentMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CarDocumentMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCarDocumentMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'carImageMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCarImageMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'carImageMapper'.
   */
  private static BeanDefinition getCarImageMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.car.mapper.CarImageMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CarImageMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCarImageMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'carMaintenanceMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCarMaintenanceMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'carMaintenanceMapper'.
   */
  private static BeanDefinition getCarMaintenanceMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.car.mapper.CarMaintenanceMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CarMaintenanceMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCarMaintenanceMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'carViolationMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCarViolationMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'carViolationMapper'.
   */
  private static BeanDefinition getCarViolationMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.car.mapper.CarViolationMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CarViolationMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCarViolationMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'gpsTrackMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getGpsTrackMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'gpsTrackMapper'.
   */
  private static BeanDefinition getGpsTrackMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.car.mapper.GpsTrackMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", GpsTrackMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getGpsTrackMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'couponCarMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCouponCarMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'couponCarMapper'.
   */
  private static BeanDefinition getCouponCarMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.marketing.mapper.CouponCarMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CouponCarMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCouponCarMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'couponMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCouponMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'couponMapper'.
   */
  private static BeanDefinition getCouponMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.marketing.mapper.CouponMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CouponMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCouponMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'memberCouponMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getMemberCouponMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'memberCouponMapper'.
   */
  private static BeanDefinition getMemberCouponMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.marketing.mapper.MemberCouponMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", MemberCouponMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getMemberCouponMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'costRecordMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCostRecordMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'costRecordMapper'.
   */
  private static BeanDefinition getCostRecordMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.statistics.mapper.CostRecordMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CostRecordMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCostRecordMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'invoiceMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getInvoiceMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'invoiceMapper'.
   */
  private static BeanDefinition getInvoiceMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.statistics.mapper.InvoiceMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", InvoiceMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getInvoiceMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'reconciliationMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getReconciliationMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'reconciliationMapper'.
   */
  private static BeanDefinition getReconciliationMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.statistics.mapper.ReconciliationMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", ReconciliationMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getReconciliationMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'carouselMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getCarouselMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'carouselMapper'.
   */
  private static BeanDefinition getCarouselMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.system.mapper.CarouselMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", CarouselMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getCarouselMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'operationLogMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getOperationLogMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'operationLogMapper'.
   */
  private static BeanDefinition getOperationLogMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.system.mapper.OperationLogMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", OperationLogMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getOperationLogMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sysDictDataMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getSysDictDataMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysDictDataMapper'.
   */
  private static BeanDefinition getSysDictDataMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.system.mapper.SysDictDataMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", SysDictDataMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getSysDictDataMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sysDictTypeMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getSysDictTypeMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysDictTypeMapper'.
   */
  private static BeanDefinition getSysDictTypeMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.system.mapper.SysDictTypeMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", SysDictTypeMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getSysDictTypeMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Get the bean instance supplier for 'sysFileMapper'.
   */
  private static BeanInstanceSupplier<MapperFactoryBean> getSysFileMapperInstanceSupplier() {
    return BeanInstanceSupplier.<MapperFactoryBean>forConstructor(Class.class)
            .withGenerator((registeredBean, args) -> new MapperFactoryBean(args.get(0)));
  }

  /**
   * Get the bean definition for 'sysFileMapper'.
   */
  private static BeanDefinition getSysFileMapperBeanDefinition() {
    RootBeanDefinition beanDefinition = new RootBeanDefinition(MapperFactoryBean.class);
    beanDefinition.setTargetType(ResolvableType.forClass(MapperFactoryBean.class));
    beanDefinition.setLazyInit(false);
    beanDefinition.getConstructorArgumentValues().addGenericArgumentValue("com.car.rental.module.system.mapper.SysFileMapper");
    beanDefinition.getPropertyValues().addPropertyValue("mapperInterface", SysFileMapper.class);
    beanDefinition.getPropertyValues().addPropertyValue("addToConfig", true);
    beanDefinition.setInstanceSupplier(getSysFileMapperInstanceSupplier());
    return beanDefinition;
  }

  /**
   * Register the bean definitions.
   */
  public void registerBeanDefinitions(DefaultListableBeanFactory beanFactory) {
    beanFactory.registerBeanDefinition("org.springframework.context.event.internalEventListenerProcessor", EventListenerMethodProcessor__BeanDefinitions.getInternalEventListenerProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.context.event.internalEventListenerFactory", DefaultEventListenerFactory__BeanDefinitions.getInternalEventListenerFactoryBeanDefinition());
    beanFactory.registerBeanDefinition("carRentalApplication", CarRentalApplication__BeanDefinitions.getCarRentalApplicationBeanDefinition());
    beanFactory.registerBeanDefinition("globalExceptionHandler", GlobalExceptionHandler__BeanDefinitions.getGlobalExceptionHandlerBeanDefinition());
    beanFactory.registerBeanDefinition("jwtTokenFilter", JwtTokenFilter__BeanDefinitions.getJwtTokenFilterBeanDefinition());
    beanFactory.registerBeanDefinition("operationLogAspect", OperationLogAspect__BeanDefinitions.getOperationLogAspectBeanDefinition());
    beanFactory.registerBeanDefinition("permissionAspect", PermissionAspect__BeanDefinitions.getPermissionAspectBeanDefinition());
    beanFactory.registerBeanDefinition("jwtUtil", JwtUtil__BeanDefinitions.getJwtUtilBeanDefinition());
    beanFactory.registerBeanDefinition("securityUtil", SecurityUtil__BeanDefinitions.getSecurityUtilBeanDefinition());
    beanFactory.registerBeanDefinition("globalCorsConfig", GlobalCorsConfig__BeanDefinitions.getGlobalCorsConfigBeanDefinition());
    beanFactory.registerBeanDefinition("jacksonConfig", JacksonConfig__BeanDefinitions.getJacksonConfigBeanDefinition());
    beanFactory.registerBeanDefinition("mybatisPlusConfig", MybatisPlusConfig__BeanDefinitions.getMybatisPlusConfigBeanDefinition());
    beanFactory.registerBeanDefinition("redisConfig", RedisConfig__BeanDefinitions.getRedisConfigBeanDefinition());
    beanFactory.registerBeanDefinition("securityConfig", SecurityConfig__BeanDefinitions.getSecurityConfigBeanDefinition());
    beanFactory.registerBeanDefinition("webMvcConfig", WebMvcConfig__BeanDefinitions.getWebMvcConfigBeanDefinition());
    beanFactory.registerBeanDefinition("afterSalesComplaintController", AfterSalesComplaintController__BeanDefinitions.getAfterSalesComplaintControllerBeanDefinition());
    beanFactory.registerBeanDefinition("afterSalesComplaintServiceImpl", AfterSalesComplaintServiceImpl__BeanDefinitions.getAfterSalesComplaintServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("authController", AuthController__BeanDefinitions.getAuthControllerBeanDefinition());
    beanFactory.registerBeanDefinition("profileController", ProfileController__BeanDefinitions.getProfileControllerBeanDefinition());
    beanFactory.registerBeanDefinition("authServiceImpl", AuthServiceImpl__BeanDefinitions.getAuthServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("profileServiceImpl", ProfileServiceImpl__BeanDefinitions.getProfileServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("carController", CarController__BeanDefinitions.getCarControllerBeanDefinition());
    beanFactory.registerBeanDefinition("carDocumentController", CarDocumentController__BeanDefinitions.getCarDocumentControllerBeanDefinition());
    beanFactory.registerBeanDefinition("carImageController", CarImageController__BeanDefinitions.getCarImageControllerBeanDefinition());
    beanFactory.registerBeanDefinition("carMaintenanceController", CarMaintenanceController__BeanDefinitions.getCarMaintenanceControllerBeanDefinition());
    beanFactory.registerBeanDefinition("carViolationController", CarViolationController__BeanDefinitions.getCarViolationControllerBeanDefinition());
    beanFactory.registerBeanDefinition("gpsTrackController", GpsTrackController__BeanDefinitions.getGpsTrackControllerBeanDefinition());
    beanFactory.registerBeanDefinition("carDocumentServiceImpl", CarDocumentServiceImpl__BeanDefinitions.getCarDocumentServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("carImageServiceImpl", CarImageServiceImpl__BeanDefinitions.getCarImageServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("carMaintenanceServiceImpl", CarMaintenanceServiceImpl__BeanDefinitions.getCarMaintenanceServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("carServiceImpl", CarServiceImpl__BeanDefinitions.getCarServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("carViolationServiceImpl", CarViolationServiceImpl__BeanDefinitions.getCarViolationServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("gpsTrackServiceImpl", GpsTrackServiceImpl__BeanDefinitions.getGpsTrackServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("customerController", CustomerController__BeanDefinitions.getCustomerControllerBeanDefinition());
    beanFactory.registerBeanDefinition("customerServiceImpl", CustomerServiceImpl__BeanDefinitions.getCustomerServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("couponController", CouponController__BeanDefinitions.getCouponControllerBeanDefinition());
    beanFactory.registerBeanDefinition("customerCouponController", CustomerCouponController__BeanDefinitions.getCustomerCouponControllerBeanDefinition());
    beanFactory.registerBeanDefinition("couponServiceImpl", CouponServiceImpl__BeanDefinitions.getCouponServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("customerCouponServiceImpl", CustomerCouponServiceImpl__BeanDefinitions.getCustomerCouponServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("orderController", OrderController__BeanDefinitions.getOrderControllerBeanDefinition());
    beanFactory.registerBeanDefinition("orderServiceImpl", OrderServiceImpl__BeanDefinitions.getOrderServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("costController", CostController__BeanDefinitions.getCostControllerBeanDefinition());
    beanFactory.registerBeanDefinition("financeController", FinanceController__BeanDefinitions.getFinanceControllerBeanDefinition());
    beanFactory.registerBeanDefinition("invoiceController", InvoiceController__BeanDefinitions.getInvoiceControllerBeanDefinition());
    beanFactory.registerBeanDefinition("reconciliationController", ReconciliationController__BeanDefinitions.getReconciliationControllerBeanDefinition());
    beanFactory.registerBeanDefinition("statisticsController", StatisticsController__BeanDefinitions.getStatisticsControllerBeanDefinition());
    beanFactory.registerBeanDefinition("costServiceImpl", CostServiceImpl__BeanDefinitions.getCostServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("financeServiceImpl", FinanceServiceImpl__BeanDefinitions.getFinanceServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("financeStatsServiceImpl", FinanceStatsServiceImpl__BeanDefinitions.getFinanceStatsServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("invoiceServiceImpl", InvoiceServiceImpl__BeanDefinitions.getInvoiceServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("reconciliationServiceImpl", ReconciliationServiceImpl__BeanDefinitions.getReconciliationServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("statisticsServiceImpl", StatisticsServiceImpl__BeanDefinitions.getStatisticsServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("announcementController", AnnouncementController__BeanDefinitions.getAnnouncementControllerBeanDefinition());
    beanFactory.registerBeanDefinition("carouselController", CarouselController__BeanDefinitions.getCarouselControllerBeanDefinition());
    beanFactory.registerBeanDefinition("dictController", DictController__BeanDefinitions.getDictControllerBeanDefinition());
    beanFactory.registerBeanDefinition("menuController", MenuController__BeanDefinitions.getMenuControllerBeanDefinition());
    beanFactory.registerBeanDefinition("operationLogController", OperationLogController__BeanDefinitions.getOperationLogControllerBeanDefinition());
    beanFactory.registerBeanDefinition("roleController", RoleController__BeanDefinitions.getRoleControllerBeanDefinition());
    beanFactory.registerBeanDefinition("sysConfigController", SysConfigController__BeanDefinitions.getSysConfigControllerBeanDefinition());
    beanFactory.registerBeanDefinition("sysFileController", SysFileController__BeanDefinitions.getSysFileControllerBeanDefinition());
    beanFactory.registerBeanDefinition("themeController", ThemeController__BeanDefinitions.getThemeControllerBeanDefinition());
    beanFactory.registerBeanDefinition("userController", UserController__BeanDefinitions.getUserControllerBeanDefinition());
    beanFactory.registerBeanDefinition("announcementServiceImpl", AnnouncementServiceImpl__BeanDefinitions.getAnnouncementServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("carouselServiceImpl", CarouselServiceImpl__BeanDefinitions.getCarouselServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("dictServiceImpl", DictServiceImpl__BeanDefinitions.getDictServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("menuServiceImpl", MenuServiceImpl__BeanDefinitions.getMenuServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("operationLogServiceImpl", OperationLogServiceImpl__BeanDefinitions.getOperationLogServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("roleServiceImpl", RoleServiceImpl__BeanDefinitions.getRoleServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("sysConfigServiceImpl", SysConfigServiceImpl__BeanDefinitions.getSysConfigServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("sysFileServiceImpl", SysFileServiceImpl__BeanDefinitions.getSysFileServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("themeServiceImpl", ThemeServiceImpl__BeanDefinitions.getThemeServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("userServiceImpl", UserServiceImpl__BeanDefinitions.getUserServiceImplBeanDefinition());
    beanFactory.registerBeanDefinition("operationLogExporter", OperationLogExporter__BeanDefinitions.getOperationLogExporterBeanDefinition());
    beanFactory.registerBeanDefinition("uploadController", UploadController__BeanDefinitions.getUploadControllerBeanDefinition());
    beanFactory.registerBeanDefinition("corsFilter", GlobalCorsConfig__BeanDefinitions.getCorsFilterBeanDefinition());
    beanFactory.registerBeanDefinition("jacksonCustomizer", JacksonConfig__BeanDefinitions.getJacksonCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("mybatisPlusInterceptor", MybatisPlusConfig__BeanDefinitions.getMybatisPlusInterceptorBeanDefinition());
    beanFactory.registerBeanDefinition("redisTemplate", RedisConfig__BeanDefinitions.getRedisTemplateBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.security.config.annotation.configuration.ObjectPostProcessorConfiguration", ObjectPostProcessorConfiguration__BeanDefinitions.getObjectPostProcessorConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("objectPostProcessor", ObjectPostProcessorConfiguration__BeanDefinitions.getObjectPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration", AuthenticationConfiguration__BeanDefinitions.getAuthenticationConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("authenticationManagerBuilder", AuthenticationConfiguration__BeanDefinitions.getAuthenticationManagerBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("enableGlobalAuthenticationAutowiredConfigurer", AuthenticationConfiguration__BeanDefinitions.getEnableGlobalAuthenticationAutowiredConfigurerBeanDefinition());
    beanFactory.registerBeanDefinition("initializeUserDetailsBeanManagerConfigurer", AuthenticationConfiguration__BeanDefinitions.getInitializeUserDetailsBeanManagerConfigurerBeanDefinition());
    beanFactory.registerBeanDefinition("initializeAuthenticationProviderBeanManagerConfigurer", AuthenticationConfiguration__BeanDefinitions.getInitializeAuthenticationProviderBeanManagerConfigurerBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration", WebSecurityConfiguration__BeanDefinitions.getWebSecurityConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("delegatingApplicationListener", WebSecurityConfiguration__BeanDefinitions.getDelegatingApplicationListenerBeanDefinition());
    beanFactory.registerBeanDefinition("webSecurityExpressionHandler", WebSecurityConfiguration__BeanDefinitions.getWebSecurityExpressionHandlerBeanDefinition());
    beanFactory.registerBeanDefinition("privilegeEvaluator", WebSecurityConfiguration__BeanDefinitions.getPrivilegeEvaluatorBeanDefinition());
    beanFactory.registerBeanDefinition("conversionServicePostProcessor", WebSecurityConfiguration__BeanDefinitions.getConversionServicePostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.security.config.annotation.web.configuration.WebMvcSecurityConfiguration", WebMvcSecurityConfiguration__BeanDefinitions.getWebMvcSecurityConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("requestDataValueProcessor", WebMvcSecurityConfiguration__BeanDefinitions.getRequestDataValueProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("springSecurityHandlerMappingIntrospectorBeanDefinitionRegistryPostProcessor", WebMvcSecurityConfiguration__BeanDefinitions.getSpringSecurityHandlerMappingIntrospectorBeanDefinitionRegistryPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.security.config.annotation.web.configuration.HttpSecurityConfiguration", HttpSecurityConfiguration__BeanDefinitions.getHttpSecurityConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.security.config.annotation.web.configuration.HttpSecurityConfiguration.httpSecurity", HttpSecurityConfiguration__BeanDefinitions.getHttpSecurityBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.security.config.annotation.method.configuration.PrePostMethodSecurityConfiguration", PrePostMethodSecurityConfiguration__BeanDefinitions.getPrePostMethodSecurityConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("preFilterAuthorizationMethodInterceptor", PrePostMethodSecurityConfiguration__BeanDefinitions.getPreFilterAuthorizationMethodInterceptorBeanDefinition());
    beanFactory.registerBeanDefinition("preAuthorizeAuthorizationMethodInterceptor", PrePostMethodSecurityConfiguration__BeanDefinitions.getPreAuthorizeAuthorizationMethodInterceptorBeanDefinition());
    beanFactory.registerBeanDefinition("postAuthorizeAuthorizationMethodInterceptor", PrePostMethodSecurityConfiguration__BeanDefinitions.getPostAuthorizeAuthorizationMethodInterceptorBeanDefinition());
    beanFactory.registerBeanDefinition("postFilterAuthorizationMethodInterceptor", PrePostMethodSecurityConfiguration__BeanDefinitions.getPostFilterAuthorizationMethodInterceptorBeanDefinition());
    beanFactory.registerBeanDefinition("securityFilterChain", SecurityConfig__BeanDefinitions.getSecurityFilterChainBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.aop.config.internalAutoProxyCreator", AnnotationAwareAspectJAutoProxyCreator__BeanDefinitions.getInternalAutoProxyCreatorBeanDefinition());
    beanFactory.registerBeanDefinition("preFilterAuthorizationAdvisor", PrePostMethodSecurityConfiguration__BeanDefinitions.getPreFilterAuthorizationAdvisorBeanDefinition());
    beanFactory.registerBeanDefinition("preAuthorizeAuthorizationAdvisor", PrePostMethodSecurityConfiguration__BeanDefinitions.getPreAuthorizeAuthorizationAdvisorBeanDefinition());
    beanFactory.registerBeanDefinition("postFilterAuthorizationAdvisor", PrePostMethodSecurityConfiguration__BeanDefinitions.getPostFilterAuthorizationAdvisorBeanDefinition());
    beanFactory.registerBeanDefinition("postAuthorizeAuthorizationAdvisor", PrePostMethodSecurityConfiguration__BeanDefinitions.getPostAuthorizeAuthorizationAdvisorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.AutoConfigurationPackages", AutoConfigurationPackages__BeanDefinitions.BasePackages.getAutoConfigurationPackagesBeanDefinition());
    beanFactory.registerBeanDefinition("com.car.rental.CarRentalApplication#MapperScannerRegistrar#0", MapperScannerConfigurer__BeanDefinitions.getCarRentalApplicationMapperScannerRegistrarBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration", PropertyPlaceholderAutoConfiguration__BeanDefinitions.getPropertyPlaceholderAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("propertySourcesPlaceholderConfigurer", PropertyPlaceholderAutoConfiguration__BeanDefinitions.getPropertySourcesPlaceholderConfigurerBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.ssl.SslAutoConfiguration", SslAutoConfiguration__BeanDefinitions.getSslAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("fileWatcher", SslAutoConfiguration__BeanDefinitions.getFileWatcherBeanDefinition());
    beanFactory.registerBeanDefinition("sslPropertiesSslBundleRegistrar", SslAutoConfiguration__BeanDefinitions.getSslPropertiesSslBundleRegistrarBeanDefinition());
    beanFactory.registerBeanDefinition("sslBundleRegistry", SslAutoConfiguration__BeanDefinitions.getSslBundleRegistryBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor", ConfigurationPropertiesBindingPostProcessor__BeanDefinitions.getConfigurationPropertiesBindingPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.context.internalConfigurationPropertiesBinder", ConfigurationPropertiesBinder__BeanDefinitions.ConfigurationPropertiesBinderFactory.getInternalConfigurationPropertiesBinderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.context.properties.BoundConfigurationProperties", BoundConfigurationProperties__BeanDefinitions.getBoundConfigurationPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.context.properties.EnableConfigurationPropertiesRegistrar.methodValidationExcludeFilter", MethodValidationExcludeFilter__BeanDefinitions.getMethodValidationExcludeFilterBeanDefinition());
    beanFactory.registerBeanDefinition("spring.ssl-org.springframework.boot.autoconfigure.ssl.SslProperties", SslProperties__BeanDefinitions.getSslPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration$TomcatWebSocketConfiguration", WebSocketServletAutoConfiguration__BeanDefinitions.TomcatWebSocketConfiguration.getTomcatWebSocketConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("websocketServletWebServerCustomizer", WebSocketServletAutoConfiguration__BeanDefinitions.TomcatWebSocketConfiguration.getWebsocketServletWebServerCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.websocket.servlet.WebSocketServletAutoConfiguration", WebSocketServletAutoConfiguration__BeanDefinitions.getWebSocketServletAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryConfiguration$EmbeddedTomcat", ServletWebServerFactoryConfiguration__BeanDefinitions.EmbeddedTomcat.getEmbeddedTomcatBeanDefinition());
    beanFactory.registerBeanDefinition("tomcatServletWebServerFactory", ServletWebServerFactoryConfiguration__BeanDefinitions.EmbeddedTomcat.getTomcatServletWebServerFactoryBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration", ServletWebServerFactoryAutoConfiguration__BeanDefinitions.getServletWebServerFactoryAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("servletWebServerFactoryCustomizer", ServletWebServerFactoryAutoConfiguration__BeanDefinitions.getServletWebServerFactoryCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("tomcatServletWebServerFactoryCustomizer", ServletWebServerFactoryAutoConfiguration__BeanDefinitions.getTomcatServletWebServerFactoryCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("server-org.springframework.boot.autoconfigure.web.ServerProperties", ServerProperties__BeanDefinitions.getServerPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("webServerFactoryCustomizerBeanPostProcessor", WebServerFactoryCustomizerBeanPostProcessor__BeanDefinitions.getWebServerFactoryCustomizerBeanPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("errorPageRegistrarBeanPostProcessor", ErrorPageRegistrarBeanPostProcessor__BeanDefinitions.getErrorPageRegistrarBeanPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration$DispatcherServletConfiguration", DispatcherServletAutoConfiguration__BeanDefinitions.DispatcherServletConfiguration.getDispatcherServletConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("dispatcherServlet", DispatcherServletAutoConfiguration__BeanDefinitions.DispatcherServletConfiguration.getDispatcherServletBeanDefinition());
    beanFactory.registerBeanDefinition("spring.mvc-org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties", WebMvcProperties__BeanDefinitions.getWebMvcPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration$DispatcherServletRegistrationConfiguration", DispatcherServletAutoConfiguration__BeanDefinitions.DispatcherServletRegistrationConfiguration.getDispatcherServletRegistrationConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("dispatcherServletRegistration", DispatcherServletAutoConfiguration__BeanDefinitions.DispatcherServletRegistrationConfiguration.getDispatcherServletRegistrationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration", DispatcherServletAutoConfiguration__BeanDefinitions.getDispatcherServletAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.task.TaskExecutorConfigurations$ThreadPoolTaskExecutorBuilderConfiguration", TaskExecutorConfigurations__BeanDefinitions.ThreadPoolTaskExecutorBuilderConfiguration.getThreadPoolTaskExecutorBuilderConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("threadPoolTaskExecutorBuilder", TaskExecutorConfigurations__BeanDefinitions.ThreadPoolTaskExecutorBuilderConfiguration.getThreadPoolTaskExecutorBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.task.TaskExecutorConfigurations$TaskExecutorBuilderConfiguration", TaskExecutorConfigurations__BeanDefinitions.TaskExecutorBuilderConfiguration.getTaskExecutorBuilderConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("taskExecutorBuilder", TaskExecutorConfigurations__BeanDefinitions.TaskExecutorBuilderConfiguration.getTaskExecutorBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.task.TaskExecutorConfigurations$SimpleAsyncTaskExecutorBuilderConfiguration", TaskExecutorConfigurations__BeanDefinitions.SimpleAsyncTaskExecutorBuilderConfiguration.getSimpleAsyncTaskExecutorBuilderConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("simpleAsyncTaskExecutorBuilder", TaskExecutorConfigurations__BeanDefinitions.SimpleAsyncTaskExecutorBuilderConfiguration.getSimpleAsyncTaskExecutorBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.task.TaskExecutorConfigurations$TaskExecutorConfiguration", TaskExecutorConfigurations__BeanDefinitions.TaskExecutorConfiguration.getTaskExecutorConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("applicationTaskExecutor", TaskExecutorConfigurations__BeanDefinitions.TaskExecutorConfiguration.getApplicationTaskExecutorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration", TaskExecutionAutoConfiguration__BeanDefinitions.getTaskExecutionAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("spring.task.execution-org.springframework.boot.autoconfigure.task.TaskExecutionProperties", TaskExecutionProperties__BeanDefinitions.getTaskExecutionPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration", ValidationAutoConfiguration__BeanDefinitions.getValidationAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("defaultValidator", ValidationAutoConfiguration__BeanDefinitions.getDefaultValidatorBeanDefinition());
    beanFactory.registerBeanDefinition("methodValidationPostProcessor", ValidationAutoConfiguration__BeanDefinitions.getMethodValidationPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration$WhitelabelErrorViewConfiguration", ErrorMvcAutoConfiguration__BeanDefinitions.WhitelabelErrorViewConfiguration.getWhitelabelErrorViewConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("error", ErrorMvcAutoConfiguration__BeanDefinitions.WhitelabelErrorViewConfiguration.getErrorBeanDefinition());
    beanFactory.registerBeanDefinition("beanNameViewResolver", ErrorMvcAutoConfiguration__BeanDefinitions.WhitelabelErrorViewConfiguration.getBeanNameViewResolverBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration$DefaultErrorViewResolverConfiguration", ErrorMvcAutoConfiguration__BeanDefinitions.DefaultErrorViewResolverConfiguration.getDefaultErrorViewResolverConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("conventionErrorViewResolver", ErrorMvcAutoConfiguration__BeanDefinitions.DefaultErrorViewResolverConfiguration.getConventionErrorViewResolverBeanDefinition());
    beanFactory.registerBeanDefinition("spring.web-org.springframework.boot.autoconfigure.web.WebProperties", WebProperties__BeanDefinitions.getWebPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration", ErrorMvcAutoConfiguration__BeanDefinitions.getErrorMvcAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("errorAttributes", ErrorMvcAutoConfiguration__BeanDefinitions.getErrorAttributesBeanDefinition());
    beanFactory.registerBeanDefinition("basicErrorController", ErrorMvcAutoConfiguration__BeanDefinitions.getBasicErrorControllerBeanDefinition());
    beanFactory.registerBeanDefinition("errorPageCustomizer", ErrorMvcAutoConfiguration__BeanDefinitions.getErrorPageCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("preserveErrorControllerTargetClassPostProcessor", ErrorMvcAutoConfiguration__BeanDefinitions.getPreserveErrorControllerTargetClassPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration$EnableWebMvcConfiguration", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getEnableWebMvcConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("welcomePageHandlerMapping", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getWelcomePageHandlerMappingBeanDefinition());
    beanFactory.registerBeanDefinition("welcomePageNotAcceptableHandlerMapping", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getWelcomePageNotAcceptableHandlerMappingBeanDefinition());
    beanFactory.registerBeanDefinition("localeResolver", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getLocaleResolverBeanDefinition());
    beanFactory.registerBeanDefinition("themeResolver", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getThemeResolverBeanDefinition());
    beanFactory.registerBeanDefinition("flashMapManager", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getFlashMapManagerBeanDefinition());
    beanFactory.registerBeanDefinition("mvcConversionService", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcConversionServiceBeanDefinition());
    beanFactory.registerBeanDefinition("mvcValidator", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcValidatorBeanDefinition());
    beanFactory.registerBeanDefinition("mvcContentNegotiationManager", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcContentNegotiationManagerBeanDefinition());
    beanFactory.registerBeanDefinition("requestMappingHandlerMapping", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getRequestMappingHandlerMappingBeanDefinition());
    beanFactory.registerBeanDefinition("mvcPatternParser", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcPatternParserBeanDefinition());
    beanFactory.registerBeanDefinition("mvcUrlPathHelper", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcUrlPathHelperBeanDefinition());
    beanFactory.registerBeanDefinition("mvcPathMatcher", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcPathMatcherBeanDefinition());
    beanFactory.registerBeanDefinition("viewControllerHandlerMapping", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getViewControllerHandlerMappingBeanDefinition());
    beanFactory.registerBeanDefinition("beanNameHandlerMapping", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getBeanNameHandlerMappingBeanDefinition());
    beanFactory.registerBeanDefinition("routerFunctionMapping", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getRouterFunctionMappingBeanDefinition());
    beanFactory.registerBeanDefinition("resourceHandlerMapping", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getResourceHandlerMappingBeanDefinition());
    beanFactory.registerBeanDefinition("mvcResourceUrlProvider", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcResourceUrlProviderBeanDefinition());
    beanFactory.registerBeanDefinition("defaultServletHandlerMapping", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getDefaultServletHandlerMappingBeanDefinition());
    beanFactory.registerBeanDefinition("requestMappingHandlerAdapter", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getRequestMappingHandlerAdapterBeanDefinition());
    beanFactory.registerBeanDefinition("handlerFunctionAdapter", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getHandlerFunctionAdapterBeanDefinition());
    beanFactory.registerBeanDefinition("mvcUriComponentsContributor", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcUriComponentsContributorBeanDefinition());
    beanFactory.registerBeanDefinition("httpRequestHandlerAdapter", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getHttpRequestHandlerAdapterBeanDefinition());
    beanFactory.registerBeanDefinition("simpleControllerHandlerAdapter", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getSimpleControllerHandlerAdapterBeanDefinition());
    beanFactory.registerBeanDefinition("handlerExceptionResolver", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getHandlerExceptionResolverBeanDefinition());
    beanFactory.registerBeanDefinition("mvcViewResolver", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcViewResolverBeanDefinition());
    beanFactory.registerBeanDefinition("mvcHandlerMappingIntrospector", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getMvcHandlerMappingIntrospectorBeanDefinition());
    beanFactory.registerBeanDefinition("viewNameTranslator", WebMvcAutoConfiguration__BeanDefinitions.EnableWebMvcConfiguration.getViewNameTranslatorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration$WebMvcAutoConfigurationAdapter", WebMvcAutoConfiguration__BeanDefinitions.WebMvcAutoConfigurationAdapter.getWebMvcAutoConfigurationAdapterBeanDefinition());
    beanFactory.registerBeanDefinition("defaultViewResolver", WebMvcAutoConfiguration__BeanDefinitions.WebMvcAutoConfigurationAdapter.getDefaultViewResolverBeanDefinition());
    beanFactory.registerBeanDefinition("viewResolver", WebMvcAutoConfiguration__BeanDefinitions.WebMvcAutoConfigurationAdapter.getViewResolverBeanDefinition());
    beanFactory.registerBeanDefinition("requestContextFilter", WebMvcAutoConfiguration__BeanDefinitions.WebMvcAutoConfigurationAdapter.getRequestContextFilterBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration", WebMvcAutoConfiguration__BeanDefinitions.getWebMvcAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("formContentFilter", WebMvcAutoConfiguration__BeanDefinitions.getFormContentFilterBeanDefinition());
    beanFactory.registerBeanDefinition("cn.hutool.extra.spring.SpringUtil", SpringUtil__BeanDefinitions.getSpringUtilBeanDefinition());
    beanFactory.registerBeanDefinition("com.baomidou.mybatisplus.autoconfigure.DdlAutoConfiguration", DdlAutoConfiguration__BeanDefinitions.getDdlAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration", MybatisPlusLanguageDriverAutoConfiguration__BeanDefinitions.getMybatisPlusLanguageDriverAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.DataSourceConfiguration$Hikari", DataSourceConfiguration__BeanDefinitions.Hikari.getHikariBeanDefinition());
    beanFactory.registerBeanDefinition("jdbcConnectionDetailsHikariBeanPostProcessor", DataSourceConfiguration__BeanDefinitions.Hikari.getJdbcConnectionDetailsHikariBeanPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("dataSource", DataSourceConfiguration__BeanDefinitions.Hikari.getDataSourceBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.DataSourceJmxConfiguration$Hikari", DataSourceJmxConfiguration__BeanDefinitions.Hikari.getHikariBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.DataSourceJmxConfiguration", DataSourceJmxConfiguration__BeanDefinitions.getDataSourceJmxConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration$PooledDataSourceConfiguration", DataSourceAutoConfiguration__BeanDefinitions.PooledDataSourceConfiguration.getPooledDataSourceConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("jdbcConnectionDetails", DataSourceAutoConfiguration__BeanDefinitions.PooledDataSourceConfiguration.getJdbcConnectionDetailsBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration$HikariPoolDataSourceMetadataProviderConfiguration", DataSourcePoolMetadataProvidersConfiguration__BeanDefinitions.HikariPoolDataSourceMetadataProviderConfiguration.getHikariPoolDataSourceMetadataProviderConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("hikariPoolDataSourceMetadataProvider", DataSourcePoolMetadataProvidersConfiguration__BeanDefinitions.HikariPoolDataSourceMetadataProviderConfiguration.getHikariPoolDataSourceMetadataProviderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.metadata.DataSourcePoolMetadataProvidersConfiguration", DataSourcePoolMetadataProvidersConfiguration__BeanDefinitions.getDataSourcePoolMetadataProvidersConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration", DataSourceAutoConfiguration__BeanDefinitions.getDataSourceAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("spring.datasource-org.springframework.boot.autoconfigure.jdbc.DataSourceProperties", DataSourceProperties__BeanDefinitions.getDataSourcePropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration", MybatisPlusAutoConfiguration__BeanDefinitions.getMybatisPlusAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("sqlSessionFactory", MybatisPlusAutoConfiguration__BeanDefinitions.getSqlSessionFactoryBeanDefinition());
    beanFactory.registerBeanDefinition("sqlSessionTemplate", MybatisPlusAutoConfiguration__BeanDefinitions.getSqlSessionTemplateBeanDefinition());
    beanFactory.registerBeanDefinition("mybatis-plus-com.baomidou.mybatisplus.autoconfigure.MybatisPlusProperties", MybatisPlusProperties__BeanDefinitions.getMybatisPlusPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$AspectJAutoProxyingConfiguration$CglibAutoProxyConfiguration", AopAutoConfiguration__BeanDefinitions.AspectJAutoProxyingConfiguration.CglibAutoProxyConfiguration.getCglibAutoProxyConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.aop.AopAutoConfiguration$AspectJAutoProxyingConfiguration", AopAutoConfiguration__BeanDefinitions.AspectJAutoProxyingConfiguration.getAspectJAutoProxyingConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.aop.AopAutoConfiguration", AopAutoConfiguration__BeanDefinitions.getAopAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.availability.ApplicationAvailabilityAutoConfiguration", ApplicationAvailabilityAutoConfiguration__BeanDefinitions.getApplicationAvailabilityAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("applicationAvailability", ApplicationAvailabilityAutoConfiguration__BeanDefinitions.getApplicationAvailabilityBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.data.redis.LettuceConnectionConfiguration", LettuceConnectionConfiguration__BeanDefinitions.getLettuceConnectionConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("lettuceClientResources", LettuceConnectionConfiguration__BeanDefinitions.getLettuceClientResourcesBeanDefinition());
    beanFactory.registerBeanDefinition("redisConnectionFactory", LettuceConnectionConfiguration__BeanDefinitions.getRedisConnectionFactoryBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration", RedisAutoConfiguration__BeanDefinitions.getRedisAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("redisConnectionDetails", RedisAutoConfiguration__BeanDefinitions.getRedisConnectionDetailsBeanDefinition());
    beanFactory.registerBeanDefinition("stringRedisTemplate", RedisAutoConfiguration__BeanDefinitions.getStringRedisTemplateBeanDefinition());
    beanFactory.registerBeanDefinition("spring.data.redis-org.springframework.boot.autoconfigure.data.redis.RedisProperties", RedisProperties__BeanDefinitions.getRedisPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$Jackson2ObjectMapperBuilderCustomizerConfiguration", JacksonAutoConfiguration__BeanDefinitions.Jackson2ObjectMapperBuilderCustomizerConfiguration.getJacksonObjectMapperBuilderCustomizerConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("standardJacksonObjectMapperBuilderCustomizer", JacksonAutoConfiguration__BeanDefinitions.Jackson2ObjectMapperBuilderCustomizerConfiguration.getStandardJacksonObjectMapperBuilderCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("spring.jackson-org.springframework.boot.autoconfigure.jackson.JacksonProperties", JacksonProperties__BeanDefinitions.getJacksonPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonObjectMapperBuilderConfiguration", JacksonAutoConfiguration__BeanDefinitions.JacksonObjectMapperBuilderConfiguration.getJacksonObjectMapperBuilderConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("jacksonObjectMapperBuilder", JacksonAutoConfiguration__BeanDefinitions.JacksonObjectMapperBuilderConfiguration.getJacksonObjectMapperBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$ParameterNamesModuleConfiguration", JacksonAutoConfiguration__BeanDefinitions.ParameterNamesModuleConfiguration.getParameterNamesModuleConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("parameterNamesModule", JacksonAutoConfiguration__BeanDefinitions.ParameterNamesModuleConfiguration.getParameterNamesModuleBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonObjectMapperConfiguration", JacksonAutoConfiguration__BeanDefinitions.JacksonObjectMapperConfiguration.getJacksonObjectMapperConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("jacksonObjectMapper", JacksonAutoConfiguration__BeanDefinitions.JacksonObjectMapperConfiguration.getJacksonObjectMapperBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration$JacksonMixinConfiguration", JacksonAutoConfiguration__BeanDefinitions.JacksonMixinConfiguration.getJacksonMixinConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("jsonMixinModuleEntries", JsonMixinModuleEntries__BeanDefinitions.getJsonMixinModuleEntriesBeanDefinition());
    beanFactory.registerBeanDefinition("jsonMixinModule", JacksonAutoConfiguration__BeanDefinitions.JacksonMixinConfiguration.getJsonMixinModuleBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration", JacksonAutoConfiguration__BeanDefinitions.getJacksonAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("jsonComponentModule", JacksonAutoConfiguration__BeanDefinitions.getJsonComponentModuleBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizationAutoConfiguration", TransactionManagerCustomizationAutoConfiguration__BeanDefinitions.getTransactionManagerCustomizationAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("platformTransactionManagerCustomizers", TransactionManagerCustomizationAutoConfiguration__BeanDefinitions.getPlatformTransactionManagerCustomizersBeanDefinition());
    beanFactory.registerBeanDefinition("transactionExecutionListeners", TransactionManagerCustomizationAutoConfiguration__BeanDefinitions.getTransactionExecutionListenersBeanDefinition());
    beanFactory.registerBeanDefinition("spring.transaction-org.springframework.boot.autoconfigure.transaction.TransactionProperties", TransactionProperties__BeanDefinitions.getTransactionPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.context.ConfigurationPropertiesAutoConfiguration", ConfigurationPropertiesAutoConfiguration__BeanDefinitions.getConfigurationPropertiesAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.context.LifecycleAutoConfiguration", LifecycleAutoConfiguration__BeanDefinitions.getLifecycleAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("lifecycleProcessor", LifecycleAutoConfiguration__BeanDefinitions.getLifecycleProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("spring.lifecycle-org.springframework.boot.autoconfigure.context.LifecycleProperties", LifecycleProperties__BeanDefinitions.getLifecyclePropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.dao.PersistenceExceptionTranslationAutoConfiguration", PersistenceExceptionTranslationAutoConfiguration__BeanDefinitions.getPersistenceExceptionTranslationAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("persistenceExceptionTranslationPostProcessor", PersistenceExceptionTranslationAutoConfiguration__BeanDefinitions.getPersistenceExceptionTranslationPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration", RedisReactiveAutoConfiguration__BeanDefinitions.getRedisReactiveAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("reactiveRedisTemplate", RedisReactiveAutoConfiguration__BeanDefinitions.getReactiveRedisTemplateBeanDefinition());
    beanFactory.registerBeanDefinition("reactiveStringRedisTemplate", RedisReactiveAutoConfiguration__BeanDefinitions.getReactiveStringRedisTemplateBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration", RedisRepositoriesAutoConfiguration__BeanDefinitions.getRedisRepositoriesAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("redisIndexConfiguration#0", IndexConfiguration__BeanDefinitions.getRedisIndexConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("redisKeyspaceConfiguration#0", KeyspaceConfiguration__BeanDefinitions.getRedisKeyspaceConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("redisMappingConfiguration#0", MappingConfiguration__BeanDefinitions.getRedisMappingConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("keyValueMappingContext", RedisMappingContext__BeanDefinitions.getKeyValueMappingContextBeanDefinition());
    beanFactory.registerBeanDefinition("redisCustomConversions", RedisCustomConversions__BeanDefinitions.getRedisCustomConversionsBeanDefinition());
    beanFactory.registerBeanDefinition("redisReferenceResolver", ReferenceResolverImpl__BeanDefinitions.getRedisReferenceResolverBeanDefinition());
    beanFactory.registerBeanDefinition("redisConverter", MappingRedisConverter__BeanDefinitions.getRedisConverterBeanDefinition());
    beanFactory.registerBeanDefinition("redisKeyValueAdapter", RedisKeyValueAdapter__BeanDefinitions.getRedisKeyValueAdapterBeanDefinition());
    beanFactory.registerBeanDefinition("redisKeyValueTemplate", RedisKeyValueTemplate__BeanDefinitions.getRedisKeyValueTemplateBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration$StringHttpMessageConverterConfiguration", HttpMessageConvertersAutoConfiguration__BeanDefinitions.StringHttpMessageConverterConfiguration.getStringHttpMessageConverterConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("stringHttpMessageConverter", HttpMessageConvertersAutoConfiguration__BeanDefinitions.StringHttpMessageConverterConfiguration.getStringHttpMessageConverterBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration$MappingJackson2HttpMessageConverterConfiguration", JacksonHttpMessageConvertersConfiguration__BeanDefinitions.MappingJackson2HttpMessageConverterConfiguration.getMappingJacksonHttpMessageConverterConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("mappingJackson2HttpMessageConverter", JacksonHttpMessageConvertersConfiguration__BeanDefinitions.MappingJackson2HttpMessageConverterConfiguration.getMappingJacksonHttpMessageConverterBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.http.JacksonHttpMessageConvertersConfiguration", JacksonHttpMessageConvertersConfiguration__BeanDefinitions.getJacksonHttpMessageConvertersConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration", HttpMessageConvertersAutoConfiguration__BeanDefinitions.getHttpMessageConvertersAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("messageConverters", HttpMessageConvertersAutoConfiguration__BeanDefinitions.getMessageConvertersBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.data.web.config.ProjectingArgumentResolverRegistrar", ProjectingArgumentResolverRegistrar__BeanDefinitions.getProjectingArgumentResolverRegistrarBeanDefinition());
    beanFactory.registerBeanDefinition("projectingArgumentResolverBeanPostProcessor", ProjectingArgumentResolverRegistrar__BeanDefinitions.getProjectingArgumentResolverBeanPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.data.web.config.SpringDataWebConfiguration", SpringDataWebConfiguration__BeanDefinitions.getSpringDataWebConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("pageableResolver", SpringDataWebConfiguration__BeanDefinitions.getPageableResolverBeanDefinition());
    beanFactory.registerBeanDefinition("sortResolver", SpringDataWebConfiguration__BeanDefinitions.getSortResolverBeanDefinition());
    beanFactory.registerBeanDefinition("offsetResolver", SpringDataWebConfiguration__BeanDefinitions.getOffsetResolverBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.data.web.config.SpringDataJacksonConfiguration", SpringDataJacksonConfiguration__BeanDefinitions.getSpringDataJacksonConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("jacksonGeoModule", SpringDataJacksonConfiguration__BeanDefinitions.getJacksonGeoModuleBeanDefinition());
    beanFactory.registerBeanDefinition("pageModule", SpringDataJacksonConfiguration__BeanDefinitions.getPageModuleBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.data.web.SpringDataWebAutoConfiguration", SpringDataWebAutoConfiguration__BeanDefinitions.getSpringDataWebAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("pageableCustomizer", SpringDataWebAutoConfiguration__BeanDefinitions.getPageableCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("sortCustomizer", SpringDataWebAutoConfiguration__BeanDefinitions.getSortCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("spring.data.web-org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties", SpringDataWebProperties__BeanDefinitions.getSpringDataWebPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration", ProjectInfoAutoConfiguration__BeanDefinitions.getProjectInfoAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("spring.info-org.springframework.boot.autoconfigure.info.ProjectInfoProperties", ProjectInfoProperties__BeanDefinitions.getProjectInfoPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.JdbcTemplateConfiguration", JdbcTemplateConfiguration__BeanDefinitions.getJdbcTemplateConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("jdbcTemplate", JdbcTemplateConfiguration__BeanDefinitions.getJdbcTemplateBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.NamedParameterJdbcTemplateConfiguration", NamedParameterJdbcTemplateConfiguration__BeanDefinitions.getNamedParameterJdbcTemplateConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("namedParameterJdbcTemplate", NamedParameterJdbcTemplateConfiguration__BeanDefinitions.getNamedParameterJdbcTemplateBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration", JdbcTemplateAutoConfiguration__BeanDefinitions.getJdbcTemplateAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("spring.jdbc-org.springframework.boot.autoconfigure.jdbc.JdbcProperties", JdbcProperties__BeanDefinitions.getJdbcPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.sql.init.dependency.DatabaseInitializationDependencyConfigurer$DependsOnDatabaseInitializationPostProcessor", DatabaseInitializationDependencyConfigurer__BeanDefinitions.DependsOnDatabaseInitializationPostProcessor.getDependsOnDatabaseInitializationPostProcessorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.JdbcClientAutoConfiguration", JdbcClientAutoConfiguration__BeanDefinitions.getJdbcClientAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("jdbcClient", JdbcClientAutoConfiguration__BeanDefinitions.getJdbcClientBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.netty.NettyAutoConfiguration", NettyAutoConfiguration__BeanDefinitions.getNettyAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("spring.netty-org.springframework.boot.autoconfigure.netty.NettyProperties", NettyProperties__BeanDefinitions.getNettyPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.reactor.ReactorAutoConfiguration", ReactorAutoConfiguration__BeanDefinitions.getReactorAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("spring.reactor-org.springframework.boot.autoconfigure.reactor.ReactorProperties", ReactorProperties__BeanDefinitions.getReactorPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.security.servlet.SpringBootWebSecurityConfiguration", SpringBootWebSecurityConfiguration__BeanDefinitions.getSpringBootWebSecurityConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration", SecurityAutoConfiguration__BeanDefinitions.getSecurityAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("authenticationEventPublisher", SecurityAutoConfiguration__BeanDefinitions.getAuthenticationEventPublisherBeanDefinition());
    beanFactory.registerBeanDefinition("spring.security-org.springframework.boot.autoconfigure.security.SecurityProperties", SecurityProperties__BeanDefinitions.getSecurityPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration", SecurityFilterAutoConfiguration__BeanDefinitions.getSecurityFilterAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("securityFilterChainRegistration", SecurityFilterAutoConfiguration__BeanDefinitions.getSecurityFilterChainRegistrationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration", UserDetailsServiceAutoConfiguration__BeanDefinitions.getUserDetailsServiceAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("inMemoryUserDetailsManager", UserDetailsServiceAutoConfiguration__BeanDefinitions.getInMemoryUserDetailsManagerBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.sql.init.DataSourceInitializationConfiguration", DataSourceInitializationConfiguration__BeanDefinitions.getDataSourceInitializationConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("dataSourceScriptDatabaseInitializer", DataSourceInitializationConfiguration__BeanDefinitions.getDataSourceScriptDatabaseInitializerBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.sql.init.SqlInitializationAutoConfiguration", SqlInitializationAutoConfiguration__BeanDefinitions.getSqlInitializationAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("spring.sql.init-org.springframework.boot.autoconfigure.sql.init.SqlInitializationProperties", SqlInitializationProperties__BeanDefinitions.getSqlInitializationPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.task.TaskSchedulingConfigurations$ThreadPoolTaskSchedulerBuilderConfiguration", TaskSchedulingConfigurations__BeanDefinitions.ThreadPoolTaskSchedulerBuilderConfiguration.getThreadPoolTaskSchedulerBuilderConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("threadPoolTaskSchedulerBuilder", TaskSchedulingConfigurations__BeanDefinitions.ThreadPoolTaskSchedulerBuilderConfiguration.getThreadPoolTaskSchedulerBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.task.TaskSchedulingConfigurations$TaskSchedulerBuilderConfiguration", TaskSchedulingConfigurations__BeanDefinitions.TaskSchedulerBuilderConfiguration.getTaskSchedulerBuilderConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("taskSchedulerBuilder", TaskSchedulingConfigurations__BeanDefinitions.TaskSchedulerBuilderConfiguration.getTaskSchedulerBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.task.TaskSchedulingConfigurations$SimpleAsyncTaskSchedulerBuilderConfiguration", TaskSchedulingConfigurations__BeanDefinitions.SimpleAsyncTaskSchedulerBuilderConfiguration.getSimpleAsyncTaskSchedulerBuilderConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("simpleAsyncTaskSchedulerBuilder", TaskSchedulingConfigurations__BeanDefinitions.SimpleAsyncTaskSchedulerBuilderConfiguration.getSimpleAsyncTaskSchedulerBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration", TaskSchedulingAutoConfiguration__BeanDefinitions.getTaskSchedulingAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("spring.task.scheduling-org.springframework.boot.autoconfigure.task.TaskSchedulingProperties", TaskSchedulingProperties__BeanDefinitions.getTaskSchedulingPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration$JdbcTransactionManagerConfiguration", DataSourceTransactionManagerAutoConfiguration__BeanDefinitions.JdbcTransactionManagerConfiguration.getJdbcTransactionManagerConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("transactionManager", DataSourceTransactionManagerAutoConfiguration__BeanDefinitions.JdbcTransactionManagerConfiguration.getTransactionManagerBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration", DataSourceTransactionManagerAutoConfiguration__BeanDefinitions.getDataSourceTransactionManagerAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration", ProxyTransactionManagementConfiguration__BeanDefinitions.getProxyTransactionManagementConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.transaction.config.internalTransactionAdvisor", ProxyTransactionManagementConfiguration__BeanDefinitions.getInternalTransactionAdvisorBeanDefinition());
    beanFactory.registerBeanDefinition("transactionAttributeSource", ProxyTransactionManagementConfiguration__BeanDefinitions.getTransactionAttributeSourceBeanDefinition());
    beanFactory.registerBeanDefinition("transactionInterceptor", ProxyTransactionManagementConfiguration__BeanDefinitions.getTransactionInterceptorBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.transaction.config.internalTransactionalEventListenerFactory", AbstractTransactionManagementConfiguration__BeanDefinitions.getInternalTransactionalEventListenerFactoryBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration$EnableTransactionManagementConfiguration$CglibAutoProxyConfiguration", TransactionAutoConfiguration__BeanDefinitions.EnableTransactionManagementConfiguration.CglibAutoProxyConfiguration.getCglibAutoProxyConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration$EnableTransactionManagementConfiguration", TransactionAutoConfiguration__BeanDefinitions.EnableTransactionManagementConfiguration.getEnableTransactionManagementConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration$TransactionTemplateConfiguration", TransactionAutoConfiguration__BeanDefinitions.TransactionTemplateConfiguration.getTransactionTemplateConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("transactionTemplate", TransactionAutoConfiguration__BeanDefinitions.TransactionTemplateConfiguration.getTransactionTemplateBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration", TransactionAutoConfiguration__BeanDefinitions.getTransactionAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration", RestClientAutoConfiguration__BeanDefinitions.getRestClientAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("httpMessageConvertersRestClientCustomizer", RestClientAutoConfiguration__BeanDefinitions.getHttpMessageConvertersRestClientCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("restClientSsl", RestClientAutoConfiguration__BeanDefinitions.getRestClientSslBeanDefinition());
    beanFactory.registerBeanDefinition("restClientBuilderConfigurer", RestClientAutoConfiguration__BeanDefinitions.getRestClientBuilderConfigurerBeanDefinition());
    beanFactory.registerBeanDefinition("restClientBuilder", RestClientAutoConfiguration__BeanDefinitions.getRestClientBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration", RestTemplateAutoConfiguration__BeanDefinitions.getRestTemplateAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("restTemplateBuilderConfigurer", RestTemplateAutoConfiguration__BeanDefinitions.getRestTemplateBuilderConfigurerBeanDefinition());
    beanFactory.registerBeanDefinition("restTemplateBuilder", RestTemplateAutoConfiguration__BeanDefinitions.getRestTemplateBuilderBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration$TomcatWebServerFactoryCustomizerConfiguration", EmbeddedWebServerFactoryCustomizerAutoConfiguration__BeanDefinitions.TomcatWebServerFactoryCustomizerConfiguration.getTomcatWebServerFactoryCustomizerConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("tomcatWebServerFactoryCustomizer", EmbeddedWebServerFactoryCustomizerAutoConfiguration__BeanDefinitions.TomcatWebServerFactoryCustomizerConfiguration.getTomcatWebServerFactoryCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.embedded.EmbeddedWebServerFactoryCustomizerAutoConfiguration", EmbeddedWebServerFactoryCustomizerAutoConfiguration__BeanDefinitions.getEmbeddedWebServerFactoryCustomizerAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.HttpEncodingAutoConfiguration", HttpEncodingAutoConfiguration__BeanDefinitions.getHttpEncodingAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("characterEncodingFilter", HttpEncodingAutoConfiguration__BeanDefinitions.getCharacterEncodingFilterBeanDefinition());
    beanFactory.registerBeanDefinition("localeCharsetMappingsCustomizer", HttpEncodingAutoConfiguration__BeanDefinitions.getLocaleCharsetMappingsCustomizerBeanDefinition());
    beanFactory.registerBeanDefinition("org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration", MultipartAutoConfiguration__BeanDefinitions.getMultipartAutoConfigurationBeanDefinition());
    beanFactory.registerBeanDefinition("multipartConfigElement", MultipartAutoConfiguration__BeanDefinitions.getMultipartConfigElementBeanDefinition());
    beanFactory.registerBeanDefinition("multipartResolver", MultipartAutoConfiguration__BeanDefinitions.getMultipartResolverBeanDefinition());
    beanFactory.registerBeanDefinition("spring.servlet.multipart-org.springframework.boot.autoconfigure.web.servlet.MultipartProperties", MultipartProperties__BeanDefinitions.getMultipartPropertiesBeanDefinition());
    beanFactory.registerBeanDefinition("mvcHandlerMappingIntrospectorRequestTransformer", HandlerMappingIntrospectorRequestTransformer__BeanDefinitions.getMvcHandlerMappingIntrospectorRequestTransformerBeanDefinition());
    beanFactory.registerBeanDefinition("springSecurityFilterChain", WebMvcSecurityConfiguration__BeanDefinitions.CompositeFilterChainProxy.getSpringSecurityFilterChainBeanDefinition());
    beanFactory.registerBeanDefinition("announcementMapper", getAnnouncementMapperBeanDefinition());
    beanFactory.registerBeanDefinition("carInfoMapper", getCarInfoMapperBeanDefinition());
    beanFactory.registerBeanDefinition("customerInfoMapper", getCustomerInfoMapperBeanDefinition());
    beanFactory.registerBeanDefinition("customerOrderMapper", getCustomerOrderMapperBeanDefinition());
    beanFactory.registerBeanDefinition("financeRecordMapper", getFinanceRecordMapperBeanDefinition());
    beanFactory.registerBeanDefinition("sysConfigMapper", getSysConfigMapperBeanDefinition());
    beanFactory.registerBeanDefinition("sysMenuMapper", getSysMenuMapperBeanDefinition());
    beanFactory.registerBeanDefinition("sysRoleMapper", getSysRoleMapperBeanDefinition());
    beanFactory.registerBeanDefinition("sysThemeConfigMapper", getSysThemeConfigMapperBeanDefinition());
    beanFactory.registerBeanDefinition("sysUserMapper", getSysUserMapperBeanDefinition());
    beanFactory.registerBeanDefinition("sysUserRoleMapper", getSysUserRoleMapperBeanDefinition());
    beanFactory.registerBeanDefinition("afterSalesComplaintMapper", getAfterSalesComplaintMapperBeanDefinition());
    beanFactory.registerBeanDefinition("carConfigMapper", getCarConfigMapperBeanDefinition());
    beanFactory.registerBeanDefinition("carDocumentMapper", getCarDocumentMapperBeanDefinition());
    beanFactory.registerBeanDefinition("carImageMapper", getCarImageMapperBeanDefinition());
    beanFactory.registerBeanDefinition("carMaintenanceMapper", getCarMaintenanceMapperBeanDefinition());
    beanFactory.registerBeanDefinition("carViolationMapper", getCarViolationMapperBeanDefinition());
    beanFactory.registerBeanDefinition("gpsTrackMapper", getGpsTrackMapperBeanDefinition());
    beanFactory.registerBeanDefinition("couponCarMapper", getCouponCarMapperBeanDefinition());
    beanFactory.registerBeanDefinition("couponMapper", getCouponMapperBeanDefinition());
    beanFactory.registerBeanDefinition("memberCouponMapper", getMemberCouponMapperBeanDefinition());
    beanFactory.registerBeanDefinition("costRecordMapper", getCostRecordMapperBeanDefinition());
    beanFactory.registerBeanDefinition("invoiceMapper", getInvoiceMapperBeanDefinition());
    beanFactory.registerBeanDefinition("reconciliationMapper", getReconciliationMapperBeanDefinition());
    beanFactory.registerBeanDefinition("carouselMapper", getCarouselMapperBeanDefinition());
    beanFactory.registerBeanDefinition("operationLogMapper", getOperationLogMapperBeanDefinition());
    beanFactory.registerBeanDefinition("sysDictDataMapper", getSysDictDataMapperBeanDefinition());
    beanFactory.registerBeanDefinition("sysDictTypeMapper", getSysDictTypeMapperBeanDefinition());
    beanFactory.registerBeanDefinition("sysFileMapper", getSysFileMapperBeanDefinition());
  }

  /**
   * Register the aliases.
   */
  public void registerAliases(DefaultListableBeanFactory beanFactory) {
    beanFactory.registerAlias("applicationTaskExecutor", "taskExecutor");
  }
}

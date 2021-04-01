package com.roc.spring.component.annotation;

import com.roc.spring.component.service.ConfigService;
import com.roc.spring.component.service.impl.RefreshConfigService;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 使用方法：
 * <pre>{@code
 * @literal @Config(tableName = "cloud_bar")
 *  public class BarTypeConfigService extends RefreshConfigService<BarType> {
 *     @literal @Scheduled(cron = "0 0 * * * ?")
 *     @literal @Override
 *      protected void scheduleReload() {
 *          this.reloadConfigs();
 *      }
 *  }}
 * </pre>
 * 然后，就可以调用获取到：
 * <pre>{@code List<BarType> barTypeConfigs = BarTypeConfigService.getConfigList();}</pre>
 * <p>
 *
 * @author chenpeng
 * @version 1.0
 * @see RefreshConfigService
 * @see ConfigService
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@EnableScheduling
@Lazy(false)
@RPC
public @interface Config {
    String tableName() default "";

    String sql() default "";
}

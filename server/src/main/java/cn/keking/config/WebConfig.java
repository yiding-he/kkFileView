package cn.keking.config;

import cn.keking.web.filter.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashSet;
import java.util.Set;

/**
 *
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final static Logger LOGGER = LoggerFactory.getLogger(WebConfig.class);

  /**
   * 访问外部文件配置
   */
  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    String filePath = ConfigConstants.getFileDir();
    LOGGER.info("Add resource locations: {}", filePath);
    registry.addResourceHandler("/**").addResourceLocations("classpath:/META-INF/resources/", "classpath:/resources/", "classpath:/static/", "classpath:/public/", "file:" + filePath);
  }

  @Bean
  public FilterRegistrationBean<ChinesePathFilter> getChinesePathFilter() {
    ChinesePathFilter filter = new ChinesePathFilter();
    FilterRegistrationBean<ChinesePathFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(filter);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<TrustHostFilter> getTrustHostFilter() {
    Set<String> filterUri = new HashSet<>();
    filterUri.add("/onlinePreview"   );
    filterUri.add("/picturesPreview");
    filterUri.add("/getCorsFile"     );
    TrustHostFilter filter = new TrustHostFilter();
    FilterRegistrationBean<TrustHostFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(filter);
    registrationBean.setUrlPatterns(filterUri);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<TrustDirFilter> getTrustDirFilter() {
    Set<String> filterUri = new HashSet<>();
    filterUri.add("/onlinePreview");
    filterUri.add("/picturesPreview");
    filterUri.add("/getCorsFile");
    TrustDirFilter filter = new TrustDirFilter();
    FilterRegistrationBean<TrustDirFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(filter);
    registrationBean.setUrlPatterns(filterUri);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<BaseUrlFilter> getBaseUrlFilter() {
    BaseUrlFilter filter = new BaseUrlFilter();
    FilterRegistrationBean<BaseUrlFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(filter);
    registrationBean.setUrlPatterns(BaseUrlFilter.URIS);
    return registrationBean;
  }

  @Bean
  public FilterRegistrationBean<AttributeSetFilter> getWatermarkConfigFilter() {
    AttributeSetFilter filter = new AttributeSetFilter();
    FilterRegistrationBean<AttributeSetFilter> registrationBean = new FilterRegistrationBean<>();
    registrationBean.setFilter(filter);
    registrationBean.setUrlPatterns(AttributeSetFilter.URIS);
    return registrationBean;
  }
}

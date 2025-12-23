package com.common_logging.logging.library.configuration;


import com.common_logging.logging.library.filter.RequestResponseLoggingFilter;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(LoggingProperties.class)
@ConditionalOnProperty(prefix = "common.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CommonLoggingAutoConfiguration {

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> requestResponseLoggingFilter(
            LoggingProperties properties,
            ObjectProvider<Tracer> tracerProvider
    ) {
        Tracer tracer = tracerProvider.getIfAvailable();

        RequestResponseLoggingFilter filter = new RequestResponseLoggingFilter(properties, tracer);
        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>(filter);
        registrationBean.setOrder(Integer.MIN_VALUE + 10); // early
        return registrationBean;
    }
}

package io.angularpay.paynable.adapters.inbound;

import io.angularpay.paynable.adapters.outbound.CipherServiceAdapter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CipherFilterRegistrar {

    @ConditionalOnProperty(
            value = "angularpay.cipher.enabled",
            havingValue = "true",
            matchIfMissing = true)
    @Bean
    public FilterRegistrationBean<CipherFilter> registerPostCommentsRateLimiter(CipherServiceAdapter cipherServiceAdapter) {
        FilterRegistrationBean<CipherFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CipherFilter(cipherServiceAdapter));
        registrationBean.addUrlPatterns(
                "/paynable/requests",
                "/paynable/requests/*/amount",
                "/paynable/requests/*/interest-rate",
                "/paynable/requests/*/investors",
                "/paynable/requests/*/bargains",
                "/paynable/requests/*/investors/*/amount",
                "/paynable/requests/*/investors/*/payment"
        );
        return registrationBean;
    }
}

package com.stormpath.juiser.spring.boot.config;

import com.stormpath.juiser.jwt.config.JwtConfig;
import com.stormpath.juiser.model.User;
import com.stormpath.juiser.servlet.user.RequestHeaderUserFactory;
import com.stormpath.juiser.spring.web.ForwardedUserFilter;
import com.stormpath.juiser.spring.web.user.RequestContextUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.http.HttpServletRequest;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "SpringJavaAutowiringInspection"})
@Configuration
@ConditionalOnProperty(name = {"juiser.enabled"}, matchIfMissing = true)
@ConditionalOnJuiserSpringSecurityDisabled
public class JuiserDefaultAutoConfiguration {

    @Autowired
    @Qualifier("juiserForwardedUserJwsClaimsExtractor")
    private Function<String, Claims> juiserForwardedUserJwsClaimsExtractor;

    @Autowired
    @Qualifier("juiserJwtClaimsUserFactory")
    private Function<Claims, User> juiserJwtClaimsUserFactory;

    @Bean
    @ConditionalOnMissingBean
    public ForwardedHeaderConfig<JwtConfig> forwardedHeaderConfig() {
        return new ForwardedHeaderConfig<>();
    }

    @Bean
    @ConditionalOnMissingBean
    public ForwardedUserFilterConfig juiserForwardedUserFilterConfig() {
        return new ForwardedUserFilterConfig();
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserRequestUserFactory")
    public Function<HttpServletRequest, User> juiserRequestUserFactory() {

        return new RequestHeaderUserFactory(forwardedHeaderConfig().getName(), s -> {
            Claims claims = juiserForwardedUserJwsClaimsExtractor.apply(s);
            Assert.state(claims != null && !claims.isEmpty(),
                "juiserForwardedUserJwsClaimsExtractor cannot return null or empty Claims instances.");
            User user = juiserJwtClaimsUserFactory.apply(claims);
            Assert.state(user != null,
                "juiserJwtClaimsUserFactory cannot return null User instances.");
            return user;
        });
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserForwardedUserFilter")
    public FilterRegistrationBean juiserForwardedUserFilter() {

        ForwardedUserFilterConfig cfg = juiserForwardedUserFilterConfig();

        Filter filter = new ForwardedUserFilter(forwardedHeaderConfig().getName(),
            juiserRequestUserFactory(),
            cfg.getRequestAttributeNames());

        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(filter);
        bean.setEnabled(cfg.isEnabled());
        bean.setMatchAfter(cfg.isMatchAfter());
        bean.setOrder(cfg.getOrder());

        Set<DispatcherType> dispatcherTypes = cfg.getDispatcherTypes();
        if (!CollectionUtils.isEmpty(dispatcherTypes)) {
            bean.setDispatcherTypes(EnumSet.copyOf(dispatcherTypes));
        }
        Set<String> set = cfg.getServletNames();
        if (!CollectionUtils.isEmpty(set)) {
            bean.setServletNames(set);
        }
        set = cfg.getUrlPatterns();
        if (!CollectionUtils.isEmpty(set)) {
            bean.setUrlPatterns(set);
        }

        return bean;
    }

    @Bean
    @ConditionalOnMissingBean
    public User juiserUser() {
        return new RequestContextUser();
    }
}

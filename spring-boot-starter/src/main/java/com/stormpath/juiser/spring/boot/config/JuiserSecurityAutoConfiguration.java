package com.stormpath.juiser.spring.boot.config;

import com.stormpath.juiser.jwt.StringCollectionClaimResolver;
import com.stormpath.juiser.model.User;
import com.stormpath.juiser.spring.jwt.ClaimsExpressionEvaluator;
import com.stormpath.juiser.spring.security.authentication.ClaimsGrantedAuthoritiesResolver;
import com.stormpath.juiser.spring.security.authentication.HeaderAuthenticationProvider;
import com.stormpath.juiser.spring.security.authentication.JwsToUserDetailsConverter;
import com.stormpath.juiser.spring.security.config.SpringSecurityJwtConfig;
import com.stormpath.juiser.spring.security.user.SecurityContextUser;
import com.stormpath.juiser.spring.security.web.authentication.HeaderAuthenticationFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
@SuppressWarnings({"SpringJavaAutowiringInspection", "SpringAutowiredFieldsWarningInspection"})
@Configuration
@ConditionalOnClass({AbstractHttpConfigurer.class})
@ConditionalOnJuiserSpringSecurityEnabled
public class JuiserSecurityAutoConfiguration {

    @Autowired
    @Qualifier("juiserForwardedUserJwsClaimsExtractor")
    private Function<String, Claims> claimsExtractor;

    @Autowired
    @Qualifier("juiserJwtClaimsUserFactory")
    private Function<Claims, User> claimsUserFactory;

    @Bean
    @ConditionalOnMissingBean
    public ForwardedHeaderConfig<SpringSecurityJwtConfig> forwardedHeaderConfig() {
        return new ForwardedHeaderConfig<>(new SpringSecurityJwtConfig());
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserJwtDataExtractor")
    public Function<Claims, Collection<String>> juiserGrantedAuthoritiesClaimResolver() {

        final SpringSecurityJwtConfig jwt = forwardedHeaderConfig().getJwt();

        String expression = jwt.getGrantedAuthoritiesExpression();

        if (!Strings.hasText(expression)) {
            expression = "['user']['groups']['items'].![get('name')]";
        }

        return new StringCollectionClaimResolver(new ClaimsExpressionEvaluator(expression), false);
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserJwsUserDetailsConverter")
    public Function<String, UserDetails> juiserJwsUserDetailsConverter() {

        Function<Claims, Collection<? extends GrantedAuthority>> grantedAuthoritiesResolver =
            new ClaimsGrantedAuthoritiesResolver(juiserGrantedAuthoritiesClaimResolver());

        return new JwsToUserDetailsConverter(claimsExtractor, claimsUserFactory, grantedAuthoritiesResolver);
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserAuthenticationProvider")
    public AuthenticationProvider juiserAuthenticationProvider() {
        Function<String, UserDetails> converter = juiserJwsUserDetailsConverter();
        return new HeaderAuthenticationProvider(converter);
    }

    @Autowired
    public void registerAuthenticationProvider(AuthenticationManagerBuilder builder,
                                               @Qualifier("juiserAuthenticationProvider") AuthenticationProvider provider) {
        builder.authenticationProvider(provider);
    }

    @Bean
    @ConditionalOnMissingBean
    public User juiserUser() {
        return new SecurityContextUser();
    }
}

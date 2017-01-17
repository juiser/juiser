package com.stormpath.juiser.spring.boot.config;

import com.stormpath.juiser.jwt.StringCollectionClaimResolver;
import com.stormpath.juiser.model.User;
import com.stormpath.juiser.spring.jwt.ClaimsExpressionEvaluator;
import com.stormpath.juiser.spring.security.authentication.ClaimsGrantedAuthoritiesResolver;
import com.stormpath.juiser.spring.security.authentication.HeaderAuthenticationProvider;
import com.stormpath.juiser.spring.security.authentication.JwsToUserDetailsConverter;
import com.stormpath.juiser.spring.security.config.SpringSecurityJwtConfig;
import com.stormpath.juiser.spring.security.user.SecurityContextUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
@Configuration
@ConditionalOnClass({AuthenticationProvider.class})
@ConditionalOnProperty(name = {"juiser.enabled", "juiser.security.enabled", "security.basic.enabled", "management.security.enabled"}, matchIfMissing = true)
@AutoConfigureAfter(JuiserAutoConfiguration.class)
public class JuiserSecurityAutoConfiguration {

    @Autowired
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private ForwardedHeaderConfig<SpringSecurityJwtConfig> config;

    @Autowired
    @Qualifier("juiserJwsClaimsExtractor")
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private Function<String, Claims> claimsExtractor;

    @Autowired
    @Qualifier("juiserJwtClaimsUserFactory")
    @SuppressWarnings("SpringJavaAutowiringInspection")
    private Function<Claims, User> claimsUserFactory;

    @Bean
    @ConditionalOnMissingBean(name = "juiserJwtDataExtractor")
    public Function<Claims, Collection<String>> juiserGrantedAuthoritiesClaimResolver() {

        final SpringSecurityJwtConfig jwt = config.getJwt();

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

    @Bean
    @ConditionalOnMissingBean
    public User juiserUser() {
        return new SecurityContextUser();
    }
}

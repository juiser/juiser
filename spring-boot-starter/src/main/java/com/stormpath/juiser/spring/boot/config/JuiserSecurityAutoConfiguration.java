package com.stormpath.juiser.spring.boot.config;

import com.stormpath.juiser.io.ResourceLoader;
import com.stormpath.juiser.jwt.FallbackSigningKeyResolver;
import com.stormpath.juiser.jwt.JwsClaimsExtractor;
import com.stormpath.juiser.jwt.StringCollectionClaimResolver;
import com.stormpath.juiser.jwt.config.ConfigJwkResolver;
import com.stormpath.juiser.jwt.config.JwkConfig;
import com.stormpath.juiser.jwt.config.JwtConfig;
import com.stormpath.juiser.model.DefaultMapUserFactory;
import com.stormpath.juiser.model.User;
import com.stormpath.juiser.spring.io.SpringResourceLoader;
import com.stormpath.juiser.spring.jwt.ClaimsExpressionEvaluator;
import com.stormpath.juiser.spring.security.authentication.ClaimsGrantedAuthoritiesResolver;
import com.stormpath.juiser.spring.security.authentication.HeaderAuthenticationProvider;
import com.stormpath.juiser.spring.security.authentication.JwsToUserDetailsConverter;
import com.stormpath.juiser.spring.security.config.SpringSecurityJwtConfig;
import com.stormpath.juiser.spring.security.user.SecurityContextUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
@SuppressWarnings("Duplicates")
@Configuration
@ConditionalOnClass({AuthenticationProvider.class})
@ConditionalOnProperty(name = {"juiser.enabled", "juiser.security.enabled"}, matchIfMissing = true)
@EnableConfigurationProperties(ForwardedHeaderConfig.class)
public class JuiserSecurityAutoConfiguration {

    @Autowired
    private ForwardedHeaderConfig forwardedHeaderConfig = new ForwardedHeaderConfig();

    @Autowired(required = false)
    private SigningKeyResolver signingKeyResolver;

    @Autowired
    private ApplicationContext appCtx;

    @Bean
    @ConditionalOnMissingBean(name = "juiserForwardedUserJwtSigningKey")
    public Key juiserForwardedUserJwtSigningKey() {
        return null;
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserForwardedUserJwtSigningKeyResolver")
    public SigningKeyResolver juiserForwardedUserJwtSigningKeyResolver() {
        return this.signingKeyResolver;
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserJwsClaimsExtractor")
    public Function<String, Claims> juiserJwsClaimsExtractor() {

        final JwtConfig jwt = forwardedHeaderConfig.getJwt();
        final JwkConfig jwk = jwt.getKey();

        boolean keyEnabled = jwt.isEnabled() && jwk.isEnabled();
        Key key = null;

        if (keyEnabled) {

            ResourceLoader resourceLoader = new SpringResourceLoader(appCtx);

            ConfigJwkResolver keyFactory = new ConfigJwkResolver(resourceLoader);

            key = keyFactory.apply(jwk);
        }

        SigningKeyResolver resolver = juiserForwardedUserJwtSigningKeyResolver();

        if (keyEnabled && key == null && resolver == null) {
            String msg = "JWT signature validation is enabled, but no SigningKeyResolver or default/fallback key has " +
                "been configured.";
            throw new IllegalArgumentException(msg);
        }

        JwsClaimsExtractor extractor;

        if (resolver != null) {
            if (key != null) {
                resolver = new FallbackSigningKeyResolver(resolver, key);
            }
            extractor = new JwsClaimsExtractor(resolver);
        } else {
            if (key != null) {
                extractor = new JwsClaimsExtractor(key);
            } else {
                extractor = new JwsClaimsExtractor();
            }
        }

        Long allowedClockSkewSeconds = jwt.getAllowedClockSkewSeconds();
        extractor.setAllowedClockSkewSeconds(allowedClockSkewSeconds);

        return extractor;
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserJwtClaimsUserFactory")
    public Function<Map<String, ?>, User> juiserJwtUserDataResolver() {
        return new DefaultMapUserFactory();
    }

    @SuppressWarnings("unchecked")
    @Bean
    @ConditionalOnMissingBean(name = "juiserJwtClaimsUserFactory")
    public Function<Claims, User> juiserJwtClaimsUserFactory() {

        final SpringSecurityJwtConfig jwt = forwardedHeaderConfig.getJwt();

        final Function<Map<String, ?>, User> juiserJwtUserDataResolver = juiserJwtUserDataResolver();

        final String userClaimName = jwt.getUserClaimName();

        return claims -> {
            Map<String, ?> data = claims;

            if (Strings.hasText(userClaimName) && claims.containsKey(userClaimName)) {
                Object value = claims.get(userClaimName);
                if (value != null && value instanceof Map) {
                    data = (Map<String, ?>) value;
                }
            } else if (claims.containsKey("user")) {
                Object value = claims.get("user");
                if (value != null && value instanceof Map) {
                    data = (Map<String, ?>) value;
                }
            }

            return juiserJwtUserDataResolver.apply(data);
        };
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserJwtDataExtractor")
    public Function<Claims, Collection<String>> juiserGrantedAuthoritiesClaimResolver() {

        final SpringSecurityJwtConfig jwt = forwardedHeaderConfig.getJwt();

        String expression = jwt.getGrantedAuthoritiesExpression();

        if (!Strings.hasText(expression)) {
            expression = "['user']['groups']['items'].![get('name')]";
        }

        return new StringCollectionClaimResolver(new ClaimsExpressionEvaluator(expression), false);
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserHeaderValueUserDetailsConverter")
    public Function<String, UserDetails> juiserHeaderValueUserDetailsConverter() {

        Function<String, Claims> claimsExtractor = juiserJwsClaimsExtractor();
        Function<Claims, User> claimsUserFactory = juiserJwtClaimsUserFactory();
        Function<Claims, Collection<? extends GrantedAuthority>> grantedAuthoritiesResolver =
            new ClaimsGrantedAuthoritiesResolver(juiserGrantedAuthoritiesClaimResolver());

        return new JwsToUserDetailsConverter(
            claimsExtractor,
            claimsUserFactory,
            grantedAuthoritiesResolver);
    }

    @Bean
    @ConditionalOnMissingBean(name = "juiserAuthenticationProvider")
    public AuthenticationProvider juiserAuthenticationProvider() {
        Function<String, UserDetails> converter = juiserHeaderValueUserDetailsConverter();
        return new HeaderAuthenticationProvider(converter);
    }

    @Bean
    @ConditionalOnMissingBean
    public User juiseUser() {
        return new SecurityContextUser();
    }
}

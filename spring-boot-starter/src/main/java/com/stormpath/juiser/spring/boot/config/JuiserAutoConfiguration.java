package com.stormpath.juiser.spring.boot.config;

import com.stormpath.juiser.io.ResourceLoader;
import com.stormpath.juiser.jwt.FallbackSigningKeyResolver;
import com.stormpath.juiser.jwt.JwsClaimsExtractor;
import com.stormpath.juiser.jwt.config.ConfigJwkResolver;
import com.stormpath.juiser.jwt.config.JwkConfig;
import com.stormpath.juiser.jwt.config.JwtConfig;
import com.stormpath.juiser.model.DefaultMapUserFactory;
import com.stormpath.juiser.model.User;
import com.stormpath.juiser.spring.io.SpringResourceLoader;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SigningKeyResolver;
import io.jsonwebtoken.lang.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.Key;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings({"SpringAutowiredFieldsWarningInspection", "SpringJavaAutowiringInspection"})
@Configuration
@ConditionalOnProperty(name = {"juiser.enabled"}, matchIfMissing = true)
public class JuiserAutoConfiguration {

    @Autowired
    private ForwardedHeaderConfig forwardedHeaderConfig;

    @Autowired
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
    @ConditionalOnMissingBean(name = "juiserForwardedUserJwsClaimsExtractor")
    public Function<String, Claims> juiserForwardedUserJwsClaimsExtractor() {

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

        final JwtConfig jwt = forwardedHeaderConfig.getJwt();

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
}

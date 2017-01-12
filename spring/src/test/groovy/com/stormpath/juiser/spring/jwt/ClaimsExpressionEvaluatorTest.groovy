package com.stormpath.juiser.spring.jwt

import com.stormpath.juiser.jwt.JwsClaimsExtractor
import com.stormpath.juiser.jwt.StringClaimResolver
import com.stormpath.juiser.jwt.StringCollectionClaimResolver
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.impl.crypto.MacProvider
import org.junit.Test

import java.security.Key

import static org.junit.Assert.assertEquals

/**
 * @since 0.1.0
 */
class ClaimsExpressionEvaluatorTest {

    private static Key key = MacProvider.generateKey(); //for testing only

    @Test
    void testSubClaim() {

        String jws = Jwts.builder().setSubject("jsmith").signWith(SignatureAlgorithm.HS512, key).compact();

        Claims claims = new JwsClaimsExtractor(key.getEncoded()).apply(jws)

        def fn = new StringClaimResolver(new ClaimsExpressionEvaluator("['sub']"))

        assertEquals 'jsmith', fn.apply(claims)

        fn = new StringClaimResolver(new ClaimsExpressionEvaluator("subject"))

        assertEquals 'jsmith', fn.apply(claims)
    }

    @Test
    void testNestedUsername() {

        def user = ["username": "jsmith"]

        String jws = Jwts.builder().claim("user", user).signWith(SignatureAlgorithm.HS512, key).compact();

        Claims claims = new JwsClaimsExtractor(key).apply(jws)

        def fn = new StringClaimResolver(new ClaimsExpressionEvaluator("['user']['username']"))

        assertEquals 'jsmith', fn.apply(claims)
    }

    @Test
    void testGrantedAuthorities1() { //test structures like this:  user.groups.items[*].name

        def user = [
                "username": "jsmith",
                "groups"  : [
                        "items": [
                                ["name": "foo"],
                                ["name": "bar"]
                        ]
                ]
        ]

        String jws = Jwts.builder().claim("user", user).signWith(SignatureAlgorithm.HS512, key).compact();

        Claims claims = new JwsClaimsExtractor(key).apply(jws)

        String spel = "['user']['groups']['items'].![get('name')]"

        def value = new StringCollectionClaimResolver(new ClaimsExpressionEvaluator(spel), false).apply(claims)

        assertEquals(['foo', 'bar'], value)
    }

    @Test
    void testGrantedAuthorities2() { //test structures like this:  user.groups[*].name

        def user = [
                "username": "jsmith",
                "groups"  : [
                        ["name": "foo"],
                        ["name": "bar"]
                ]
        ]

        String jws = Jwts.builder().claim("user", user).signWith(SignatureAlgorithm.HS512, key).compact();

        Claims claims = new JwsClaimsExtractor(key).apply(jws)

        String spel = "['user']['groups'].![get('name')]"

        def value = new StringCollectionClaimResolver(new ClaimsExpressionEvaluator(spel), false).apply(claims)

        assertEquals(['foo', 'bar'], value)
    }

    @Test
    void testGrantedAuthorities3() { //test structures like this:  user.groups[name]

        def user = [
                "username": "jsmith",
                "groups"  : ['foo', 'bar']
        ]

        String jws = Jwts.builder().claim("user", user).signWith(SignatureAlgorithm.HS512, key).compact();

        Claims claims = new JwsClaimsExtractor(key).apply(jws)

        String spel = "['user']['groups']"

        def value = new StringCollectionClaimResolver(new ClaimsExpressionEvaluator(spel), false).apply(claims)

        assertEquals(['foo', 'bar'], value)
    }


}

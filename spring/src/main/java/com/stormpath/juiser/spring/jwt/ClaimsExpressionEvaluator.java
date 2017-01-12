package com.stormpath.juiser.spring.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class ClaimsExpressionEvaluator implements Function<Claims, Object> {

    private final String expressionString;
    private final Expression expression;

    public ClaimsExpressionEvaluator(String spelExpression) {
        Assert.hasText(spelExpression, "spelExpression argument cannot be null or empty.");
        ExpressionParser parser = new SpelExpressionParser();
        this.expressionString = spelExpression;
        this.expression = parser.parseExpression(spelExpression);
    }

    @Override
    public Object apply(Claims claims) {
        return expression.getValue(claims);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + expressionString + "]";
    }
}

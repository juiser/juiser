/*
 * Copyright 2017 Les Hazlewood and the respective Juiser contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.juiser.spring.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.util.Assert;

import java.util.function.Function;

/**
 * @since 1.0.0
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

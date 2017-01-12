package com.stormpath.juiser.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @since 0.1.0
 */
public class StringCollectionClaimResolver extends ClaimCollectionResolver<String> {

    public StringCollectionClaimResolver(Function<Claims, ?> delegate, boolean resultRequired) {
        super(delegate, resultRequired);
    }

    @Override
    protected Collection<String> toTypedValue(Object value, Claims claims) {
        if (value instanceof Collection) {
            Collection c = (Collection) value;
            if (Collections.isEmpty(c)) {
                return java.util.Collections.emptyList();
            }
            List<String> strings = new ArrayList<>(c.size());
            for (Object o : c) {
                String s = String.valueOf(o);
                strings.add(s);
            }

            return strings;
        }

        String s = String.valueOf(value);
        return java.util.Collections.singletonList(s);
    }
}

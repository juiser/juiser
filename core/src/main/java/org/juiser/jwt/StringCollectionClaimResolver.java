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
package org.juiser.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.lang.Collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

/**
 * @since 1.0.0
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

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
package org.juiser.spring.io;

import org.juiser.io.DefaultResource;
import org.juiser.io.Resource;
import org.juiser.io.ResourceLoader;

import java.io.IOException;

/**
 * @since 0.1.0
 */
public class SpringResourceLoader implements ResourceLoader {

    private final org.springframework.core.io.ResourceLoader loader;

    public SpringResourceLoader(org.springframework.core.io.ResourceLoader loader) {
        this.loader = loader;
    }

    public Resource getResource(String path) throws IOException {
        org.springframework.core.io.Resource resource = loader.getResource(path);
        return new DefaultResource(resource.getInputStream(), resource.getDescription());
    }
}

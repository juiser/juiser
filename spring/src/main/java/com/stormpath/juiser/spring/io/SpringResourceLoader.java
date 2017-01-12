package com.stormpath.juiser.spring.io;

import com.stormpath.juiser.io.DefaultResource;
import com.stormpath.juiser.io.Resource;
import com.stormpath.juiser.io.ResourceLoader;

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

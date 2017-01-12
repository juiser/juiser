package com.stormpath.juiser.io;

import java.io.IOException;

/**
 * @since 0.1.0.
 */
public interface ResourceLoader {

    Resource getResource(String path) throws IOException;
}

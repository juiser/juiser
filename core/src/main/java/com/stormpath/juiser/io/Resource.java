package com.stormpath.juiser.io;

import java.io.InputStream;

/**
 * @since 0.1.0
 */
public interface Resource {

    InputStream getInputStream();

    String getName();
}

package io.github.zebalu.badidea.chdir.native_impl.jna;

import com.sun.jna.Library;

/**
 * Mapping for <code>libc</code>'s <code>chdir</code> method.
 */
public interface LibcChDir extends Library {
    /**
     * calls <code>libc</code>'s <code>chdir</code> method
     * @param path the new path
     * @return 0 on success
     */
    int chdir(String path);
}

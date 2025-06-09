package io.github.zebalu.badidea.chdir.native_impl.jna;

import com.sun.jna.Library;

/**
 * Mapping for <code>msvcrt</code>'s <code>_chdir</code> method.
 */
public interface MsvcrtChDir extends Library {
    /**
     * Call's <code>msvcrt</code>'s <code>_chdir</code> method
     * @param path the new path
     * @return 0 on success
     */
    int _chdir(String path);
}

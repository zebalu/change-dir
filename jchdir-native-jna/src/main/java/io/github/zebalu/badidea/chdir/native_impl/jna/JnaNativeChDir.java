package io.github.zebalu.badidea.chdir.native_impl.jna;

import io.github.zebalu.badidea.chdir.NativeChangeDir;
import io.github.zebalu.badidea.chdir.util.OsUtil;

/**
 * Changing directory through JNA library. Should not be used, only through the {@link io.github.zebalu.badidea.chdir.ChangeDir} instance.
 */
public class JnaNativeChDir implements NativeChangeDir {

    private final CommonChDir chDir;

    /**
     * Creates an instance with an operating system specific {@link CommonChDir} impplementation.
     */
    public JnaNativeChDir() {
        chDir = OsUtil.isWindows() ? new WindowsChDir() : new UnixChDir();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changeDir(String dir) {
        return chDir.chdir(dir);
    }

    /**
     * 4 as preference number
     * @return 4 always
     */
    @Override
    public int preference() {
        return 4;
    }
}

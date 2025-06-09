package io.github.zebalu.badidea.chdir.native_impl.jna;

import com.sun.jna.Native;
import io.github.zebalu.badidea.chdir.util.LazyInit;

class UnixChDir implements CommonChDir {
    private static final LazyInit<LibcChDir> libcChDir = new LazyInit<LibcChDir>(()->Native.load("c", LibcChDir.class));

    @Override
    public boolean chdir(String path) {
        return 0 == libcChDir.get().chdir(path);
    }
}

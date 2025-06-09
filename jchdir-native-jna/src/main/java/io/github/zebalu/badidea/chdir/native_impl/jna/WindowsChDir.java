package io.github.zebalu.badidea.chdir.native_impl.jna;

import com.sun.jna.Native;
import io.github.zebalu.badidea.chdir.util.LazyInit;

class WindowsChDir implements CommonChDir {
    private static final LazyInit<MsvcrtChDir> msvcrtChDir = new LazyInit<>(()->Native.load("msvcrt", MsvcrtChDir.class));

    @Override
    public boolean chdir(String path) {
        return 0 == msvcrtChDir.get()._chdir(path);
    }
}

module change.dir.jchdir.native_impl.jna {
    requires java.base;
    requires com.sun.jna;
    requires change.dir.jchdir.main;
    opens io.github.zebalu.badidea.chdir.native_impl.jna to com.sun.jna;
    provides io.github.zebalu.badidea.chdir.NativeChangeDir with io.github.zebalu.badidea.chdir.native_impl.jna.JnaNativeChDir;
}
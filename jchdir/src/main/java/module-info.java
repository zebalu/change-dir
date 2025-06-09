/**
 * Implements an easy to use global "change current working directory" logic.
 */
@SuppressWarnings("module")
module change.dir.jchdir.main {
    requires java.base;
    exports io.github.zebalu.badidea.chdir;
    exports io.github.zebalu.badidea.chdir.util to change.dir.jchdir.native_impl.jna, change.dir.jchdir.native_impl.jni, change.dir.jchdir.native_impl.ffm;
    uses io.github.zebalu.badidea.chdir.NativeChangeDir;
}

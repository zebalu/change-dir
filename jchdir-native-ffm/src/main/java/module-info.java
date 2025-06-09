import io.github.zebalu.badidea.chdir.NativeChangeDir;
import io.github.zebalu.badidea.chdir.native_impl.ffm.FfmChangeDir;

/** Java Foreign Function and Memory API based implementation of {@link io.github.zebalu.badidea.chdir.NativeChangeDir} as a service.*/
module change.dir.jchdir.native_impl.ffm {
    requires change.dir.jchdir.main;
    provides NativeChangeDir with FfmChangeDir;
}
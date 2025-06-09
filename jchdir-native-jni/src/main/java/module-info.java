import io.github.zebalu.badidea.chdir.NativeChangeDir;
import io.github.zebalu.badidea.chdir.native_impl.jni.JniNativeChangeDir;

module change.dir.jchdir.native_impl.jni {
    requires change.dir.jchdir.main;
    provides NativeChangeDir with JniNativeChangeDir;
}
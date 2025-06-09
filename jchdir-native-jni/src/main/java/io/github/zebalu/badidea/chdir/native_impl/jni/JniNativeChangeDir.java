/*
Copyright 2025 Balázs Zaicsek

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package io.github.zebalu.badidea.chdir.native_impl.jni;

import io.github.zebalu.badidea.chdir.NativeChangeDir;
import io.github.zebalu.badidea.chdir.util.LazyInit;
import io.github.zebalu.badidea.chdir.util.OsUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * JNI based implementation of the {@link NativeChangeDir} interface. During first usage it installs the required .so
 * file or .dll to <code>temo</code> folder in cases it is not there already.
 * Should not be used directly only through the {@link io.github.zebalu.badidea.chdir.ChangeDir} instance.
 */
public class JniNativeChangeDir implements NativeChangeDir {
    private static final LazyInit<JniChDir> INSTANCE = new LazyInit<>(() -> {
        loadLibrary();
        return new JniChDir();
    });

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean changeDir(String dir) {
        return 0 == INSTANCE.get().chDir(dir);
    }

    /**
     * Returns 1 as preference number
     * @return always 1
     */
    @Override
    public int preference() {
        return 1;
    }

    private static void loadLibrary() {
        String toLoad = getOrCreateLibPath();
        System.load(toLoad);
    }

    private static String getOrCreateLibPath() {
        String name = OsUtil.isWindows() ? "jchdir_jni.dll" : "libjchdir_jni.so";
        String tmp = System.getProperty("java.io.tmpdir");
        Path libPath = Path.of(tmp, name);
        if (!libPath.toFile().exists()) {
            copyLibrary(name, libPath);
        }
        System.out.println("lib: " + libPath);
        return libPath.toString();
    }

    private static void copyLibrary(String name, Path libPath) {
        try (InputStream is = JniNativeChangeDir.class.getResourceAsStream("/" + name)) {
            if (null == is) {
                throw new IllegalStateException("Lib file (/" + name + ") is missing from classpath");
            }
            Files.copy(is, libPath);
        } catch (IOException ioe) {
            throw new IllegalStateException("Can not copy lib file (/" + name + ") to " + libPath, ioe);
        }
    }
}

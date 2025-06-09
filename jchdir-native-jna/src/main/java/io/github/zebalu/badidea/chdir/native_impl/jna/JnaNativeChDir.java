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

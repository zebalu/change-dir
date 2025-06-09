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

package io.github.zebalu.badidea.chdir.native_impl.ffm;

import io.github.zebalu.badidea.chdir.NativeChangeDir;
import io.github.zebalu.badidea.chdir.util.LazyInit;
import io.github.zebalu.badidea.chdir.util.OsUtil;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

/**
 * Accessing platform dependent change directory functionality based on operating system.
 * On Windows it uses <code>msvcrt</code>'s <code>_chdir</code> on other systems it uses
 * <code>libc</code>'s <code>chdir</code> method.
 */
public class FfmChangeDir implements NativeChangeDir {

    private static final LazyInit<Arena> SHARED_ARENA = new LazyInit<>(Arena::ofAuto);

    private static final LazyInit<MethodHandle> METHOD_HANDLE = new LazyInit<>(() -> {
        boolean isWindows = OsUtil.isWindows();
        String name = isWindows ? "_chdir" : "chdir";
        SymbolLookup lookup;
        if (isWindows) {
            lookup = SymbolLookup.libraryLookup("msvcrt", SHARED_ARENA.get());
        } else {
            lookup = SymbolLookup.libraryLookup("c", SHARED_ARENA.get());
        }
        MemorySegment methodAddress = lookup.find(name).orElseThrow();
        return Linker.nativeLinker().downcallHandle(methodAddress, FunctionDescriptor.of(ValueLayout.JAVA_INT, ValueLayout.ADDRESS));
    });

    /**
     * Creates an instance of {@link FfmChangeDir} without any explicit initialisation. If any previous instances
     * have initialised the shared global state, the same state will be used by this instance as well.
     * Global state is only initialised up on first use.
     */
    public FfmChangeDir() {
    }

    /**
     * Call's native change dir logic through FFM API.
     * @param dir the absolute path of a directory.
     * @return <code>true</code> if change was successful.
     * @throws IllegalStateException in case underlying native access throws exception.
     */
    @Override
    public boolean changeDir(String dir) {
        try {
            MemorySegment cString = SHARED_ARENA.get().allocateFrom(dir);
            int result = (int) METHOD_HANDLE.get().invokeExact(cString);
            return 0 == result;
        } catch (Throwable t) {
            throw new IllegalStateException("Could not call native function", t);
        }
    }

    /**
     * Returns 22 as preference number
     *
     * @return 22 always
     */
    @Override
    public int preference() {
        return 22;
    }
}

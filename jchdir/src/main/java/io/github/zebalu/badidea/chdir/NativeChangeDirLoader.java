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

package io.github.zebalu.badidea.chdir;

import io.github.zebalu.badidea.chdir.util.LazyInit;

import java.util.Comparator;
import java.util.ServiceLoader;

class NativeChangeDirLoader {

    private static final LazyInit<ServiceLoader<NativeChangeDir>> LOADER = new LazyInit<>(()->ServiceLoader.load(NativeChangeDir.class));
    private static final LazyInit<NativeChangeDir> INSTANCE = new LazyInit<>(NativeChangeDirLoader::loadGreatestPreference);
    private static final Comparator<? super ServiceLoader.Provider<NativeChangeDir>> PREFERENCE_COMPARATOR = Comparator.comparingInt(p->p.get().preference());


    static NativeChangeDir getInstance() {
        return INSTANCE.get();
    }

    private static NativeChangeDir loadGreatestPreference() {
        return LOADER.get().stream().max(PREFERENCE_COMPARATOR).orElse(new ServiceLoader.Provider<>() {
            @Override
            public Class<? extends NativeChangeDir> type() {
                return NoNativeChangeDir.class;
            }

            @Override
            public NativeChangeDir get() {
                return new NoNativeChangeDir();
            }
        }).get();
    }

    private static class NoNativeChangeDir implements NativeChangeDir {

        @Override
        public boolean changeDir(String dir) {
            return false;
        }

        @Override
        public int preference() {
            return 0;
        }
    }
}

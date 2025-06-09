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

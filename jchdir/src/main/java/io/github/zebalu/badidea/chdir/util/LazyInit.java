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

package io.github.zebalu.badidea.chdir.util;

import java.util.function.Supplier;

/**
 * Centralised logic to push variable initialisation to the first moment of use. This class is Thread-safe.
 *
 * @param <T> the type of the stored value
 */
public final class LazyInit<T> {
    private volatile T value;
    private Supplier<T> supplier;

    /**
     * Creates a lazy init instance with the provided supplier logic, that is only executed, if a variable is forst exexuted.
     * After usage reference to {@link Supplier} is freed up.
     *
     * @param supplier the initialisation logic
     * @throws IllegalArgumentException in case supplier is null
     */
    public LazyInit(Supplier<T> supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier can not be null!");
        }
        this.supplier = supplier;
    }

    /**
     * Thread-safe way of getting or initialising the underlying value. It uses an optimistic lock mechanism, to only block if initialisation is necessary.
     *
     * @return the initialised value
     * @throws IllegalStateException in case supplier return null during initialisation
     */
    public T get() {
        T local = value;
        if (local == null) {
            synchronized (this) {
                local = value;
                if (local == null) {
                    value = local = supplier.get();
                    if(local == null) {
                        throw new IllegalStateException("Supplier has returned null, initialisation ahs failed");
                    }
                    supplier = null;
                }
            }
        }
        return local;
    }
}

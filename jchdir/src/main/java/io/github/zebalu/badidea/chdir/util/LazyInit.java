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

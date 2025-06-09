package io.github.zebalu.badidea.chdir.util;

import java.util.Locale;

/**
 * Utility class to collect OS related methods.
 */
public final class OsUtil {
    private OsUtil() {
        throw new IllegalAccessError("Utility class");
    }

    /**
     * Checks whether the actual OS is Windows or not.
     * @return true if JVM is running on Windows.
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).contains("windows");
    }
}

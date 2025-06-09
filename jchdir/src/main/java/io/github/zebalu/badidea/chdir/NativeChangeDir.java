package io.github.zebalu.badidea.chdir;

/**
 * Interface abstraction to select the best suited native change dir implementation.
 */
public interface NativeChangeDir {
    /**
     * Calls out to a native solution to change the current working directory of JVM.
     *
     * @param dir the new working directory (must be absolute path, to existing directory)
     * @return <code>true</code> in case the directoy change was succesful.
     */
    boolean changeDir(String dir);

    /**
     * The preference helps find the best available implementation of available services. The higher the number, the better the more likely to be selected.
     * Built in implementations:
     * <ol>
     *     <li>0 --  No implementation</li>
     *     <li>1 -- JNI based implementation</li>
     *     <li>4 -- JNA based implementation</li>
     *     <li>22 -- FFM based implementation</li>
     * </ol>
     * If you add your implementation, aim above those that are on the classpath.
     *
     * @return preference code
     */
    int preference();
}

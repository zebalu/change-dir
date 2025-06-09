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

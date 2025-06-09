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

import io.github.zebalu.badidea.chdir.NativeChangeDir;
import io.github.zebalu.badidea.chdir.native_impl.ffm.FfmChangeDir;

/** Java Foreign Function and Memory API based implementation of {@link io.github.zebalu.badidea.chdir.NativeChangeDir} as a service.*/
module change.dir.jchdir.native_impl.ffm {
    requires change.dir.jchdir.main;
    provides NativeChangeDir with FfmChangeDir;
}
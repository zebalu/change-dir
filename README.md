This is a simple library to change working directory.

## How to use
Add the dependency in maven:
```xml
<dependency>
    <groupId>io.github.zebalu</groupId>
    <artifactId>jchdir</artifactId>
    <version>0.0.2-RELEASE</version>
</dependency>
```

or in gradle:
```groovy
implementation 'io.github.zebalu:jchdir:0.0.2-RELEASE'
```

Then you can:
```java
import io.github.zebalu.badidea.chdir.ChangeDir;
public class Example {
    public static void main(String[] args) {
        ChangeDir instance = ChangeDir.getInstance();
        instance.changeDir("../other_dir");
    }
}
```

What is the effect? A simple relative file / path created
__after__ the directory change will have a different root directory.

But you need to add:
```shell
--add-opens java.base/java.io=change.dir.jchdir.main --add-opens java.base/sun.nio.fs=change.dir.jchdir.main
```

as JVM commands (if used as module), or

```shell
--add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/sun.nio.fs=ALL-UNNAMED
```

if used on the class-path. (Because it heavily uses reflection.)

But will JVM change dir? No. For that you need a native implementation as well.

Just add `jchdir-native-ffm` _or_ `jchdir-native-jna` _or_ `jchdir-native-jni` to your dependencies,
and native change dir will be executed as well.

Do you need these extra libs? Only if you also care about the underlying JVM changing dir.
Why would you do that? Because you might also want to effect what native libraries are loaded.
(It is a bad practice to load libraries with relative path, but it is a possibility.)

## Should you use this project?

__NO__! Why not? changing directory can mess up your JVM. Then why does this project exists?
I was challenged to solve this problem, so I have done it. This does not mean you should use it...



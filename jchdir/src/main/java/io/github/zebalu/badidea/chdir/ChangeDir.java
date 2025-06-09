package io.github.zebalu.badidea.chdir;

import io.github.zebalu.badidea.chdir.util.LazyInit;
import io.github.zebalu.badidea.chdir.util.OsUtil;

import java.io.File;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.lang.reflect.Field;
import java.lang.reflect.InaccessibleObjectException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Utility class to Change directory. It is designed to only be used through its static instance, that you can obtain by
 * <code>getInstance()</code> method.
 */
public final class ChangeDir {

    private static final System.Logger LOG = System.getLogger(ChangeDir.class.getName());
    private static final LazyInit<ChangeDir> LAZY_INSTANCE = new LazyInit<>(()->new ChangeDir(ChdirConfig.getInstance()));

    /**
     * Returns to you the only instance to be used.
     * @return the thread-safe {@link ChangeDir} instance
     */
    public static ChangeDir getInstance() {
        return LAZY_INSTANCE.get();
    }

    private final Lock lock;
    private final Level logLevel;
    private final NativeChangeDir nativeChangeDir;

    private String dir;
    private final Object fileObject;
    private final Field fileField;

    private final boolean asByteArray;
    private final Object pathObject;
    private final Field pathField;


    private ChangeDir(ChdirConfig config) {
        lock = new ReentrantLock(config.fairLock());
        logLevel = config.logLevel();
        nativeChangeDir = NativeChangeDirLoader.getInstance();
        System.out.println(nativeChangeDir.getClass().getName());
        dir = System.getProperty("user.dir");
        try {
            Field fileFs = File.class.getDeclaredField("FS");
            fileFs.setAccessible(true);
            fileObject = fileFs.get(null);
            fileField = fileObject.getClass().getDeclaredField("userDir");
            fileField.setAccessible(true);

            Path currentUserDir = Paths.get(dir);
            Field pathFs = currentUserDir.getClass().getDeclaredField("fs");
            pathFs.setAccessible(true);

            if(OsUtil.isWindows()) {
                pathObject = pathFs.get(currentUserDir);
                pathField = pathObject.getClass().getDeclaredField("defaultDirectory");
                pathField.setAccessible(true);
                asByteArray = false;
            } else {
                pathObject = pathFs.get(currentUserDir);
                Field[] declaredFields = pathObject.getClass().getDeclaredFields();
                Field found = null;
                for(int i=0; i<declaredFields.length && found == null; ++i) {
                    Field field = declaredFields[i];
                    if(field.getName().equals("userDir")) {
                        found =  field;
                        found.setAccessible(true);
                    }
                }
                if(found == null) {
                    found = findDefultDirectoryField(pathObject.getClass().getSuperclass(), 2);
                    asByteArray = true;
                } else {
                    asByteArray = false;
                }
                pathField = found;
            }
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException |
                 InaccessibleObjectException e) {
            LOG.log(logLevel, "probably missing jvm params: --add-opens java.base/java.io=change.dir.jchdir.main --add-opens java.base/sun.nio.fs=change.dir.jchdir.main");
            LOG.log(logLevel, "or: --add-opens java.base/java.io=ALL-UNNAMED --add-opens java.base/sun.nio.fs=ALL-UNNAMED");
            LOG.log(logLevel, "Can not get required fields to set", e);
            throw new IllegalStateException("Can not initialise", e);
        }
    }

    /**
     * Changes the current working directory to the one given directory. At first it turns the directory into an absolute path, if possible.
     * This method is thread-safe.
     * @param dir the new working directory
     * @throws IllegalArgumentException in case the specified directory is not available, does not exisits, or not a directory
     * @throws RuntimeException if can not change specified fields of underlying FileSystem abstraction
     */
    public void changeDir(String dir) {
        lock.lock();
        try {
            String toSet = ensureAbsolutePathToValidFolder(dir);
            this.dir = toSet;
            System.setProperty("user.dir", toSet);
            fileField.set(fileObject, toSet);
            if(!asByteArray) {
                pathField.set(pathObject, toSet);
            } else {
                pathField.set(pathObject, toSet.getBytes(StandardCharsets.UTF_8));
            }
            boolean nativeSuccess = nativeChangeDir.changeDir(dir);
            if(!nativeSuccess) {
                LOG.log(logLevel, "Can not change native directory");
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }

    private static String ensureAbsolutePathToValidFolder(String dir) {
        try {
            File absCanonFile = new File(dir).getCanonicalFile().getAbsoluteFile();
            if(!absCanonFile.exists()) {
                throw new IllegalArgumentException("You can not set current working directory to a non existing folder");
            }
            if(!absCanonFile.isDirectory()) {
                throw new IllegalArgumentException("You can not set current working directory to a regular file");
            }
            return absCanonFile.getPath();
        } catch (IOException ioe) {
            throw new IllegalArgumentException("Can not use path: "+dir, ioe);
        }
    }

    private static Field findDefultDirectoryField(Class<?> clazz, int depthRemaining) {
        if(clazz == null || depthRemaining <= 0) {
            throw new IllegalStateException("Not supported OS / File System.");
        }
        for(Field field : clazz.getDeclaredFields()) {
            if(field.getName().equals("defaultDirectory")) {
                field.setAccessible(true);
                return field;
            }
        }
        return findDefultDirectoryField(clazz.getSuperclass(), depthRemaining - 1);
    }

    /**
     * Changes the current working directory to the one given directory. At first it turns the directory into an absolute path, if possible.
     * This method is thread-safe.
     *
     * @param file the new working directory
     * @throws IllegalArgumentException in case the specified directory is not available, does not exisits, or not a directory
     * @throws RuntimeException if can not change specified fields of underlying FileSystem abstraction
     */
    public void changeDir(File file) {
        changeDir(file.getAbsolutePath());
    }

    /**
     * Changes the current working directory to the one given directory. At first it turns the directory into an absolute path, if possible.
     * This method is thread-safe.
     *
     * @param path the new working directory
     * @throws IllegalArgumentException in case the specified directory is not available, does not exisits, or not a directory
     * @throws RuntimeException if can not change specified fields of underlying FileSystem abstraction
     */
    public void changeDir(Path path) {
        changeDir(path.toAbsolutePath().toString());
    }

    /**
     * Returns the current working directory as String
     * This method is thread-safe.
     *
     * @return absolute path of current working directory
     */
    public String getCWD() {
        lock.lock();
        try {
            return dir;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Returns the current working directory as an absolute path
     * This method is thread-safe.
     *
     * @return the absolute file of current working directory.
     */
    public File getCWDFile() {
        return new File(getCWD());
    }

    /**
     * Returns the current working directory as an absolute path.
     * This method is thread-safe.
     *
     * @return the absolute path of the current working directory.
     */
    public Path getCWDPath() {
        return Path.of(getCWD());
    }

    public static void main(String[] args) {
        var inst = getInstance();
        File dotF = new File("");
        Path dotP = Path.of("");
        System.out.println(dotF.getAbsolutePath());
        System.out.println(dotP.toAbsolutePath());

        inst.changeDir("..");
        File dotF2 = new File("");
        Path dotP2 = Path.of("");
        System.out.println(dotF2.getAbsolutePath());
        System.out.println(dotP2.toAbsolutePath());
    }

}

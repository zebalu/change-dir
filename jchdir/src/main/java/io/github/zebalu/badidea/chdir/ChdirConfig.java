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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.System.Logger.Level;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import java.util.function.Consumer;

/**
 * Holds the configuration instance of the JCHDIR settings.
 * It can be configured with:
 * <ol>
 *     <li>nothing: default config</li>
 *     <li>a central file: ~/.jchdir.rc</li>
 *     <li>system wide environment properties</li>
 *     <li>jvm properties</li>
 * </ol>
 *
 * The strength order is: nothing (weakest) --&gt; to --&gt; jvm properties (strognest) as that one is directly linked to the started JVM.
 */
public final class ChdirConfig {

    /** The file it is used to load user settings from. Value: <code>~/.jchdir.rc</code>*/
    public static final File CONFIG_FILE = new File(new File(System.getProperty("user.home")), ".jchdir.rc");

    /** the key in config properties object to set log level. Value: {@value #PROPS_LOG_LEVEL} */
    public static final String PROPS_LOG_LEVEL = "logLevel";
    /** the key in config properties object to set lock fairness. Valu: {@value #PROPS_FAIR_LOCK} */
    public static final String PROPS_FAIR_LOCK = "fairLock";

    /** JVM property to set log level. Value: {@value #JVM_JCHDIR_LOG_LEVEL} */
    public static final String JVM_JCHDIR_LOG_LEVEL = "jchdir.logLevel";

    /** JVM property to set lock fairness. Value: {@value #JVM_JCHDIR_FAIR_LOCK} */
    public static final String JVM_JCHDIR_FAIR_LOCK = "jchdir.fairLock";

    /**Environment variable name to set log level. Value: {@value #SYSTEM_JCHDIR_LOGLEVEL} */
    public static final String SYSTEM_JCHDIR_LOGLEVEL = "JCHDIR_LOGLEVEL";
    /** Environment variable name to set fairness. Value: {@value #SYSTEM_JCHDIR_FAIRLOCK}*/
    public static final String SYSTEM_JCHDIR_FAIRLOCK = "JCHDIR_FAIRLOCK";

    private static ChdirConfig lazy_instance;

    private static String initialisationStackTrace;

    /**
     * Gets the ChdirConfig instance. Initialises it, if needed.
     * This method is thread-safe, always returns a value, and idempotent.
     *
     * @return the ChdirConfig instance.
     */
    public static ChdirConfig getInstance() {
        return getInstanceWithConfig(null);
    }

    /**
     * Trys to initialise a config with a custom setting. The config can only be initialised once / JVM.
     * @param config this lambda gets the current properties (merged default, user, env, and jvm properties) and can modify it.
     *               The modified object is then used to initialise a config. In case it is <code>null</code> behaves like getInstance.
     * @return the ChdirConfig instance
     * @throws IllegalStateException in case config is not null, and instance is already initialised
     */
    public static ChdirConfig getInstanceWithConfig(Consumer<Properties> config) {
        synchronized (ChdirConfig.class) {
            if (lazy_instance == null) {
                saveInitialisationStackTrace();
                Properties defaults = mergeAllProperties();
                if(config != null) {
                    config.accept(defaults);
                }
                lazy_instance = createFromProperties(defaults);
                return lazy_instance;
            } else if(config != null) {
                throw new IllegalStateException("Instance was already configured with stack trace: \n"+initialisationStackTrace);
            } else {
                return lazy_instance;
            }
        }
    }

    private final Level logLevel;
    private final boolean fairLock;

    private ChdirConfig(final Level logLevel, final boolean fairLock) {
        this.logLevel = logLevel;
        this.fairLock = fairLock;
    }

    /**
     * Returns the log level we want ot see {@link ChangeDir} logs on.
     * @return The requested log elvel. {@link Level#DEBUG} yb default.
     */
    public Level logLevel() {
        return logLevel;
    }

    /**
     * Determines to use a fair or unfair lock.
     * @return <code>false</code> by default.
     */
    public boolean fairLock() {
        return fairLock;
    }

    /**
     * Saves the current setup as user settings to ~/.jchdir.rc
     * @throws IOException in case it can not write the file
     */
    public void saveAsUserSettings() throws IOException {
        Properties currentProperties = new Properties();
        currentProperties.setProperty(PROPS_LOG_LEVEL, logLevel.toString());
        currentProperties.setProperty(PROPS_FAIR_LOCK, Boolean.toString(fairLock));
        synchronized (ChdirConfig.class) {
            try (FileWriter fw = new FileWriter(CONFIG_FILE)) {
                currentProperties.store(fw, "saved from stacktrace:\n"+createStackTraceString(2));
            }
        }
    }

    private static void saveInitialisationStackTrace() {
        initialisationStackTrace = createStackTraceString(3);
    }

    private static String createStackTraceString(int skipElements) {
        int counter = 1;
        StringBuilder sb = new StringBuilder();
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for(int i=skipElements; i<stackTraceElements.length; i++) {
            sb.append("\t\t");
            sb.append(counter);
            sb.append(".\t");
            sb.append(stackTraceElements[i]);
            sb.append("\n");
            ++counter;
        }
        return sb.toString();
    }

    private static Properties mergeAllProperties() {
        Properties props = createDefaultProps();
        loadUserConfgis(props);
        loadEnvironmentSettings(props);
        loadJvmProperties(props);
        return props;
    }

    private static ChdirConfig createFromProperties(Properties props) {
        Level logLevel = Level.valueOf(props.getProperty(PROPS_LOG_LEVEL));
        boolean fairLock = Boolean.parseBoolean(props.getProperty(PROPS_FAIR_LOCK));
        return new ChdirConfig(logLevel, fairLock);
    }

    /**
     * Reads the strongest config: jvm properties.
     * @param props the properties to update.
     */
    private static void loadJvmProperties(Properties props) {
        loadJvmProperty(props, JVM_JCHDIR_LOG_LEVEL, PROPS_LOG_LEVEL);
        loadJvmProperty(props, JVM_JCHDIR_FAIR_LOCK, PROPS_FAIR_LOCK);
    }

    private static void loadJvmProperty(Properties props, String jvmPorpKey, String storePropKey) {
        if (System.getProperties().containsKey(jvmPorpKey)) {
            props.setProperty(storePropKey, System.getProperty(jvmPorpKey));
        }
    }

    /**
     * Overrides the config with the settings from the environment variables.
     * @param props the properties to update
     */
    private static void loadEnvironmentSettings(Properties props) {
        loadEnvironmentSetting(props, SYSTEM_JCHDIR_LOGLEVEL, PROPS_LOG_LEVEL);
        loadEnvironmentSetting(props, SYSTEM_JCHDIR_FAIRLOCK, PROPS_FAIR_LOCK);
    }

    private static void loadEnvironmentSetting(Properties props, String envKey, String propsKey) {
        if(System.getenv().containsKey(envKey)) {
            props.setProperty(propsKey, System.getenv(envKey));
        }
    }

    /**
     * Overrides default values with user's own configuration from ~/.jchdir.rc if exists.
     *
     * @param props The properties object to update with the loaded config
     */
    private static void loadUserConfgis(Properties props) {
        if(CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE, StandardCharsets.UTF_8)) {
                props.load(reader);
            } catch (IOException ioe) {
                System.getLogger(ChdirConfig.class.getName()).log(Level.INFO, "Can not load config file: "+CONFIG_FILE.getAbsolutePath(), ioe);
            }
        }
    }

    /**
     * Creates the default config values. This is the weakest config.
     * @return a {@link Properties} object with all values set to the default values as Strign
     */
    private static Properties createDefaultProps() {
        Properties props = new Properties();
        props.setProperty(PROPS_LOG_LEVEL, "DEBUG");
        props.setProperty(PROPS_FAIR_LOCK, "false");
        return props;
    }
}

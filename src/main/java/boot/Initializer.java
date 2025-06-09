package boot;

public class Initializer {

    static volatile boolean initialized = false;

    synchronized static public void Initialized(String path) {

        if(initialized) return;

        // When used with non-servlet,argument will given to "configInitialized" method
        ConfigurationLoader.initialized(path);

        // Resources - from ResourceBundleClasses
        AssemblerInitializer.initialized();

        initialized = true;
    }

}

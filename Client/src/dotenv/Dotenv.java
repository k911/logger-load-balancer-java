package dotenv;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public final class Dotenv {
    private final static Logger logger = Logger.getLogger(Dotenv.class.getName());
    private String envFile;
    private boolean silent;

    public Dotenv() {
        this.envFile = ".env";
        this.silent = true;
    }

    public Dotenv(String envFile, boolean silent) {
        this.envFile = envFile;
        this.silent = silent;
    }

    public static void loadEnvironment() {
        if (System.getenv("APP_ENV") == null) {
            new Dotenv().load();
        }
    }

    /**
     * It load the env vars accordingly.
     */
    public void load() {
        try {
            Properties prop = new Properties();
            InputStream inputStream = new FileInputStream(envFile);
            prop.load(inputStream);
            Map<String, String> map = new LinkedHashMap<>(System.getenv());
            String key;
            for (Map.Entry entry : prop.entrySet()) {
                key = entry.getKey().toString();
                if (map.get(key) == null) {
                    if (!silent) {
                        logger.info("Loading env var " + key + " from " + envFile + ".");
                    }
                    map.put(key, entry.getValue().toString());
                } else if (!silent) {
                  logger.severe("Env var " + key + " is already set.");
                }
            }
            Dotenv.setEnv(map);
        } catch (IOException e) {
            logger.severe("It was not possible to load properties from '" + envFile + "'.");
        }
    }

    private static void setEnv(Map<String, String> newEnvs) {
        try {
            Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
            Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
            theEnvironmentField.setAccessible(true);
            Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
            env.putAll(newEnvs);
            Field theCaseInsensitiveEnvironmentField = processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
            theCaseInsensitiveEnvironmentField.setAccessible(true);
            Map<String, String> caseInsensitiveEnvs = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
            caseInsensitiveEnvs.putAll(newEnvs);
        } catch (NoSuchFieldException e) {
            try {
                Class[] classes = Collections.class.getDeclaredClasses();
                Map<String, String> env = System.getenv();
                for (Class cl : classes) {
                    if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
                        Field field = cl.getDeclaredField("m");
                        field.setAccessible(true);
                        Object obj = field.get(env);
                        Map<String, String> map = (Map<String, String>) obj;
                        map.clear();
                        map.putAll(newEnvs);
                    }
                }
            } catch (Exception e2) {
                throw new RuntimeException(e2);
            }
        } catch (Exception e1) {
            throw new RuntimeException(e1);
        }
    }

}

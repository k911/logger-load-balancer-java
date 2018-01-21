package utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dotenv.Dotenv;

public class AppUtils {
    private AppUtils() {}

    public static void loadEnvironment() {
        if (System.getenv("APP_ENV") == null) {
            new Dotenv().load();
        }
    }

    public static Gson buildGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss.S");
        gsonBuilder.excludeFieldsWithoutExposeAnnotation();
        gsonBuilder.serializeNulls();
        return gsonBuilder.create();
    }
}

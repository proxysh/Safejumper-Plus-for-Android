package apps.base.app.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import apps.base.app.BaseAppApplication;

public class ConfigManager {

    public static final int SAFE_JUMPER = 0;
    public static final int SHIELDTRA = 1;
    public static String activeUserName;
    public static String activePasswdOfUser;

    private static List<String> encryptionTypes = new ArrayList<>();

    public static List<String> getEncryptionType() {
        return encryptionTypes;
    }

    public static void setEncryptionTypes(Set<String> encryptionTypesSet) {
        encryptionTypes = new ArrayList<>();
        encryptionTypes.addAll(encryptionTypesSet);
    }

    public static String getUserPlanType(int type) {
        int serverListAPI = getField("serverListAPI");
        switch (serverListAPI) {
            default:
            case SAFE_JUMPER:
                return String.format("$%s Package", type);
            case SHIELDTRA:
                switch (type) {
                    default:
                    case 0:
                        return "Free Pack";
                    case 1:
                        return "Standard Pack";
                    case 2:
                        return "Premium Pack";
                }
        }
    }


    public final static <Type> Type getField(String fieldName) {
        try {
            Class aClass = Class.forName(BaseAppApplication.PACKAGE_NAME.replace(".beta", "") + ".BuildConfig");
            Field field = aClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (Type) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

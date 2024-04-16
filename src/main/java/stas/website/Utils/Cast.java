package stas.website.Utils;

import java.util.HashMap;
import java.util.Map;

public class Cast {
    
    public static Map<String, String> string_object_to_string_string(Map<String, Object> original) {
        Map<String, String> castedMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : original.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof String) {
                castedMap.put(entry.getKey(), (String) value);
            } else {
                // Handle the case where the object is not a String
                throw new IllegalArgumentException("Non-string value found for key: " + entry.getKey());
            }
        }
        return castedMap;
    }
    
}

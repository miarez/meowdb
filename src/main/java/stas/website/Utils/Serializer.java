package stas.website.Utils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class Serializer {
    
    public static byte[] serialize(
        Map<String, String> schema,
        Map<String, Object> record
    ) {

        ByteBuffer buffer = ByteBuffer.allocate(264); // 4 (int) + 256 (string) + 4 (int)
        for (Map.Entry<String, String> entry : schema.entrySet()) {
            String key = entry.getKey();
            String expectedType = entry.getValue();
            Object value = record.get(key);

            switch(expectedType){
                case "INT":
                    buffer.putInt((Integer) value); 
                    break;
                case "STRING":
                    byte[] eventBytes = ((String) value).getBytes(StandardCharsets.UTF_8);
                    buffer.put(eventBytes, 0, Math.min(eventBytes.length, 256));
                    buffer.position(260);
                    break;
            }
        }
        return buffer.array();
    }

    public static Map<String, Object> deserialize(
        Map<String, String> schema,
        byte[] data
    ) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        Map<String, Object> record = new HashMap<>();

        for (Map.Entry<String, String> entry : schema.entrySet()) {
            String key = entry.getKey();
            String expectedType = entry.getValue();
            switch(expectedType){
                case "INT":
                    record.put(key, buffer.getInt());
                    break;
                case "STRING":
                    byte[] stringBytes = new byte[256];
                    buffer.get(stringBytes);
                    record.put(key, new String(stringBytes, StandardCharsets.UTF_8).trim());
                    break;
            }
        }
        return record;
    }
    
}

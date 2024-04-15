package stas.website.Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import stas.website.Utils.Utils;

public class SQL {

    public SQL(){}

    public boolean table_exists(
        String table_name
    ){
        String fileName = "schema/" + table_name + ".json";
        File file = new File(fileName);
        if (file.exists()) {
           return true;
        }
        return false;

    }

    public boolean create_table(
        String table_name,
        Map<String, String> schema
    ) throws IOException{
        String fileName = "schema/" + table_name + ".json";

        if (table_exists(table_name)) {
            System.err.println("TABLE [" + table_name + "] already exists.");
            return false;
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(schema);
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(json);
            System.out.println("JSON has been written to the file successfully.");
        } catch (IOException e) {
            System.err.println("Error occurred while writing JSON to file: " + e.getMessage());
            return false;
        }

        return true;
    }


    public void drop_table(
        String table_name
    ) throws IOException {
        try {
            Files.delete(Paths.get("schema/" + table_name + ".json"));
            System.out.println("File deleted successfully.");
        } catch (IOException e) {
            System.err.println("Failed to DROP SCHEMA [" + table_name + "]");
        }
        try {
            Files.delete(Paths.get("data/" + table_name + ".json"));
            System.out.println("File deleted successfully.");
        } catch (IOException e) {
            System.err.println("Failed to DROP TABLE [" + table_name + "]");
        }
    }

    public boolean create_or_replace_table (
        String table_name,
        Map<String, String> schema
    ) throws IOException {

        if (table_exists(table_name)) {
            drop_table(table_name);
        }
        return create_table(table_name, schema);
    };
    
    public Map<String, String> get_schema(
        String table_name
    ) throws FileNotFoundException, IOException {
        Map<String, String> schema = new HashMap<>();
        String fileName = "schema/" + table_name + ".json";
        Gson gson = new Gson();

        // Use FileReader to read JSON file
        try (FileReader reader = new FileReader(fileName)) {
            // Deserialize JSON file to Java object (nested Maps)
            // Corrected: removed the duplicate variable declaration
            schema = gson.fromJson(reader, Map.class);
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());  // Updated to use System.out.println for simplicity
        }
        return schema;
    }

    public boolean insert(
        String table_name,
        Map<String, Object> record
    ) throws FileNotFoundException, IOException{

        Map<String, String> schema = get_schema(table_name);
        if(!validateRecord(record, schema)){
            return false;
        }

        byte[] record_serialized = serializeRecord(record);


        Map<String, Object> undo = deserializeRecord(record_serialized); 


        Utils.pp(undo);
     
        return true;
    }


    
    public static byte[] serializeRecord(Map<String, Object> record) {
        ByteBuffer buffer = ByteBuffer.allocate(264); // 4 (int) + 256 (string) + 4 (int)
        buffer.putInt((Integer) record.get("id")); // Assume id is always an Integer

        // Handle event as String
        String event = (String) record.get("event");
        byte[] eventBytes = event.getBytes(StandardCharsets.UTF_8);
        buffer.put(eventBytes, 0, Math.min(eventBytes.length, 256));
        buffer.position(260); // Move position to after string allocation (256 bytes from index 4)

        buffer.putInt((Integer) record.get("price")); // Assume price is always an Integer

        return buffer.array();
    }

    public static Map<String, Object> deserializeRecord(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        Map<String, Object> record = new HashMap<>();

        int id = buffer.getInt();
        record.put("id", id);

        byte[] stringBytes = new byte[256];
        buffer.get(stringBytes);
        String event = new String(stringBytes, StandardCharsets.UTF_8).trim();
        record.put("event", event);

        int price = buffer.getInt(); 
        record.put("price", price);
        return record;
    }

    // private void validate_record(){}


    private boolean validateRecord(
        Map<String, Object> record, 
        Map<String, String> schema
    ) {
        if(!record.keySet().equals(schema.keySet())){
            System.out.println("Error: Record Does Not Match Schema");
            return false;
        }

        for (Map.Entry<String, String> entry : schema.entrySet()) {
            String key = entry.getKey();
            String expectedType = entry.getValue();
            Object value = record.get(key);

            // Check for type consistency
            if (!isTypeValid(value, expectedType)) {
                System.out.println("Error: Value type for key '" + key + "' is invalid. Expected " + expectedType + ".");
                return false;
            }
        }
        return true; 
    }

    private static boolean isTypeValid(Object value, String type) {
        try {
            switch (type) {
                case "INT":
                    // Check if it's an instance of Integer
                    return value instanceof Integer;
                case "STRING":
                    // Check if it's an instance of String
                    return value instanceof String;
                default:
                    // Unsupported type
                    System.out.println("Unsupported type: " + type);
                    return false;
            }
        } catch (Exception e) {
            System.out.println("Type validation error: " + e.getMessage());
            return false;
        }
    }


}

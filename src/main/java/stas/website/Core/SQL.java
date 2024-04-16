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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Gatherer.Integrator;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import stas.website.Filter.Filter;
import stas.website.Utils.Utils;

public class SQL {

    private static final long blockSize     = 480;
    private static final long storageSize   = 480000;



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

        // validate the insert 
        Map<String, String> schema = get_schema(table_name);
        if(!validateRecord(record, schema)){
            return false;
        }

        String file_path = "data/" + table_name + ".db";

        // get the length of the current file
        RandomAccessFile dataStream = new RandomAccessFile(file_path, "rw");
        long fileSize = dataStream.length();

        // serialize
        byte[] serialized = serializeRecord(schema, record);
        long recordSize   = serialized.length;
        
        Map<String, Object> blockInfo = findSuitableBlock(recordSize, fileSize);

        // write the record
        dataStream.seek((int) blockInfo.get("insertionPoint"));
        dataStream.write(serialized);

        return true;
    }

    public Map<String, Object> findSuitableBlock(
        long recordSize,
        long fileSize
    ){
        return prepareNewBlock(recordSize, fileSize);
    }

    public Map<String, Object> prepareNewBlock(
        long recordSize,
        long fileSize
    ){

        int blockId = (int) (fileSize / blockSize);
        Utils.pp("BLOCKID : " + blockId);
        int spaceUsedInLastBlock = (int) (fileSize % blockSize);


        if(blockSize - spaceUsedInLastBlock < recordSize){
            blockId++;
            spaceUsedInLastBlock = 0;
        }

        // prepare block info
        int insertionPoint = (int) (blockId * blockSize) + spaceUsedInLastBlock;
        int blockSpaceUsed = (int) (spaceUsedInLastBlock + recordSize);


        Map<String, Object> blockInfo = new HashMap<>();
        blockInfo.put("id", blockId);
        blockInfo.put("insertionPoint", insertionPoint);
        blockInfo.put("block_used_space", blockSpaceUsed);
        return blockInfo;
    }

    
    public List<Map<String, Object>> read(
        String table_name,
        List<Filter> filters
    ) throws IOException{

        Map<String, String> schema = get_schema(table_name);

        List<Map<String, Object>> records = new ArrayList<>();

        String file_path = "data/" + table_name + ".db";

        try (RandomAccessFile dataStream = new RandomAccessFile(file_path, "rw")) {
            long fileSize = dataStream.length();

            dataStream.seek(0);


            while(dataStream.getFilePointer() < fileSize){

                byte[] block = new byte[(int) blockSize];
                try {
                    dataStream.readFully(block);
                    Map<String, Object> record = deserializeRecord(schema, block);
                    boolean addRecord = true;
                    if(!filters.isEmpty()){
    
                        for (Filter filter : filters) {
                            if(!filter.invoke(record)){
                                addRecord = false;
                            }
                        }
                    }
                    if(addRecord){
                        records.add(record);
                    }
                } catch (IOException e){
                    System.err.println("Can't read block");
                }
            }
        }
        return records;
    }


    
    public static byte[] serializeRecord(
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

    public static Map<String, Object> deserializeRecord(
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

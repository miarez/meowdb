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
import stas.website.Index.Btree.BTree;
import stas.website.Utils.Cast;
import stas.website.Utils.IO;
import stas.website.Utils.Serializer;
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


    public boolean drop_table(
        String table_name
    ){
        try {
            IO.delete_file("schema/" + table_name + ".json");
            System.out.println("File deleted successfully.");
        } catch (IOException e) {
            System.err.println("Failed to DROP TABLE [" + table_name + "]");
            return false;
        }
        try {
            IO.delete_file("data/" + table_name + ".json");
            System.out.println("File deleted successfully.");
        } catch (IOException e) {
            System.err.println("Failed to DROP TABLE [" + table_name + "]");
            return false;
        }
        return true;
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
    
    public Map<String, String> describe(
        String table_name
    ) throws FileNotFoundException, IOException {
        Map<String, Object> schema = IO.from_json("schema/" + table_name + ".json");
        return Cast.string_object_to_string_string(schema);
    }


    public boolean insert(
        String table_name,
        Map<String, Object> record
    ) throws FileNotFoundException, IOException{

        // validate the insert 
        Map<String, String> schema = describe(table_name);
        if(!validateRecord(record, schema)){
            return false;
        }

        String file_path = "data/" + table_name + ".db";

        // get the length of the current file
        RandomAccessFile dataStream = new RandomAccessFile(file_path, "rw");
        long fileSize = dataStream.length();

        // serialize
        byte[] serialized = Serializer.serialize(schema, record);
        long recordSize   = serialized.length;
        
        Map<String, Object> blockInfo = findSuitableBlock(recordSize, fileSize);


        Utils.pp("WRITING TO BLOCK ID : " + blockInfo.get("id"));

        // write the record
        dataStream.seek((int) blockInfo.get("insertionPoint"));
        dataStream.write(serialized);

        update_index("test_2", "event", (String) record.get("event"), (int) blockInfo.get("id"));
        return true;
    }

    private void update_index(
        String table_name,
        String index_name,
        String value,
        int blockID
    ){
        BTree tree = IndexManager.read(table_name, index_name);
        tree.insert(value, blockID);                 
        IndexManager.commit(table_name, index_name, tree);
    }


    private Map<String, Object> findSuitableBlock(
        long recordSize,
        long fileSize
    ){
        return prepareNewBlock(recordSize, fileSize);
    }

    private Map<String, Object> prepareNewBlock(
        long recordSize,
        long fileSize
    ){

        int blockId = (int) (fileSize / blockSize);
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

        Map<String, String> schema = describe(table_name);

        List<Map<String, Object>> records = new ArrayList<>();

        String file_path = "data/" + table_name + ".db";


        try (RandomAccessFile dataStream = new RandomAccessFile(file_path, "r")) {
            long fileSize = dataStream.length();


            dataStream.seek(0);
            while(dataStream.getFilePointer() < fileSize){
                
                long remaining = fileSize - dataStream.getFilePointer();  // Calculate remaining bytes
                byte[] block;

                // If remaining bytes are less than the block size, read only the remaining bytes
                if (remaining < blockSize) {
                    block = new byte[(int) remaining];
                } else {
                    block = new byte[(int) blockSize];
                }    
                try {
                    dataStream.readFully(block);
                    Map<String, Object> record = Serializer.deserialize(schema, block);
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


    public List<Map<String, Object>> read_with_indexes(
        String table_name,
        List<Filter> filters
    ) throws IOException{


        List<Map<String, Object>> records = new ArrayList<>();

        Map<String, String> schema = describe(table_name);

        String file_path = "data/" + table_name + ".db";


        ArrayList<Integer> indexes = new ArrayList<>();

        for (Filter filter : filters) {
            String filter_on_column = filter.getColumnName();
            if(does_column_have_index(table_name, filter_on_column)){
                BTree tree = IndexManager.read(table_name, filter_on_column);
                ArrayList<Integer> column_indexes = tree.search("apply");
                indexes.addAll(column_indexes);

                // start leap frog here somehow....

            }
        }

        // LOOKUP MY INDEXES

        Utils.pp("MY INDEXES ARE : " + indexes);

        // COMBINE ALL INDEXES VIA LEAP-FROG TODO


        try (RandomAccessFile dataStream = new RandomAccessFile(file_path, "r")) {
            long fileSize = dataStream.length();


            for(Integer blockID : indexes){

                dataStream.seek(blockSize * blockID);
                
                long remaining = fileSize - dataStream.getFilePointer();  // Calculate remaining bytes
                byte[] block;
    
                // If remaining bytes are less than the block size, read only the remaining bytes
                if (remaining < blockSize) {
                    block = new byte[(int) remaining];
                } else {
                    block = new byte[(int) blockSize];
                }    
                try {
                    dataStream.readFully(block);
                    Map<String, Object> record = Serializer.deserialize(schema, block);
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

    private boolean does_column_have_index(
        String table_name,
        String index_name
    )
    {
        String filePath = "index/" + table_name + "/" + index_name + ".json";
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
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

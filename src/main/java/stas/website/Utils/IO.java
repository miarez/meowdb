package stas.website.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

public class IO {

    public static Map<String, Object> from_json(
        String full_file_path
    ){
        Map<String, Object> data = new HashMap<>();
        Gson gson = new Gson();        
        try (FileReader reader = new FileReader(full_file_path)) {
            data = gson.fromJson(reader, Map.class);
        } catch (IOException e){
            System.err.println(e);
        }
        return data;
    }

    public static void delete_file(
        String file_path
    ) throws IOException{
        Files.delete(Paths.get(file_path));
    }
    


}

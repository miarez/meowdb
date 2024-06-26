package stas.website.Core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONObject;

import com.google.gson.Gson;

import stas.website.Utils.Utils;

import stas.website.Filter.Filter;

public class NoSQL {

    public NoSQL(){

    }

    public boolean insert(
        String table_name,
        Map<String, Object> record
    ){
       String line = new Gson().toJson(record);
       try (FileWriter fw = new FileWriter("data/"+ table_name + ".db", true);
       BufferedWriter bw = new BufferedWriter(fw);
       PrintWriter out = new PrintWriter(bw)) {
           out.println(line);
       } catch (IOException e) {
           System.err.println("An error occurred while writing to the file: " + e.getMessage());
           return false;
       }
       return true;
    }


    public List<Map<String, Object>> read(
        String table_name,
        List<Filter> filters
    ){

        String filePath = "data/"+ table_name + ".db";
        List<Map<String, Object>> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8))) {
            String line;
            while((line = reader.readLine()) != null){
                line = line.trim();
                if(line.isEmpty()) continue;
                JSONObject json = new JSONObject(line); 
                Map<String, Object> map = json.toMap();

                boolean addRecord = true;
                if(!filters.isEmpty()){

                    for (Filter filter : filters) {
                        if(!filter.invoke(map)){
                            addRecord = false;
                        }
                    }
                }
                
                if(addRecord){
                    records.add(map);
                }

            }
        } catch(IOException e){
            System.err.println("UH OH");
        }       
        return records;
    }

    
}

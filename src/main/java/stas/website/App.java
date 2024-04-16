package stas.website;

import java.io.FileReader;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect.Type;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import stas.website.Core.NoSQL;
import stas.website.Core.SQL;
import stas.website.Filter.Filter;
import stas.website.Filter.Eq;
import stas.website.Filter.In;
import stas.website.Filter.Nin;
import stas.website.Filter.Match;
import stas.website.Utils.Utils;

import com.google.gson.Gson;

// mvn test -Dtest=NoSQLTest#insert
// mvn test -P service-tests  


public class App 
{
    public static void main( String[] args ) throws IOException
    {

        SQL db = new SQL();
        
        Map<String, Object> record = new HashMap<>();
        record.put("id", 0);
        record.put("event", "apply");
        record.put("price", 10);

        // db.insert("test_2", record);
        // db.insert("test_2", record);
        // db.insert("test_2", record);
        // db.insert("test_2", record);

        List<Filter> filter_list = new ArrayList<>();
        
        String queryName= "test0";
        String filePath = "query/"+ queryName +".json";
        Gson gson = new Gson();

        java.lang.reflect.Type type = new TypeToken<Map<String, List<Map<String, Map<String, String>>>>>(){}.getType();

        // Use FileReader to read JSON file
        try (FileReader reader = new FileReader(filePath)) {
            // Deserialize JSON file to Java object (nested Maps)
            Map<String, List<Map<String, Map<String, String>>>> data = gson.fromJson(reader, type);

            // Accessing the list of filters
            List<Map<String, Map<String, String>>> filters = data.get("filters");

            // Iterate over each filter and print out details
            for (Map<String, Map<String, String>> filter : filters) {
                
                String filterType = filter.keySet().iterator().next(); 
                System.out.println("Filter: " + filter);

                Map<String, String> params = filter.get(filterType);
                String column_name =  params.get("column_name");
                String required_value = params.get("value");
                switch(filterType){
                    case "eq":
                        filter_list.add(new Eq(column_name, required_value));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading the JSON file.");
        }

        List<Map<String, Object>> response = db.read("test_2", filter_list);
        Utils.pp("HERE" + response);

           
    }


    public void noSQLDemo(){

        NoSQL db = new NoSQL();

        // Map<String, Object> record = new HashMap<>();
        // record.put("id", "abc123");
        // record.put("event", "apply");
        // record.put("type", "job");
        // record.put("price", 15);
        // // db.insert("test_1", record);


        List<Filter> filter_list = new ArrayList<>();
        
        String queryName= "test0";
        String filePath = "query/"+ queryName +".json";
        Gson gson = new Gson();

        java.lang.reflect.Type type = new TypeToken<Map<String, List<Map<String, Map<String, String>>>>>(){}.getType();

        // Use FileReader to read JSON file
        try (FileReader reader = new FileReader(filePath)) {
            // Deserialize JSON file to Java object (nested Maps)
            Map<String, List<Map<String, Map<String, String>>>> data = gson.fromJson(reader, type);

            // Accessing the list of filters
            List<Map<String, Map<String, String>>> filters = data.get("filters");

            // Iterate over each filter and print out details
            for (Map<String, Map<String, String>> filter : filters) {
                
                String filterType = filter.keySet().iterator().next(); 
                System.out.println("Filter: " + filter);

                Map<String, String> params = filter.get(filterType);
                String column_name =  params.get("column_name");
                String required_value = params.get("value");
                switch(filterType){
                    case "eq":
                        filter_list.add(new Eq(column_name, required_value));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading the JSON file.");
        }


        List<Map<String, Object>> response = db.read("test_1", filter_list);
        Utils.pp(response);

        
    }

    public List<Filter> buildFilterList(
        String queryName
    ){        
        List<Filter> filter_list = new ArrayList<>();
        // filters.add(new Match("event", "appl\\.*"));


        String filePath = "query/"+ queryName +".json";
        Gson gson = new Gson();

        java.lang.reflect.Type type = new TypeToken<Map<String, List<Map<String, Map<String, String>>>>>(){}.getType();

        // Use FileReader to read JSON file
        try (FileReader reader = new FileReader(filePath)) {
            // Deserialize JSON file to Java object (nested Maps)
            Map<String, List<Map<String, Map<String, String>>>> data = gson.fromJson(reader, type);

            // Accessing the list of filters
            List<Map<String, Map<String, String>>> filters = data.get("filters");

            // Iterate over each filter and print out details
            for (Map<String, Map<String, String>> filter : filters) {
                
                String filterType = filter.keySet().iterator().next(); 
                System.out.println("Filter: " + filter);

                Map<String, String> params = filter.get(filterType);
                String column_name =  params.get("column_name");
                String required_value = params.get("value");
                switch(filterType){
                    case "eq":
                        filter_list.add(new Eq(column_name, required_value));
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error reading the JSON file.");
        }

        return filter_list;
    }
}

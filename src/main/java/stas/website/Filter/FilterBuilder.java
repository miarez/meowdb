package stas.website.Filter;


import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import stas.website.Filter.Filter;
import stas.website.Filter.Eq;

public class FilterBuilder {
    
    public static List<Filter> build(
        Map<String, Object> json_query
    ){
        List<Filter> filter_list = new ArrayList<>();

            // Accessing the list of filters
        List<Map<String, Map<String, String>>> filters = (List<Map<String, Map<String, String>>>) json_query.get("filters");

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
        return filter_list;
    }
}

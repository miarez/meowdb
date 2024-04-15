package stas.website;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stas.website.Core.NoSQL;
import stas.website.Utils.Utils;

public class App 
{
    public static void main( String[] args )
    {
        NoSQL db = new NoSQL();

        Map<String, Object> record = new HashMap<>();
        record.put("id", "abc123");
        record.put("event", "apply");
        record.put("type", "job");
        record.put("price", 15);
        // db.insert("test_1", record);

        Map<String, Object> filters = new HashMap<>();
        filters.put("event", "apply");
        filters.put("price", 15);
        List<Map<String, Object>> response = db.read("test_1", filters);
        Utils.pp(response);

        
        
    }
}

package stas.website.service;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import stas.website.Core.NoSQL;
import stas.website.Filter.Filter;
import stas.website.Filter.FilterBuilder;
import stas.website.Utils.IO;

public class NoSQLTest 
{
    private NoSQL db = new NoSQL();
    public NoSQLTest(){}

    @Test 
    public void insert(){

        // create dummy data 
        Map<String, Object> record = new HashMap<>();
        record.put("id", "abc123");
        record.put("event", "apply");
        record.put("type", "job");
        record.put("price", 15);

        // run & test
        boolean response = db.insert("_test_nosql_0", record);
        assertTrue(response);
    }

    @Test 
    public void read(){
        List<Filter> filter_list = new ArrayList<>();
        List<Map<String, Object>> response = db.read("_test_nosql_0", filter_list);
        assertFalse( response.isEmpty() );
    }

    @Test 
    public void readWithFilter(){     
        Map<String, Object> json_query = IO.from_json("query/_test_0.json");
        List<Filter> filter_list = FilterBuilder.build(json_query);
        List<Map<String, Object>> response = db.read("_test_nosql_0", filter_list);
        assertFalse( response.isEmpty() );
    }

    
}

package stas.website.service;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import stas.website.Core.SQL;
import stas.website.Filter.Eq;
import stas.website.Filter.Filter;
import stas.website.Filter.Gt;
import stas.website.Utils.Utils;

public class SQLTest 
{
    private SQL db = new SQL();
    private String test_table_name = "_test_temp";

    private static final Map<String, String> TEST_SCHEMA;

    static {
        TEST_SCHEMA = new LinkedHashMap<>();
        TEST_SCHEMA.put("id", "INT");
        TEST_SCHEMA.put("event", "STRING");
        TEST_SCHEMA.put("price", "INT");
    }


    @Test
    public void create_table() throws IOException{
        boolean response = db.create_table(test_table_name, TEST_SCHEMA);
        assertTrue(response);
    }

    @Test
    public void create_or_replace_table() throws IOException{
        boolean response = db.create_or_replace_table(test_table_name, TEST_SCHEMA);
        assertTrue(response);
    }

    @Test
    public void drop_table() throws IOException{
        boolean response =  db.drop_table(test_table_name);
        assertTrue(response);
    }

    @Test
    public void describe() throws IOException{
        Map<String, String> response =  db.describe(test_table_name);
        Utils.pp(response);
        assertFalse(response.isEmpty());
    }

    @Test 
    public void insert() throws FileNotFoundException, IOException {

        Map<String, Object> record = new HashMap<>();
        record.put("id", 0);
        record.put("event", "click");
        record.put("price", 0);
        boolean response = db.insert("test_2", record);

        Map<String, Object> record2 = new HashMap<>();
        record2.put("id", 1);
        record2.put("event", "apply");
        record2.put("price", 0);
        boolean response2 = db.insert("test_2", record2);

        Map<String, Object> record3 = new HashMap<>();
        record3.put("id", 2);
        record3.put("event", "click");
        record3.put("price", 0);
        boolean response3 = db.insert("test_2", record3);

        Map<String, Object> record4 = new HashMap<>();
        record4.put("id", 3);
        record4.put("event", "view");
        record4.put("price", 0);
        boolean response4 = db.insert("test_2", record4);

        Map<String, Object> record5 = new HashMap<>();
        record5.put("id", 4);
        record5.put("event", "apply");
        record5.put("price", 0);
        boolean response5 = db.insert("test_2", record5);


        assertTrue(response);
    }



    @Test 
    public void read() throws IOException{

        List<Filter> filter_list = new ArrayList<>();

        List<Map<String, Object>> response = db.read("test_2", filter_list);
        Utils.pp("READ RESPONSE" + response);
    }



    @Test 
    public void read_with_indexes() throws IOException{

        List<Filter> filter_list = new ArrayList<>();
        filter_list.add(new Gt("id", 2));
        filter_list.add(new Eq("event","apply"));

        List<Map<String, Object>> response = db.read_with_indexes("test_2", filter_list);
        Utils.pp("READ RESPONSE" + response);
    }

    @Test 
    public void readWithFilter(){}

    
}

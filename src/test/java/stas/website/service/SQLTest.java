package stas.website.service;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;
import stas.website.Core.SQL;
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
    public void insert(){}

    @Test 
    public void read(){}

    @Test 
    public void readWithFilter(){}

    
}

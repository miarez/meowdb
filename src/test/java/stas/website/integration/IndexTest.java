package stas.website.integration;

import static org.junit.Assert.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import stas.website.Core.SQL;
import stas.website.Utils.Utils;

public class IndexTest {

    @Test
    public void test() throws FileNotFoundException, IOException {

        SQL db = new SQL();

        Map<String, Object> record = new HashMap<>();
        record.put("id", 0);
        record.put("event", "apply");
        record.put("price", 10);

        boolean response = db.insert("test_2", record);
        // assertTrue(response);


        Utils.pp("meoowwwww");
    }
    
}

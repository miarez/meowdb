

package stas.website;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

import stas.website.Core.SQL;

/**
 * Unit test for simple App.
 */
public class SQLCreateTest 
{
    /**
     * Rigorous Test :-)
     * @throws IOException 
     */
    @Test
    public void shouldAnswerWithTrue() throws IOException
    {
        SQL db = new SQL();

    
        Map<String, String> test_schema = new LinkedHashMap<>();
        test_schema.put("id", "INT");
        test_schema.put("event", "STRING");
        test_schema.put("price", "INT");

        // db.create_table("test_2", test_schema);
        // db.drop_table("test_2");
        db.create_or_replace_table("test_2", test_schema);


        assertTrue( true );
    }
}


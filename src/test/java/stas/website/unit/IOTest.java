package stas.website.unit;

import static org.junit.Assert.assertFalse;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import stas.website.Utils.IO;
import stas.website.Utils.Utils;


public class IOTest {

    @Test
    public void read_json() throws FileNotFoundException, IOException{
        String query_name = "_test_0";
        Map<String, Object> response = IO.from_json("query/"+ query_name +".json");
        Utils.pp(response);
        assertFalse(response.isEmpty());
    }
    
}

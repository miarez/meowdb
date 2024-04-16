package stas.website.unit;

import static org.junit.Assert.assertFalse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;


import org.junit.Test;

import stas.website.Filter.Filter;
import stas.website.Filter.FilterBuilder;
import stas.website.Utils.IO;
import stas.website.Utils.Utils;


public class FilterBuilderTest {

    @Test
    public void build() throws FileNotFoundException, IOException{

        // doing 2 things make this an integration test... de-coupled.... todo 
        // grab query
        Map<String, Object> json_query = IO.from_json("query/_test_0.json");
        List<Filter> filter_list = FilterBuilder.build(json_query);
        Utils.pp(filter_list);
        assertFalse(filter_list.isEmpty());
    }

    
}

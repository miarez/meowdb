package stas.website.Filter;

import java.util.Map;

abstract public class Filter {

    abstract public boolean invoke(Map<String, Object> row);


}

package stas.website.Filter;

import java.util.Map;

abstract public class Filter {

    public String column_name;

    abstract public boolean invoke(Map<String, Object> row);

    public abstract String getColumnName();


}

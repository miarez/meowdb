package stas.website.Filter;

import java.util.List;
import java.util.Map;

public class Nin extends Filter {
    
    public String column_name;
    public List<Object> required_values;

    public Nin(
        String column_name,
        List<Object> required_values
    ){
        this.column_name     = column_name;
        this.required_values  = required_values;
    }

    @Override
    public boolean invoke(
        Map<String, Object> row
    ){
        return !(new In(column_name, required_values)).invoke(row);
    }

}

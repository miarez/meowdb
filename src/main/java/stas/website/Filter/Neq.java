package stas.website.Filter;
import java.util.Map;

public class Neq extends Filter {

    public String column_name;
    public String required_value;

    public Neq(
        String column_name,
        String required_value
    ){
        this.column_name     = column_name;
        this.required_value  = required_value;
    }

    @Override
    public boolean invoke(
        Map<String, Object> row
    ){
        return !(new Eq(column_name, required_value)).invoke(row);
    }


    
}

package stas.website.Filter;
import java.util.Map;
import stas.website.Utils.Utils;

public class Eq extends Filter {

    public String column_name;
    public String required_value;

    public Eq(
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

        try {
            Object column_value = row.get(column_name);
            if(column_value.equals(required_value)){
                return true;
            }            
        } catch (Exception e) {
            Utils.pp("Column Name Doesn't exist");
        }
        return false;
    }


    
}

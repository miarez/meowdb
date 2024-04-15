package stas.website.Filter;
import java.util.Map;
import stas.website.Utils.Utils;

public class Gt extends Filter {

    public String column_name;
    public int required_value;

    public Gt(
        String column_name,
        int required_value
    ){
        this.column_name     = column_name;
        this.required_value  = required_value;
    }

    @Override
    public boolean invoke(
        Map<String, Object> row
    ){
        try {
            int column_value = (int) row.get(column_name);
            if(column_value > required_value){
                return true;
            }
        } catch (Exception e) {
            Utils.pp("Column Name Doesn't exist");
        }
        return false;
    }
}

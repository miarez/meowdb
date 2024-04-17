package stas.website.Filter;
import java.util.List;
import java.util.Map;
import stas.website.Utils.Utils;

public class In extends Filter {

    public String column_name;
    public List<Object> required_values;

    public In(
        String column_name,
        List<Object> required_values
    ){
        this.column_name     = column_name;
        this.required_values  = required_values;
    }
    
    public String getColumnName() {
        return column_name;
    }


    @Override
    public boolean invoke(
        Map<String, Object> row
    ){
        try {
            Object column_value = row.get(column_name);
            for (Object required_value : required_values) {
                Utils.pp("Required_value : " + required_value + " | column_value = " + column_value);
                if (required_value.equals(column_value)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Utils.pp("Column Name Doesn't exist");
        }
        return false;
    }



    
}

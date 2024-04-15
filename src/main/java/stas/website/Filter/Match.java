package stas.website.Filter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import stas.website.Utils.Utils;

public class Match extends Filter {

    public String column_name;
    public String required_value;

    public Match(
        String column_name,
        String required_value
    ){
        this.column_name       = column_name;
        this.required_value  = required_value;
    }

    @Override
    public boolean invoke(
        Map<String, Object> row
    ){
        try {
            String column_value = row.get(column_name).toString();
            Pattern pattern = Pattern.compile(required_value);          
            

            Utils.pp(pattern + " | val " + column_value);

            Matcher matcher = pattern.matcher(column_value);
            while (matcher.find()) {
                return true;
            }
        } catch (Exception e) {
            Utils.pp(e);
        }
        return false;
    }


    
}

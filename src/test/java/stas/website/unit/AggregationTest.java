package stas.website.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.util.concurrent.UncaughtExceptionHandlers;

import stas.website.Utils.Utils;

public class AggregationTest {
    

    public abstract class AggregationFunction {
        public String column_name;
        public String name;
        public abstract String get_column_name();
        public abstract String get_name();
        public int invoke(int old_value, int i) {
            // TODO Auto-generated method stub
            throw new UnsupportedOperationException("Unimplemented method 'invoke'");
        }
        
    }

    public class Count extends AggregationFunction {
        public String column_name;
        public String name;
        public Count(
            String column_name
        ){
            this.column_name = column_name;
            this.name = "COUNT(" + column_name + ")";
        }
        public String get_column_name(){
            return this.column_name;
        }
        public String get_name(){
            return this.name;
        }
        public int invoke(
            int old_value,
            int new_value
        ){
            return old_value + new_value;
        }
    }
    public class Sum extends AggregationFunction {
        public String column_name;
        public String name;
        public Sum(
            String column_name
        ){
            this.column_name = column_name;
            this.name = "SUM(" + column_name + ")";
        }
        public String get_column_name(){
            return this.column_name;
        }
        public String get_name(){
            return this.name;
        }
        public int invoke(
            int old_value,
            int new_value
        ){
            return old_value + new_value;
        }
    }

    @Test
    public void test(){

        List<Map<String, Object>> records = new ArrayList<>();


        Map<String, Object> record1 = new HashMap<>();
        record1.put("event", "click");
        record1.put("price", 10);
        records.add(record1);

        Map<String, Object> record2 = new HashMap<>();
        record2.put("event", "click");
        record2.put("price", 20);
        records.add(record2);

        Map<String, Object> record3 = new HashMap<>();
        record3.put("event", "apply");
        record3.put("price", 100);
        records.add(record3);

        List<String> aggregation_terms = new ArrayList<>();
        aggregation_terms.add("event");


        List<AggregationFunction> aggregation_functions = new ArrayList<>();
        aggregation_functions.add(new Count("price"));
        aggregation_functions.add(new Sum("price"));





        Map<String, Map<String, Integer>> out = new HashMap<>();

        for(Map<String, Object> record : records){
            StringBuilder terms_value = new StringBuilder();

        
            String key;
            // build key to group by
            for(String term : aggregation_terms){
                terms_value.append(record.get(term));
            }
            key = terms_value.toString();

            // function aggregation on value 
            for(AggregationFunction aggFun : aggregation_functions){


                String agg_fun_type = aggFun.getClass().getSimpleName();

                String agg_fun_name = aggFun.get_name();

                Map<String, Integer> agg_fun_value = new HashMap<>();
                
                int old_value = 0;
                if(out.containsKey(key)){
                    agg_fun_value = out.get(key);
                    if(agg_fun_value.containsKey(agg_fun_name)){
                        old_value = (int) agg_fun_value.get(agg_fun_name);
                    }
                }

                String column_name = aggFun.get_column_name();


                int value;
                switch(agg_fun_type){
                    case "Count":
                        value = old_value += 1;
                        break;
                    case "Sum":
                        int record_value = (int) record.get(column_name);
                        value = old_value + record_value;
                        break;
                    default:
                        throw new IllegalArgumentException("UNRECOGNIZED AGGREGATION FUNCTION");
                }

                agg_fun_value.put(agg_fun_name, value);
                out.put(key, agg_fun_value);
            }



        }

        Utils.pp(out);



    }
}

package stas.website.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.common.util.concurrent.UncaughtExceptionHandlers;

import stas.website.Utils.Utils;

public class AggregationTest {
    

    public abstract class AggregationFunction<T, R> {

        
        public String column_name;
        public String name;
        public abstract String get_column_name();
        public abstract String get_name();

        // Generic method for different implementations
        public abstract R invoke(List<T> list); 

        
    }

    public class Count extends AggregationFunction<Object, Integer> {
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
        public Integer invoke(List<Object> list){
            return list.size(); 
        }

    }
    public class Sum extends AggregationFunction<Integer, Integer> {
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

        public Integer invoke(List<Integer> list){
            int sum = 0;
            for (int num : list) {
                sum += num;
            }
            return sum; // Sum the integers in the list
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



        Map<String, Map<String, List<Object>>> out = new HashMap<>();

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

                Map<String, List<Object>> agg_fun_value = new HashMap<>();
                
                List<Object> running_list = new ArrayList<>();
                if(out.containsKey(key)){
                    agg_fun_value = out.get(key);
                    if(agg_fun_value.containsKey(agg_fun_name)){
                        running_list = agg_fun_value.get(agg_fun_name);
                    }
                }
                String column_name = aggFun.get_column_name();
                running_list.add(record.get(column_name));
                agg_fun_value.put(agg_fun_name, running_list);
                out.put(key, agg_fun_value);
            }
        }


        // Map<String, Map<String, List<Object>>> out = new HashMap<>();

        for (Map.Entry<String, Map<String, List<Object>>> outerEntry : out.entrySet()) {
            String group_by_key = outerEntry.getKey();
            Map<String, List<Object>> innerMap = outerEntry.getValue();


            for (Map.Entry<String, List<Object>> innerEntry : innerMap.entrySet()) {

                String agg_fun_name = innerEntry.getKey();
                List<Object> values = innerEntry.getValue();               
                Utils.pp(agg_fun_name);
        
            }

        }


        Utils.pp(out);



    }
}

package com.wanghengzhi.bigdata.distribute;

import org.apache.hadoop.hive.ql.exec.UDF;

import java.util.*;

/**
 * Created by wanghengzhi on 2019/4/15.
 */
public class PrioritySort extends UDF {
    public String evaluate() {
        return "";
    }


    private HashMap<String, Double> parseMap(String l) {
        HashMap<String, Double> result = new HashMap<String, Double>();
        l = l.substring(1, l.length() - 1);
        String[] ll = l.trim().split(",");
        for (String lll : ll) {
            String[] lsplit = lll.trim().split(":");
            result.put(lsplit[0].substring(1, 5), Double.parseDouble(lsplit[1]));
        }
        return result;
    }

    private ArrayList<Map.Entry<String, Double>> sortWeight(HashMap<String, Double> weightMap) {
        ArrayList<Map.Entry<String, Double>> weightList = new ArrayList<Map.Entry<String, Double>>(
                weightMap.entrySet());
        Collections.sort(weightList, new Comparator<Object>() {
            @SuppressWarnings("unchecked")
            public int compare(Object e1, Object e2) {
                Double v1 = ((Map.Entry<Integer, Double>) e1)
                        .getValue();
                Double v2 = ((Map.Entry<Integer, Double>) e2)
                        .getValue();
                return (v1 - v2) < 0 ? -1 : 1;
            }
        });
        return weightList;
    }
}

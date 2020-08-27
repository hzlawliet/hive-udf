package com.wanghengzhi.bigdata.distribute;

import org.apache.hadoop.hive.ql.exec.UDF;
import org.apache.hadoop.hive.ql.udf.UDFType;

import java.text.SimpleDateFormat;

/**
 * Created by wanghengzhi on 2019/1/16.
 */

@UDFType(deterministic=false)
public class SimilarTagFilter extends UDF {
    private String pri_id = "";
    private String pri_time = "1900-01-01 00:00:00.0";
    private double rlt;

    public double evaluate(String id, String time) {
        if (this.pri_id.equals(id)) {
            this.rlt = 0.0D;
            if ((Math.abs(time_diff(time, this.pri_time)) / 1000L / 60L <= 10L)) {
                this.rlt = 1.0D;
            }
            this.pri_time = time;
        }
        else {
            this.rlt = 0.0D;
        }
        this.pri_id = id;
        this.pri_time = time;

        return this.rlt;
    }

    public long time_diff(String pri_time, String time) {
        SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        long td = 1000L;
        try {
            td = d.parse(pri_time).getTime() - d.parse(time).getTime();
        }
        catch (Exception localException) {}
        return td;
    }

    public static void main(String[] args) {
        SimilarTagFilter similarTagFilter = new SimilarTagFilter();
        System.out.println(similarTagFilter.time_diff("2018-09-01 12:00:00", "2018-09-01 12:10:00"));
    }
}

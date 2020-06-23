package com.etoak.epidemic.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ETDateUtils {
    public static Date string2Date(String value) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(value);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> getNear7Days() {
        List<String> dates = new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dates.add(localDate.format(dtf));
        for(int i=1;i<=6;i++){
            LocalDate localDate1 = localDate.minusDays(i);
            dates.add(localDate1.format(dtf));
        }
        return dates;
    }
}

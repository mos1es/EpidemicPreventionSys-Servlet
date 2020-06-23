package com.etoak.epidemic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;
import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Person {
    private String          id;
    private String          name;
    private String          phone;
    private String          idcard;
    private Double          tmperature;
    private String          health;
    private String          inflow;
    private String          flowaddress;
    private String          jnaddress;
    private Date            returndate;

    private String          date1;

    public String getDate1() {
        SimpleDateFormat sdf =new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(returndate);
    }

    private int             del;
    private String          savepath;

}

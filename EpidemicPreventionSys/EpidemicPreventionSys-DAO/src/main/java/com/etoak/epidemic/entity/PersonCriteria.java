package com.etoak.epidemic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonCriteria {
    private String name;
    private String health;
    private String startdate;
    private String enddate;

    private String starttemp;
    private String endtemp;
    private String flowaddress;
}

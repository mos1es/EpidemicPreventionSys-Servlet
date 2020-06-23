package com.etoak.epidemic.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Locations {
    private int id;
    private String name;
    private String type;
    private int pid;
}

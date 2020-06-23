package com.etoak.epidemic.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ETResponse {
    private String returndaste;
    private int normal;
    private int exceptions;
}

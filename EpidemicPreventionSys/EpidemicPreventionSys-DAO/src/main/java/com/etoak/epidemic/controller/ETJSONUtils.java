package com.etoak.epidemic.controller;

import com.alibaba.fastjson.JSONArray;
import com.etoak.epidemic.entity.Locations;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.List;

public class ETJSONUtils {

    public static void writerArray(HttpServletResponse resp, Object obj) {
        try{
            resp.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            out.print(JSONArray.toJSONString(obj));
            out.close();
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }

    public static void writerObject(HttpServletResponse resp, Object obj) {
        try{
            resp.setContentType("text/plain;charset=UTF-8");
            PrintWriter out = resp.getWriter();
            out.print(JSONArray.toJSONString(obj));
            out.close();
        }catch (Exception e){
            e.fillInStackTrace();
        }
    }

}

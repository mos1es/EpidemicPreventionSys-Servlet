package com.etoak.epidemic.controller;

import com.alibaba.fastjson.JSONArray;


import com.etoak.epidemic.entity.Locations;
import com.etoak.epidemic.entity.Person;
import com.etoak.epidemic.entity.PersonCriteria;
import com.etoak.epidemic.service.EpidemicService;
import com.etoak.epidemic.utils.ETDateUtils;
import com.etoak.epidemic.utils.ETResponse;
import com.etoak.epidemic.utils.ETStringUtils;
import com.etoak.epidemic.utils.Page;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;


@WebServlet("/epidemic")
public class EpidemicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req,resp);
    }

    protected void report(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        EpidemicService es = new EpidemicService();

        //查询数据库的数据
        List<ETResponse> data = es.queryReportData();

        //获得最近七天['2020-06-23',''2020-06-22','2020-06-21','2020-06-20'..]
        List<String> near7Days = ETDateUtils.getNear7Days();
        //存放最后的结果
        List<ETResponse> result = new ArrayList<>();
        for (String d:near7Days){
            //构造返回的对象
            ETResponse r = new ETResponse();
            r.setReturndaste(d);
            //遍历判断如果 当前时间就是数据库的时间 把从数据库中拿到的数据组装上
            for (ETResponse r1:data){
                if (r1.getReturndaste().equals(d)){
                    r.setExceptions(r1.getExceptions());
                    r.setNormal(r1.getNormal());
                }
            }
            result.add(r);
        }
        ETJSONUtils.writerArray(resp,result);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //1.获得令牌

        String method = req.getParameter("method");

        if (method == null){
            String ct = req.getHeader("content-Type");
            if (ct.indexOf("multipart")>=0){
                this.add(req,resp);
                return;
            }
        }
        //判断去哪个方法处理
        /*if("add".equals(method)){
            this.add(req,resp);
        } else if ("querySome".equals(method)){
            this.querySome(req,resp);
        } else if ("queryAllProvinces".equals(method)){
            this.queryAllProvinces(req,resp);
        } else if ("queryCityByProvinceName".equals(method)){
            this.queryCityByProvinceName(req,resp);
        } else if ("queryAreaByCityName".equals(method)){
            this.queryAreaByCityName(req,resp);
        }*/
        try{
            this.getClass().getDeclaredMethod(method,HttpServletRequest.class,HttpServletResponse.class).invoke(this,req,resp);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void export2Excel(HttpServletRequest req,HttpServletResponse resp)throws ServletException,IOException{
        //导出 ==> 下载(图片 音视频 Excel)
        //ServletContext application = this.getServletContext();
        //服务器上的绝对路径 d:
        //String path = application.getRealPath("/files/001.jpg");
        //resp.setHeader("Content-Disposition","attachment;filename="+ URLEncoder.encode("吴老大nb","utf-8")+".jpg");
        resp.setHeader("Content-Disposition","attachment;filename="
                + URLEncoder.encode("personlist","utf-8") +".xlsx");
        EpidemicService es = new EpidemicService();
        //3.
        List<Person> data = es.querySome(new PersonCriteria(),1,10000000);
        Workbook wb = new SXSSFWorkbook();
        Sheet sheet = wb.createSheet("人员列表" );
        //3.添加行
        Row header = sheet.createRow(0);
        //4.添加表头
        header.createCell(0).setCellValue("编号");
        header.createCell(1).setCellValue("名字");
        header.createCell(2).setCellValue("体温");
        header.createCell(3).setCellValue("身份证");
        header.createCell(4).setCellValue("手机号");
        header.createCell(5).setCellValue("健康状况");
        header.createCell(6).setCellValue("是否外地流入");
        header.createCell(7).setCellValue("流入地址");
        header.createCell(8).setCellValue("在济地址");
        header.createCell(9).setCellValue("返济日期");
        header.createCell(10).setCellValue("是否删除");
        AtomicInteger index = new AtomicInteger(1);

        data.stream().forEach((d1) -> {

            Row d = sheet.createRow(index.getAndIncrement());
            //4.添加数据
            d.createCell(0).setCellValue(index.intValue());
            d.createCell(1).setCellValue(d1.getName());
            d.createCell(2).setCellValue(d1.getTmperature());
            d.createCell(3).setCellValue(d1.getIdcard());
            d.createCell(4).setCellValue(d1.getPhone());
            d.createCell(5).setCellValue(d1.getHealth());
            d.createCell(6).setCellValue(d1.getInflow());
            d.createCell(7).setCellValue(d1.getFlowaddress());
            d.createCell(8).setCellValue(d1.getJnaddress());
            d.createCell(9).setCellValue(d1.getDate1());
            d.createCell(10).setCellValue(d1.getDel() == 1 ? "已删除" : "正常");
            String savepath = d1.getSavepath();
            ServletContext application = this.getServletContext();
            String path = application.getRealPath(savepath);
            File f = new File(path);
            //~~~~~~~读取文件到数组
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                BufferedImage image = ImageIO.read(f);
                ImageIO.write(image, "jpg", baos);

                SXSSFDrawing patriarch = ((SXSSFSheet) sheet).createDrawingPatriarch();
                //确定图片的位置
                XSSFClientAnchor anchor = new
                        XSSFClientAnchor(0,0,255,255,
                        11,index.intValue()-1,12,index.intValue());
                //
                anchor.setAnchorType(ClientAnchor.AnchorType.DONT_MOVE_AND_RESIZE);
                //创建图片
                patriarch.createPicture(anchor,wb.addPicture(baos.toByteArray(),5));


            }catch(Exception e){
                e.printStackTrace();
            }

        });
        wb.write(resp.getOutputStream());
        wb.close();

    }


    protected void add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        EpidemicService es = new EpidemicService();
        DiskFileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload upload = new ServletFileUpload(factory);

        try{
            List<FileItem> items = upload.parseRequest(req);
            Iterator<FileItem> car = items.iterator();
            Person p = new Person();
            String city =null;String province=null;String area=null;String info=null;
            while (car.hasNext()){
                FileItem item = car.next();
                if (item.isFormField()){
                    String name = item.getFieldName();
                    String value = item.getString("UTF-8");
                    if ("name".equals(name)){
                        p.setName(value);
                    }
                    if("phone".equals(name)){
                        p.setPhone(value);
                    }
                    if ("idcard".equals(name)){
                        p.setIdcard(value);
                    }
                    if ("tmperature".equals(name)){
                        p.setTmperature(Double.parseDouble(value));
                    }
                    if ("health".equals(name)){
                        p.setHealth(value);
                    }
                    if ("inflow".equals(name)){
                        p.setInflow(value);
                    }
                    if ("city".equals(name)){
                        city = value;
                    }
                    if ("province".equals(name)){
                        province = value;
                    }
                    if ("area".equals(name)){
                        area = value;
                    }
                    if ("info".equals(name)){
                        info = value;
                    }
                    if ("returndate".equals(name)){
                        p.setReturndate(ETDateUtils.string2Date(value));
                    }
                    if ("jnaddress".equals(name)){
                        p.setJnaddress(value);
                    }
                }else {
                    String fileName = item.getName();

                    String fileExt = FilenameUtils.getExtension(fileName);
                    String newName = UUID.randomUUID().toString().replaceAll("-","")+"."+fileExt;
                    ServletContext application = this.getServletContext();
                    String path = application.getRealPath("/files/"+newName);
                    item.write(new File(path));
                    p.setSavepath("/files/"+newName);

                }
            }
            if (ETStringUtils.isNotEmpty(province)){
                p.setFlowaddress(province + "-"+city+"-"+area+"-"+info);
            }
            p.setDel(0);
            es.add(p);
        }catch (Exception e){
            e.printStackTrace();
        }
        resp.sendRedirect("add.html");

    }
    protected void querySome(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int pageNumber = Integer.parseInt(req.getParameter("pageNumber"));
        int pageSize = Integer.parseInt(req.getParameter("pageSize"));

        //获取条件
        String name = req.getParameter("name");
        String health = req.getParameter("health");
        String temp = req.getParameter("tmperature");
        String startdate = req.getParameter("startdate");
        String enddate = req.getParameter("enddate");
        String flowaddress = req.getParameter("flowaddress");
        PersonCriteria cri = new PersonCriteria();
        cri.setName(name);cri.setEnddate(enddate);cri.setStartdate(startdate);cri.setFlowaddress(flowaddress);cri.setHealth(health);

        if("1".equals(temp)){
            cri.setStarttemp("35.9");cri.setEndtemp("37.2");
        }
        if("2".equals(temp)){
            cri.setStarttemp("37.3");cri.setEndtemp("38.5");
        }
        if("3".equals(temp)){
            cri.setStarttemp("38.6");cri.setEndtemp("39.3");
        }

        //
        Page<Person> page = new Page<Person>();
        page.setPageNumber(pageNumber);
        page.setPageSize(pageSize);

        EpidemicService es = new EpidemicService();
        int count = es.countPerson(new PersonCriteria());
        //
        page.setTotal(count);
        List<Person> rows = es.querySome(cri,pageNumber,pageSize);

        page.setRows(rows);

        ETJSONUtils.writerObject(resp,page);

    }
    protected void queryAllProvinces(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EpidemicService es = new EpidemicService();
        //调用service查询所有省的信息
        List<Locations> pros = es.queryAllProvinces();
        //通过json写出到客户端
        resp.setContentType("text/plain;charset=UTF-8");
        PrintWriter out = resp.getWriter();
        out.print(JSONArray.toJSONString(pros));
        out.close();
    }
    protected void queryCityByProvinceName(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String proname = req.getParameter("proname");
        resp.setContentType("text/plain;charset=utf-8");
        EpidemicService es = new EpidemicService();
        List<Locations> list = es.queryCityByProvinceName(proname);
        PrintWriter out = resp.getWriter();
        out.print(JSONArray.toJSONString(list));
        out.close();

    }
    protected void queryAreaByCityName(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        EpidemicService es = new EpidemicService();

        String cityname = req.getParameter("cityname");
        List<Locations> area = es.queryAreaByCityName(cityname);
        ETJSONUtils.writerArray(resp,area);
    }
}

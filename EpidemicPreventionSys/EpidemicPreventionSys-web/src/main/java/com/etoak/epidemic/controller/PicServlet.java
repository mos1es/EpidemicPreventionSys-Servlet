package com.etoak.epidemic.controller;

import com.etoak.epidemic.entity.Person;
import com.etoak.epidemic.service.EpidemicService;
import com.etoak.epidemic.utils.ETDateUtils;
import com.etoak.epidemic.utils.ETStringUtils;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.openxmlformats.schemas.drawingml.x2006.spreadsheetDrawing.CTMarker;


import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@WebServlet("/pic")
public class PicServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        EpidemicService es = new EpidemicService();

        //i使用第三方插件获得文件
        DiskFileItemFactory factory = new DiskFileItemFactory();

        ServletFileUpload upload = new ServletFileUpload(factory);

        try {
            List<FileItem> items = upload.parseRequest(req);
            Iterator<FileItem> car = items.iterator();

            String city = null;
            String province = null;
            String area = null;
            String info = null;
            while (car.hasNext()) {
                FileItem item = car.next();

                if (!item.isFormField()) { //普通参数

                    //is就是传上来的那个文件===》Excel文件
                    InputStream is = item.getInputStream();

                    //构造一个workbook对象  传入客户端的文件
                    Workbook wb = new XSSFWorkbook(is);

                    Sheet sheet = wb.getSheetAt(0);
                    //sheetNumber_行_列，  图片
                    Map<String, PictureData> sheetIndexPicMap =
                            getSheetPictures07(1, (XSSFSheet) sheet,(XSSFWorkbook) wb);

                    int fRow = sheet.getFirstRowNum();
                    int lRow = sheet.getLastRowNum();
                    List<Person> persons = new ArrayList<>();
                    ServletContext application = this.getServletContext();

                    //存放错误的
                    List<Map<String, Object>> errors = new ArrayList<>();
                    for (int i = fRow + 1; i < lRow; i++) {
                        Map<String, Object> error = new HashMap<>();

                        //获得每一张图片{ 1_1_11图片的数据，1_2_11:图片2的数据}
                        PictureData pictureData = sheetIndexPicMap.get("1_" + i + "_11");
                        byte[] data = pictureData.getData();
                        String newName = UUID.randomUUID().toString().replaceAll("-", "") + ".jpg";
                        File f = new File(application.getRealPath("/files/" + newName));
                        OutputStream os = new FileOutputStream(f);
                        //把图片写出去  写出到服务器端的指定目录(/files/新名字)
                        os.write(data);
                        os.close();

                        Row row = sheet.getRow(i);

                        //构造Person对象
                        Person person = new Person();

                        //3.把图片的保存路径 写到数据库中 一边下次使用
                        person.setSavepath("/files/"+newName);

                        //给Person对象赋值 需要做判断 如果名字为null  1.循环continue 2.错误信息添加到错误集合
                        Cell c_name = row.getCell(1);
                        if (null != c_name) {
                            person.setName(c_name.getStringCellValue());
                        }else {
                            error.put("name","名字必填!");
                        }
                        //给person对象赋值
                        person.setTmperature(row.getCell(2).getNumericCellValue());
                        person.setIdcard(row.getCell(3).getStringCellValue());
                        person.setPhone(row.getCell(4).getStringCellValue());
                        person.setHealth(row.getCell(5).getStringCellValue());
                        person.setInflow(row.getCell(6).getStringCellValue());
                        Cell c7 = row.getCell(7);
                        if (c7 != null) {
                            person.setFlowaddress(c7.getStringCellValue());
                        }
                        person.setJnaddress(row.getCell(8).getStringCellValue());
                        person.setReturndate(ETDateUtils.string2Date(row.getCell(9).getStringCellValue()));
                        person.setDel(0);

                        if (error.isEmpty()) {
                            //把组装的person对象给llist
                            persons.add(person);
                        } else {
                            errors.add(error);
                            continue;
                        }
                    }
                    System.out.println(persons);
                    //调用Service批量添加person
                    es.addPersons(persons);

                    //验证 错误
                    if (!errors.isEmpty()) {
                        ETJSONUtils.writerObject(resp, errors);
                        return;
                    }
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("flag", "success");
            ETJSONUtils.writerObject(resp, map);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //从Excel xlsx中获得图片资源
    public static Map<String, PictureData> getSheetPictures07(
            int sheetNum, XSSFSheet sheet, XSSFWorkbook workbook) {
        Map<String, PictureData> sheetIndexPicMap = new HashMap<String, PictureData>();

        for (POIXMLDocumentPart dr : sheet.getRelations()) {
            if (dr instanceof XSSFDrawing) {
                XSSFDrawing drawing = (XSSFDrawing) dr;
                List<XSSFShape> shapes = drawing.getShapes();
                for (XSSFShape shape : shapes) {
                    XSSFPicture pic = (XSSFPicture) shape;
                    XSSFClientAnchor anchor = pic.getPreferredSize();
                    CTMarker ctMarker = (CTMarker) anchor.getFrom();
                    //1_1_11    1_2_11      1_3_11
                    String picIndex = String.valueOf(sheetNum) + "_"
                            + ctMarker.getRow() + "_" + ctMarker.getCol();
                    sheetIndexPicMap.put(picIndex, pic.getPictureData());
                }
            }
        }
        return sheetIndexPicMap;
    }

}


package com.etoak.epidemic.dao;

import com.etoak.epidemic.entity.Locations;
import com.etoak.epidemic.entity.Person;
import com.etoak.epidemic.entity.PersonCriteria;
import com.etoak.epidemic.factory.CF;
import com.etoak.epidemic.utils.ETResponse;
import com.etoak.epidemic.utils.ETStringUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;

import java.sql.SQLException;
import java.util.*;

public class EpidemicDaoImpl implements IEpidemicDao{
    public static void main(String[] args) {
        List<Person> date = new ArrayList<>();
        for(int x= 0;x<1000;x++){
            Person p = new Person();
            p.setName("etoak"+ x);
            p.setPhone("188"+ x);
            p.setIdcard("3701"+x);
            p.setReturndate(new java.sql.Date(System.currentTimeMillis()));
            date.add(p);
        }
        IEpidemicDao dao = new EpidemicDaoImpl();
        dao.addPersonBat(date);
    }

    QueryRunner qr = new QueryRunner(CF.getDs());
    @Override
    public void add(Person person) {
        try {
  /*          String selectKey = "select sys_guid as id from dual";
            Map data = qr.query(selectKey,new MapHandler());
            person.setId(data.get("id")+"");*/

        person.setId(UUID.randomUUID().toString().replaceAll("-",""));// 主键返回生成方法
        String sql="insert into person(id," +
                                    "name," +
                                    "phone," +
                                    "idcard," +
                                    "health," +
                                    "inflow," +
                                    "flowaddress," +
                                    "jnaddress," +
                                    "returndate," +
                                    "del," +
                                    "tmperature," +
                                    "savepath) values(?,?,?,?,?,?,?,?,?,?,?,?)";

            qr.update(sql,person.getId(),
                    person.getName(),
                    person.getPhone(),
                    person.getIdcard(),
                    person.getHealth(),
                    person.getInflow(),
                    person.getFlowaddress(),
                    person.getJnaddress(),
                    new java.sql.Date(person.getReturndate().getTime()),
                    person.getDel(),
                    person.getTmperature(),
                    person.getSavepath());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    @Override
    public List<Locations> queryAllProvinces() {
        String sql ="select * from locations where pid=-1 and type in ('省','直辖市','自治区')";
        try{
              List<Locations> data = qr.query(sql,new BeanListHandler<Locations>(Locations.class));
                return data;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Locations> queryCityByProName(String name) {

        String sql = "select * from locations where pid = (select id from locations where name = ? and type in('省','直辖市','自治区'))";
        try{
            List<Locations> data = qr.query(sql,new BeanListHandler<Locations>(Locations.class),name);
            return data;
        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }

    @Override
    public List<Locations> queryAreaByCityName(String city) {
        String sql =  "select * from locations where pid = (select id from locations where name = ? and type='市')";

        try{
            List<Locations> data = qr.query(sql,new BeanListHandler<Locations>(Locations.class),city);
            return data;
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Person> queryPerson(PersonCriteria tiaojian, int pageNumber, int pageSize) {

        /*
         * select * from (select rownum rn,a.* from(select * from person where 1=1 )a) where rn>? and rn<=?
         * select * from (select rownum rn,a.*from(select * from locations where type = '区')a) where rn>3 and rn <=20
         *select * from (select rownum rn,a.* from(select * from person where 1=1 )a) where rn>? and rn<=?
         * */
        String sql = "select * from (select rownum rn,a.* from(select * from person where 1=1 ";
        List data = new ArrayList();
        if (tiaojian != null && ETStringUtils.isNotEmpty(tiaojian.getName())) {
            sql += " and instr(name,?)>0";
            data.add(tiaojian.getName());
        }
        if (tiaojian != null && ETStringUtils.isNotEmpty(tiaojian.getHealth())) {
            sql += " and health=? ";
            data.add(tiaojian.getHealth());
        }
        if (tiaojian != null && ETStringUtils.isNotEmpty(tiaojian.getStartdate())) {
            sql += " and returndate >= to_date(?,'yyyy-MM-dd') ";
            data.add(tiaojian.getStartdate());
        }
        if (tiaojian != null && ETStringUtils.isNotEmpty(tiaojian.getEnddate())) {
            sql += " and returndate <= to_date(?,'yyyy-MM-dd') ";
            data.add(tiaojian.getEnddate());
        }
        if (tiaojian != null && ETStringUtils.isNotEmpty(tiaojian.getStarttemp())) {
            sql += " and tmperature >= to_number(?) ";

            data.add(tiaojian.getStarttemp());
        }
        if (tiaojian != null && ETStringUtils.isNotEmpty(tiaojian.getEndtemp())) {
            sql += " and tmperature <= to_number(?) ";

            data.add(tiaojian.getEndtemp());
        }
        if (tiaojian != null && ETStringUtils.isNotEmpty(tiaojian.getFlowaddress())) {
            sql += " and flowaddress as like ? ";

            data.add("%" + tiaojian.getFlowaddress() + "%");
        }

        sql += ")a)where rn>? and rn<=? ";


        data.add((pageNumber - 1) * pageSize);
        data.add(pageSize * pageNumber);

        try {
            /* List<Person> result =*/
            return qr.query(sql, new BeanListHandler<Person>(Person.class), data.toArray());
            /* return  result;*/
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updatePerson(Person p) {
        String sql = "update person set name=? ,phone=? ,idcard=? ,health=? ,inflow=? ,flowaddress=? ,jnadderss=?" +
                "returndate=?, del=?, tmperature=?, savepath=? where id=?";

        try{
        qr.update(sql,p.getName(),p.getPhone(),p.getIdcard(),p.getHealth(),p.getInflow(),p.getFlowaddress(),p.getJnaddress(),
                p.getReturndate(),p.getDel(),p.getTmperature(),p.getSavepath(),p.getId());
        }catch(Exception e){
        e.printStackTrace();
        }
    }

    @Override
    public void addPersonBat(List<Person> persons) {
        String sql = "insert into person(id,name,phone,idcard,health,inflow," +
                "flowaddress,jnaddress,returndate,del,tmperature,savepath)";
        List<Object> data = new ArrayList<Object>();
        for(Person p : persons){
            sql+=" select sys_guid(),?,?,?,?,?,?,?,?,?,?,? from dual union ";
            Collections.addAll(data,p.getName(),p.getPhone(),p.getIdcard(),
                    p.getHealth(),p.getInflow(),p.getFlowaddress(),p.getJnaddress(),
                    new java.sql.Date(p.getReturndate().getTime()),p.getDel(),p.getTmperature(),p.getSavepath());
        }
        sql = sql.substring(0,sql.lastIndexOf("union"));

        try{
            qr.update(sql,data.toArray());
        }catch(Exception e){
            e.printStackTrace();

        }

    }

    @Override
    public int countPerson(PersonCriteria tiaojian) {
        String sql = "select count(*) from person where 1=1";
        List<Object> data = new ArrayList();
        if(tiaojian!=null && ETStringUtils.isNotEmpty(tiaojian.getName())){
            sql+=" and instr(name,?)>0";
            data.add(tiaojian.getName());
        }
        if (tiaojian!=null && ETStringUtils.isNotEmpty(tiaojian.getHealth())){
            sql+= " adn health=?";
            data.add(tiaojian.getHealth());
        }
        if (tiaojian!=null && ETStringUtils.isNotEmpty(tiaojian.getStartdate())){
            sql+=" and returndate >= to_date(?,'yyyy-MM-dd')";
            data.add(tiaojian.getStartdate());
        }
        if (tiaojian!=null && ETStringUtils.isNotEmpty(tiaojian.getEnddate())){
            sql+=" and returndate <= to_date(?,'yyyy-MM-dd')";
            data.add(tiaojian.getEnddate());
        }
        if (tiaojian!=null && ETStringUtils.isNotEmpty(tiaojian.getStarttemp())){
            sql+=" and tmperature >= to_number(?,1)";

            data.add(tiaojian.getStarttemp());
        }
        if (tiaojian!=null && ETStringUtils.isNotEmpty(tiaojian.getEndtemp())){
            sql+=" and tmperature <= to_number(?,1)";

            data.add(tiaojian.getEndtemp());
        }
        if (tiaojian!=null && ETStringUtils.isNotEmpty(tiaojian.getFlowaddress())){
            sql+=" and flowaddress as like ?";

            data.add("%"+tiaojian.getFlowaddress()+"%");
        }
        try{
            Map result = qr.query(sql,new MapHandler(),data.toArray());
            return Integer.parseInt(result.get("count(*)")+"");
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public List<ETResponse> queryReportData() {

        String sql = "select" +
                " to_char(returndate,'yyyy-MM-dd') as returndate," +
                " sum(decode(health,'正常',1,0) as normal," +
                " (count(*)-sum(decode(health,'正常',1,0))) as exceptions " +
                " from person group by returndate having sysdate-returndate<=7";
        try{
            List<ETResponse> data = qr.query(sql,new BeanListHandler<ETResponse>(ETResponse.class));
            return data;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}

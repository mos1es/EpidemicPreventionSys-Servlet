package com.etoak.epidemic.dao;

import com.etoak.epidemic.entity.Locations;
import com.etoak.epidemic.entity.Person;
import com.etoak.epidemic.entity.PersonCriteria;
import com.etoak.epidemic.utils.ETResponse;

import java.util.List;

public interface IEpidemicDao {
    //添加
    public void add(Person person);
    //查询所有的省
    public List<Locations> queryAllProvinces();
    //查询 根据省 名字
    public List<Locations> queryCityByProName(String name);
    // 查 县区 根据市 名字
    public List<Locations> queryAreaByCityName(String city);
    //带条件 分页查询
    public List<Person> queryPerson(PersonCriteria tiaojian, int pageNumber, int pageSiza);
    //修改, 删除update
    public void updatePerson(Person p);
    //批量添加
    public void addPersonBat(List<Person> persons);

    public int countPerson(PersonCriteria tiaojian);

    public List<ETResponse> queryReportData();
}

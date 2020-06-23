package com.etoak.epidemic.service;


import com.etoak.epidemic.dao.EpidemicDaoImpl;
import com.etoak.epidemic.dao.IEpidemicDao;
import com.etoak.epidemic.entity.Locations;
import com.etoak.epidemic.entity.Person;
import com.etoak.epidemic.entity.PersonCriteria;
import com.etoak.epidemic.utils.ETResponse;

import java.util.List;
public class EpidemicService {

    private IEpidemicDao dao = new EpidemicDaoImpl();

    public List<Locations> queryAllProvinces() {
        return dao.queryAllProvinces();
    }

    public List<Locations> queryCityByProvinceName(String proname) {
        return dao.queryCityByProName(proname);
    }

    public List<Locations> queryAreaByCityName(String cityname) {
        return dao.queryAreaByCityName(cityname);
    }

    public void add(Person p) {
        dao.add(p);
    }

    public int countPerson(PersonCriteria personCriteria) {
        return dao.countPerson(personCriteria);
    }

    public List<Person> querySome(PersonCriteria tiaojian, int pageNumber, int pageSize) {
        return dao.queryPerson(tiaojian,pageNumber,pageSize);
    }

    public List<ETResponse> queryReportData() {
        return dao.queryReportData();
    }

    public void addPersons(List<Person> persons) {
        dao.addPersonBat(persons);
    }
}

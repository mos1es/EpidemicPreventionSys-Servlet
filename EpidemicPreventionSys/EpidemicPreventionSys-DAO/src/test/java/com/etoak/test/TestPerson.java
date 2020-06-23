package com.etoak.test;

import com.etoak.epidemic.dao.EpidemicDaoImpl;
import com.etoak.epidemic.dao.IEpidemicDao;
import com.etoak.epidemic.entity.Person;
import com.etoak.epidemic.entity.PersonCriteria;
import org.junit.Test;

public class TestPerson {
    @Test
    public void testPerson(){
        Person p = new Person();
        p.setDel(1);
        p.setHealth("健康");
        p.setName("张三");
        System.out.println(p);
        System.out.println(p.getHealth()+"\t"+p.getName());

        IEpidemicDao dao = new EpidemicDaoImpl();
        PersonCriteria pc = new PersonCriteria();
        pc.setName("张");
        //System.out.println(dao.queryCityByProName("山东省"));
        System.out.println(dao.queryPerson(pc,1,5));

    }
}

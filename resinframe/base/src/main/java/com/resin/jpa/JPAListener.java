package com.resin.jpa;

import com.resin.beans.BaseEntity;
import com.resin.jms.JMSTool;
import com.resin.jms.JMSType;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;


public class JPAListener {

    @Setter
    static JMSTool jmsTool;


    @PostPersist
    public void createObject(BaseEntity o) {
        //System.out.println("create object :"+ o);
        jmsTool.sendMessage(JMSType.CREATE, o);
    }


    @PostRemove
    public void removeObject(BaseEntity o) {
        //System.out.println("create object :"+ o);
        jmsTool.sendMessage(JMSType.DELETE, o);
    }
}

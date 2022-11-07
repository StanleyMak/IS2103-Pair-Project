/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.EmployeeEntity;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeAccessRightEnum;

/**
 *
 * @author stonley
 */
@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        if (em.find(EmployeeEntity.class, 1l) == null) {
            EmployeeEntity employee = new EmployeeEntity("Stanley", "stanmail", "111");
            employee.setEmployeeAccessRight(EmployeeAccessRightEnum.SALES_MANAGER);
            
            em.persist(employee);
        }

    }

}

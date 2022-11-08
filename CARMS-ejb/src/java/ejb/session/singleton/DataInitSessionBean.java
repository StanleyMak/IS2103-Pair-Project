/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import entity.CarCategoryEntity;
import entity.CarEntity;
import entity.CarModelEntity;
import entity.EmployeeEntity;
import entity.OutletEntity;
import entity.RentalRateEntity;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RentalRateTypeEnum;
import util.enumeration.StatusEnum;

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
            initialiseData();
        }
    }

    private void initialiseData() {

        /*Initialising Outlets*/
        OutletEntity outletA = new OutletEntity("Outlet A", null, null);
        em.persist(outletA);
        em.flush();

        OutletEntity outletB = new OutletEntity("Outlet B", null, null);
        em.persist(outletB);
        em.flush();

        OutletEntity outletC = new OutletEntity("Outlet C", null, null);//new Date("10:00"), new Date("22:00"));
        em.persist(outletC);
        em.flush();

//        /*Initialising Employees*/
//        EmployeeEntity employee = new EmployeeEntity("Employee A1", outletA, EmployeeAccessRightEnum.SALES_MANAGER);
//        em.persist(employee);
//        em.flush();
//        employee = new EmployeeEntity("Employee A2", outletA, EmployeeAccessRightEnum.OPERATIONS_MANAGER);
//        em.persist(employee);
//        em.flush();
//        employee = new EmployeeEntity("Employee A3", outletA, EmployeeAccessRightEnum.CUSTOMER_SERVICE_EXEC);
//        em.persist(employee);
//        em.flush();
//        employee = new EmployeeEntity("Employee A4", outletA, EmployeeAccessRightEnum.EMPLOYEE);
//        em.persist(employee);
//        em.flush();
//        employee = new EmployeeEntity("Employee A5", outletA, EmployeeAccessRightEnum.EMPLOYEE);
//        em.persist(employee);
//        em.flush();
//
//        employee = new EmployeeEntity("Employee B1", outletB, EmployeeAccessRightEnum.SALES_MANAGER);
//        em.persist(employee);
//        em.flush();
//        employee = new EmployeeEntity("Employee B2", outletB, EmployeeAccessRightEnum.OPERATIONS_MANAGER);
//        em.persist(employee);
//        em.flush();
//        employee = new EmployeeEntity("Employee B3", outletB, EmployeeAccessRightEnum.CUSTOMER_SERVICE_EXEC);
//        em.persist(employee);
//        em.flush();
//
//        employee = new EmployeeEntity("Employee C1", outletC, EmployeeAccessRightEnum.SALES_MANAGER);
//        em.persist(employee);
//        em.flush();
//        employee = new EmployeeEntity("Employee C2", outletC, EmployeeAccessRightEnum.OPERATIONS_MANAGER);
//        em.persist(employee);
//        em.flush();
//        employee = new EmployeeEntity("Employee C3", outletC, EmployeeAccessRightEnum.CUSTOMER_SERVICE_EXEC);
//        em.persist(employee);
//        em.flush();
        
        /*Initialising Employees with username and password*/
        EmployeeEntity employee = new EmployeeEntity("Employee A1", outletA, EmployeeAccessRightEnum.SALES_MANAGER, "a1", "a1");
        em.persist(employee);
        em.flush();
        employee = new EmployeeEntity("Employee A2", outletA, EmployeeAccessRightEnum.OPERATIONS_MANAGER, "a2", "a2");
        em.persist(employee);
        em.flush();
        employee = new EmployeeEntity("Employee A3", outletA, EmployeeAccessRightEnum.CUSTOMER_SERVICE_EXEC, "a3", "a3");
        em.persist(employee);
        em.flush();
        employee = new EmployeeEntity("Employee A4", outletA, EmployeeAccessRightEnum.EMPLOYEE, "a4", "a4");
        em.persist(employee);
        em.flush();
        employee = new EmployeeEntity("Employee A5", outletA, EmployeeAccessRightEnum.EMPLOYEE, "a5", "a5");
        em.persist(employee);
        em.flush();

        employee = new EmployeeEntity("Employee B1", outletB, EmployeeAccessRightEnum.SALES_MANAGER, "b1", "b1");
        em.persist(employee);
        em.flush();
        employee = new EmployeeEntity("Employee B2", outletB, EmployeeAccessRightEnum.OPERATIONS_MANAGER, "b2", "b2");
        em.persist(employee);
        em.flush();
        employee = new EmployeeEntity("Employee B3", outletB, EmployeeAccessRightEnum.CUSTOMER_SERVICE_EXEC, "b3", "b3");
        em.persist(employee);
        em.flush();

        employee = new EmployeeEntity("Employee C1", outletC, EmployeeAccessRightEnum.SALES_MANAGER, "c1", "c1");
        em.persist(employee);
        em.flush();
        employee = new EmployeeEntity("Employee C2", outletC, EmployeeAccessRightEnum.OPERATIONS_MANAGER, "c2", "c2");
        em.persist(employee);
        em.flush();
        employee = new EmployeeEntity("Employee C3", outletC, EmployeeAccessRightEnum.CUSTOMER_SERVICE_EXEC, "c3", "c3");
        em.persist(employee);
        em.flush();


        /*Initialising Category*/
        CarCategoryEntity standardSedan = new CarCategoryEntity("Standard Sedan");
        em.persist(standardSedan);
        em.flush();
        CarCategoryEntity familySedan = new CarCategoryEntity("Family Sedan");
        em.persist(familySedan);
        em.flush();
        CarCategoryEntity luxurySedan = new CarCategoryEntity("Luxury Sedan");
        em.persist(luxurySedan);
        em.flush();
        CarCategoryEntity suvMinivan = new CarCategoryEntity("SUV and Minivan");
        em.persist(suvMinivan);
        em.flush();


        /*Initialising Model*/
        CarModelEntity toyotaCorolla = new CarModelEntity("Toyota", "Corolla", standardSedan);
        em.persist(toyotaCorolla);
        em.flush();
        CarModelEntity hondaCivic = new CarModelEntity("Honda", "Civic", standardSedan);
        em.persist(hondaCivic);
        em.flush();
        CarModelEntity nissanSunny = new CarModelEntity("Nissan", "Sunny", standardSedan);
        em.persist(nissanSunny);
        em.flush();
        CarModelEntity mercedesEClass = new CarModelEntity("Mercedes", "E Class", luxurySedan);
        em.persist(mercedesEClass);
        em.flush();
        CarModelEntity bmw5Series = new CarModelEntity("BMW", "5 Series", luxurySedan);
        em.persist(bmw5Series);
        em.flush();
        CarModelEntity audiA6 = new CarModelEntity("Audi", "A6", luxurySedan);
        em.persist(audiA6);
        em.flush();

        /*Initialising Car*/
        CarEntity car = new CarEntity("SS00A1TC", toyotaCorolla, StatusEnum.AVAILABLE, outletA);
        em.persist(car);
        em.flush();
        car = new CarEntity("SS00A2TC", toyotaCorolla, StatusEnum.AVAILABLE, outletA);
        em.persist(car);
        em.flush();
        car = new CarEntity("SS00A3TC", toyotaCorolla, StatusEnum.AVAILABLE, outletA);
        em.persist(car);
        em.flush();
        car = new CarEntity("SS00B1HC", hondaCivic, StatusEnum.AVAILABLE, outletB);
        em.persist(car);
        em.flush();
        car = new CarEntity("SS00B2HC", hondaCivic, StatusEnum.AVAILABLE, outletB);
        em.persist(car);
        em.flush();
        car = new CarEntity("SS00B3HC", hondaCivic, StatusEnum.AVAILABLE, outletB);
        em.persist(car);
        em.flush();
        car = new CarEntity("SS00C1NS", nissanSunny, StatusEnum.AVAILABLE, outletC);
        em.persist(car);
        em.flush();
        car = new CarEntity("SS00C2NS", nissanSunny, StatusEnum.AVAILABLE, outletC);
        em.persist(car);
        em.flush();
        car = new CarEntity("SS00C3NS", nissanSunny, StatusEnum.REPAIR, outletC);
        em.persist(car);
        em.flush();
        car = new CarEntity("LS00A4ME", mercedesEClass, StatusEnum.AVAILABLE, outletA);
        em.persist(car);
        em.flush();
        car = new CarEntity("LS00B4B5", bmw5Series, StatusEnum.AVAILABLE, outletB);
        em.persist(car);
        em.flush();
        car = new CarEntity("LS00C4A6", audiA6, StatusEnum.AVAILABLE, outletC);
        em.persist(car);
        em.flush();

        /*Initialising Rental*/
        RentalRateEntity rate = new RentalRateEntity("Standard Sedan - Default", RentalRateTypeEnum.DEFAULT, standardSedan, 100, null, null);
        em.persist(rate);
        em.flush();
        rate = new RentalRateEntity("Standard Sedan - Weekend Promo", RentalRateTypeEnum.PROMO, standardSedan, 80, new Date("09/12/2022 12:00"), new Date("11/12/2022 00:00"));
        em.persist(rate);
        em.flush();
        rate = new RentalRateEntity("Family Sedan - Default", RentalRateTypeEnum.DEFAULT, familySedan, 200, null, null);
        em.persist(rate);
        em.flush();
        rate = new RentalRateEntity("Luxury Sedan - Default", RentalRateTypeEnum.DEFAULT, luxurySedan, 300, null, null);
        em.persist(rate);
        em.flush();
        rate = new RentalRateEntity("Luxury Sedan - Monday", RentalRateTypeEnum.PEAK, luxurySedan, 310, new Date("05/12/2022 00:00"), new Date("05/12/2022 23:59"));
        em.persist(rate);
        em.flush();
        rate = new RentalRateEntity("Luxury Sedan - Tuesday", RentalRateTypeEnum.PEAK, luxurySedan, 320, new Date("06/12/2022 00:00"), new Date("06/12/2022 23:59"));
        em.persist(rate);
        em.flush();
        rate = new RentalRateEntity("Luxury Sedan - Wednesday", RentalRateTypeEnum.PEAK, luxurySedan, 330, new Date("07/12/2022 00:00"), new Date("07/12/2022 23:59"));
        em.persist(rate);
        em.flush();
        rate = new RentalRateEntity("Luxury Sedan - Weekday Promo", RentalRateTypeEnum.PROMO, luxurySedan, 250, new Date("07/12/2022 00:00"), new Date("08/12/2022 00:00"));
        em.persist(rate);
        em.flush();
        rate = new RentalRateEntity("SUV and Minivan - Default", RentalRateTypeEnum.DEFAULT, suvMinivan, 400, null, null);
        em.persist(rate);
        em.flush();

        /*Initialising Partner*/
        //Partner partner = new Partner("Holiday.com");

    }
}

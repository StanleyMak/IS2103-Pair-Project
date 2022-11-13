/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.CarCategoryEntity;
import entity.CarEntity;
import entity.CarModelEntity;
import entity.EmployeeEntity;
import entity.OutletEntity;
import entity.OwnCustomerEntity;
import entity.RentalRateEntity;
import entity.ReservationEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.EmployeeAccessRightEnum;
import util.enumeration.RentalRateTypeEnum;
import util.enumeration.StatusEnum;
import util.exception.CarModelDisabledException;
import util.exception.CarModelNotFoundException;
import util.exception.CustomerNotFoundException;

/**
 *
 * @author stonley
 */
@Singleton
@LocalBean
@Startup

public class DataInitSessionBean {

    @EJB(name = "CarSessionBeanLocal")
    private CarSessionBeanLocal carSessionBeanLocal;

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    
    
    @PostConstruct
    public void postConstruct() {
        if (em.find(EmployeeEntity.class, 1l) == null) {
            initialiseData();
        }
    }

    private void initialiseData() {
        
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        try {
            /*Initialising Outlets*/
            OutletEntity outletA = new OutletEntity("Outlet A", null, null);//24 hours
            em.persist(outletA);
            em.flush();
            
            OutletEntity outletB = new OutletEntity("Outlet B", null, null);
            em.persist(outletB);
            em.flush();
            
       
            OutletEntity outletC = new OutletEntity("Outlet C", timeFormat.parse("08:00"), timeFormat.parse("22:00"));
            em.persist(outletC);
            em.flush();

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
            CarModelEntity audiA6 = new CarModelEntity("Audi", "A6", luxurySedan); //put in isdisabled = false
            em.persist(audiA6);
            em.flush();

            /*Initialising Car*/
            CarEntity car1 = null;
            try {
                car1 = new CarEntity("SS00A1TC", toyotaCorolla, StatusEnum.AVAILABLE, outletA);
                carSessionBeanLocal.createNewCar(car1, car1.getModel().getModelName(), car1.getCurrOutlet().getAddress());
            } catch (CarModelNotFoundException | CarModelDisabledException e) {
                System.out.println("hi");
            }
//            em.persist(car1);
//            em.flush();
            CarEntity car2 = new CarEntity("SS00A2TC", toyotaCorolla, StatusEnum.AVAILABLE, outletA);
            em.persist(car2);
            em.flush();
            CarEntity car3 = new CarEntity("SS00A3TC", toyotaCorolla, StatusEnum.AVAILABLE, outletA);
            em.persist(car3);
            em.flush();
            CarEntity car4 = new CarEntity("SS00B1HC", hondaCivic, StatusEnum.AVAILABLE, outletB);
            em.persist(car4);
            em.flush();
            CarEntity car5 = new CarEntity("SS00B2HC", hondaCivic, StatusEnum.AVAILABLE, outletB);
            em.persist(car5);
            em.flush();
            CarEntity car6 = new CarEntity("SS00B3HC", hondaCivic, StatusEnum.AVAILABLE, outletB);
            em.persist(car6);
            em.flush();
            CarEntity car7 = new CarEntity("SS00C1NS", nissanSunny, StatusEnum.AVAILABLE, outletC);
            em.persist(car7);
            em.flush();
            CarEntity car8 = new CarEntity("SS00C2NS", nissanSunny, StatusEnum.AVAILABLE, outletC);
            em.persist(car8);
            em.flush();
            CarEntity car9 = new CarEntity("SS00C3NS", nissanSunny, StatusEnum.REPAIR, outletC);
            em.persist(car9);
            em.flush();
            CarEntity car10 = new CarEntity("LS00A4ME", mercedesEClass, StatusEnum.AVAILABLE, outletA);
            em.persist(car10);
            em.flush();
            CarEntity car11 = new CarEntity("LS00B4B5", bmw5Series, StatusEnum.AVAILABLE, outletB);
            em.persist(car11);
            em.flush();
            CarEntity car12 = new CarEntity("LS00C4A6", audiA6, StatusEnum.AVAILABLE, outletC);
            em.persist(car12);
            em.flush();

            /*Initialising Rental*/
            RentalRateEntity rate = new RentalRateEntity("Standard Sedan - Default", RentalRateTypeEnum.DEFAULT, standardSedan, 100, null, null);
            em.persist(rate);
            em.flush();
            rate = new RentalRateEntity("Standard Sedan - Weekend Promo", RentalRateTypeEnum.PROMO, standardSedan, 80, dateTimeFormat.parse("09/12/2022 12:00"), dateTimeFormat.parse("11/12/2022 00:00"));
            em.persist(rate);
            em.flush();
            rate = new RentalRateEntity("Family Sedan - Default", RentalRateTypeEnum.DEFAULT, familySedan, 200, null, null);
            em.persist(rate);
            em.flush();
            rate = new RentalRateEntity("Luxury Sedan - Default", RentalRateTypeEnum.DEFAULT, luxurySedan, 300, null, null);
            em.persist(rate);
            em.flush();
            rate = new RentalRateEntity("Luxury Sedan - Monday", RentalRateTypeEnum.PEAK, luxurySedan, 310, dateTimeFormat.parse("05/12/2022 00:00"), dateTimeFormat.parse("05/12/2022 23:59"));
            em.persist(rate);
            em.flush();
            rate = new RentalRateEntity("Luxury Sedan - Tuesday", RentalRateTypeEnum.PEAK, luxurySedan, 320, dateTimeFormat.parse("06/12/2022 00:00"), dateTimeFormat.parse("06/12/2022 23:59"));
            em.persist(rate);
            em.flush();
            rate = new RentalRateEntity("Luxury Sedan - Wednesday", RentalRateTypeEnum.PEAK, luxurySedan, 330, dateTimeFormat.parse("07/12/2022 00:00"), dateTimeFormat.parse("07/12/2022 23:59"));
            em.persist(rate);
            em.flush();
            rate = new RentalRateEntity("Luxury Sedan - Weekday Promo", RentalRateTypeEnum.PROMO, luxurySedan, 250, dateTimeFormat.parse("07/12/2022 00:00"), dateTimeFormat.parse("08/12/2022 00:00"));
            em.persist(rate);
            em.flush();
            rate = new RentalRateEntity("SUV and Minivan - Default", RentalRateTypeEnum.DEFAULT, suvMinivan, 400, null, null);
            em.persist(rate);
            em.flush();
            
            
            
            
//            OwnCustomerEntity customer = new OwnCustomerEntity("hans", "stan");
//            customerSessionBeanLocal.createNewCustomer(customer);
//            
//            try {
//                OwnCustomerEntity test = customerSessionBeanLocal.retrieveOwnCustomerByOwnCustomerEmail("hans");
//                System.out.println(test.getCustomerID() + " " + test.getEmail() + " " + test.getPassword());
//            } catch (CustomerNotFoundException e) {
//                System.out.println("lol");
//            }
//            
//            try {
//                OwnCustomerEntity me = customerSessionBeanLocal.retrieveOwnCustomerByOwnCustomerEmail("hans");
//       
//                        
//                ReservationEntity res = new ReservationEntity();
//                res = new ReservationEntity(0, "", dateTimeFormat.parse("06/12/2022 00:00"), dateTimeFormat.parse("07/12/2022 00:00"), "1", true);
//                reservationSessionBeanLocal.createNewReservation(res, car1.getCarID(), "hans", "Outlet A", "Outlet A");
//                //me.getReservations().add(res);
//                
//                res = new ReservationEntity(0, "", dateTimeFormat.parse("07/12/2022 12:00"), dateTimeFormat.parse("08/12/2022 00:00"), "2", true);
//                reservationSessionBeanLocal.createNewReservation(res, car2.getCarID(), "hans", "Outlet A", "Outlet B");
//                
//                res = new ReservationEntity(0, "", dateTimeFormat.parse("07/12/2022 12:00"), dateTimeFormat.parse("09/12/2022 00:00"), "3", true);
//                reservationSessionBeanLocal.createNewReservation(res, car3.getCarID(), "hans", "Outlet B", "Outlet C");
//                
//                res = new ReservationEntity(0, "", dateTimeFormat.parse("09/12/2022 12:00"), dateTimeFormat.parse("07/12/2022 00:00"), "4", true);
//                reservationSessionBeanLocal.createNewReservation(res, car4.getCarID(), "hans", "Outlet C", "Outlet A");
//
//                res = new ReservationEntity(10, "1", dateTimeFormat.parse("06/12/2022 14:00"), dateTimeFormat.parse("10/12/2022 12:00"), "1", true); // outlet B
//                reservationSessionBeanLocal.createNewReservation(res, car4.getCarID(), "hans", "Outlet C", "Outlet A");
//                
//                res = new ReservationEntity(10, "2", dateTimeFormat.parse("06/12/2022 08:00"), dateTimeFormat.parse("08/12/2022 23:00"), "2", true); // outlet C
//                reservationSessionBeanLocal.createNewReservation(res, car4.getCarID(), "hans", "Outlet C", "Outlet C");
//                
//                res = new ReservationEntity(20, "1", dateTimeFormat.parse("05/12/2022 10:00"), dateTimeFormat.parse("06/12/2022 10:00"), "3", true); // outlet A // car 1
//                reservationSessionBeanLocal.createNewReservation(res, car4.getCarID(), "hans", "Outlet C", "Outlet A");
//                
//                res = new ReservationEntity(20, "1", dateTimeFormat.parse("06/12/2022 14:00"), dateTimeFormat.parse("07/12/2022 08:00"), "4", true); // outlet A // car 2
//                reservationSessionBeanLocal.createNewReservation(res, car4.getCarID(), "hans", "Outlet C", "Outlet A");
//                
//                res = new ReservationEntity(30, "1", dateTimeFormat.parse("07/12/2022 10:00"), dateTimeFormat.parse("09/12/2022 10:00"), "5", true); // outlet A // car 3
//                reservationSessionBeanLocal.createNewReservation(res, car4.getCarID(), "hans", "Outlet C", "Outlet A");
//                
//                res = new ReservationEntity(40, "1", dateTimeFormat.parse("12/12/2022 10:00"), dateTimeFormat.parse("13/12/2022 10:00"), "6", true); 
//                reservationSessionBeanLocal.createNewReservation(res, car4.getCarID(), "hans", "Outlet C", "Outlet A");
//            
//            } catch (ParseException | CustomerNotFoundException e) {
//                System.out.println("Error: " + e.getMessage() + "!\n");
//            } 
//            
//          /*Initialising Partner*/
//            //Partner partner = new Partner("Holiday.com");
//            
        } catch (ParseException e) {
            System.out.println("Invalid Date/Time format! Please try again!\n");
        }

    }
}
///
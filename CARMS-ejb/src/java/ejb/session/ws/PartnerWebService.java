/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.CustomerSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.RentalRateSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.CarCategoryEntity;
import entity.CustomerEntity;
import entity.OutletEntity;
import entity.PartnerEntity;
import entity.ReservationEntity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.CarCategoryNotFoundException;
import util.exception.CustomerEmailExistsException;
import util.exception.CustomerNotFoundException;
import util.exception.InputDataValidationException;
import util.exception.InvalidLoginCredentialException;
import util.exception.InvalidReservationCodeException;
import util.exception.PartnerNotFoundException;
import util.exception.ReservationCodeExistsException;
import util.exception.ReservationNotFoundException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author stonley
 */
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

    @EJB(name = "CustomerSessionBeanLocal")
    private CustomerSessionBeanLocal customerSessionBeanLocal;

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    @EJB
    private OutletSessionBeanLocal outletSessionBeanLocal;

    @EJB(name = "CarSessionBeanLocal")
    private CarSessionBeanLocal carSessionBeanLocal;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB(name = "PartnerSessionBeanLocal")
    private PartnerSessionBeanLocal partnerSessionBeanLocal;

    @EJB(name = "RentalRateSessionBeanLocal")
    private RentalRateSessionBeanLocal rentalRateSessionBeanLocal;

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "partnerLogin")
    public PartnerEntity partnerLogin(@WebParam(name = "username") String username,
            @WebParam(name = "password") String password) throws InvalidLoginCredentialException {

        if (username.length() > 0 && password.length() > 0) {
            PartnerEntity partner = partnerSessionBeanLocal.partnerLogin(username, password);
            System.out.println("********** PartnerWebService.partnerLogin(): Partner " + partner.getUsername() + " login remotely via web service");
            return partner;
        } else {
            throw new InvalidLoginCredentialException("Missing Login Credentials");
        }

    }

    @WebMethod(operationName = "partnerSearchCar")
    public List<CarCategoryEntity> partnerSearchCar(@WebParam(name = "startDateTime") String startDateTime,
            @WebParam(name = "pickupOutlet") OutletEntity pickupOutlet,
            @WebParam(name = "endDateTime") String endDateTime,
            @WebParam(name = "returnOutlet") OutletEntity returnOutlet) {

        List<CarCategoryEntity> cats = new ArrayList<>();
        try {
            Date pickupDateTime = dateTimeFormat.parse(startDateTime);
            Date returnDateTime = dateTimeFormat.parse(endDateTime);
            cats = carSessionBeanLocal.doSearchCar(pickupDateTime, returnDateTime, pickupOutlet, returnOutlet);
        } catch (ParseException ex) {
            System.out.println("Invalid Date/Time Format");
        }
        return cats;
    }

    @WebMethod(operationName = "retrieveOutletByOutletAddressWS")
    public OutletEntity retrieveOutletByOutletAddressWS(@WebParam(name = "outletAddress") String outletAddress) {
        return outletSessionBeanLocal.retrieveOutletByOutletAddress(outletAddress);
    }

    @WebMethod(operationName = "computeFee")
    public double computeFee(@WebParam(name = "startDateTime") String startDateTime,
            @WebParam(name = "endDateTime") String endDateTime,
            @WebParam(name = "catName") String catName) {

        double fee = 0;
        try {
            Date pickupDateTime = dateTimeFormat.parse(startDateTime);
            Date returnDateTime = dateTimeFormat.parse(endDateTime);
            fee = rentalRateSessionBeanLocal.computeCheapestRentalRateFee(pickupDateTime, returnDateTime, catName);
        } catch (ParseException ex) {
            System.out.println("Invalid Date/Time Format");
        }
        return fee;
    }

    @WebMethod(operationName = "partnerReserveCar")
    public String partnerReserveCar(@WebParam(name = "reservation") ReservationEntity reservation,
            @WebParam(name = "username") String username,
            @WebParam(name = "returnOutletAddress") String returnOutletAddress,
            @WebParam(name = "pickupOutletAddress") String pickupOutletAddress,
            @WebParam(name = "catName") String catName,
            @WebParam(name = "custName") String custName) throws PartnerNotFoundException, CarCategoryNotFoundException, CustomerNotFoundException, CustomerEmailExistsException, UnknownPersistenceException, InputDataValidationException, ReservationCodeExistsException {

        String resCode = "";
        try {
            resCode = reservationSessionBeanLocal.createNewReservationForPartner(reservation, username, returnOutletAddress, pickupOutletAddress, catName);
            Long cusID = customerSessionBeanLocal.createNewCustomer(new CustomerEntity(custName));
            CustomerEntity customer = customerSessionBeanLocal.retrieveCustomerByCustomerEmail(custName);
            try {
                reservationSessionBeanLocal.createNewReservation(reservation, customer.getEmail(), returnOutletAddress, pickupOutletAddress, catName);
            } catch (CustomerNotFoundException customerNotFoundException) {
                throw new CustomerNotFoundException();
            } catch (CarCategoryNotFoundException carCategoryNotFoundException) {
                throw new CarCategoryNotFoundException();
            } catch (ReservationCodeExistsException ex) {
                throw new ReservationCodeExistsException();
            }
        } catch (PartnerNotFoundException ex) {
            throw new PartnerNotFoundException("Partner Not Found");
        } catch (CarCategoryNotFoundException ex) {
            throw new CarCategoryNotFoundException("Car Category Not Found");
        } catch (CustomerNotFoundException ex) {
            throw new CustomerNotFoundException();
        } catch (CustomerEmailExistsException ex) {
            throw new CustomerEmailExistsException();
        } catch (UnknownPersistenceException e) {
            throw new UnknownPersistenceException();
        } catch (InputDataValidationException e) {
            throw new InputDataValidationException();
        }
        return resCode;

    }

    @WebMethod(operationName = "partnerCancelReservation")
    public String partnerCancelReservation(@WebParam(name = "username") String username,
            @WebParam(name = "reservationCode") String reservationCode,
            @WebParam(name = "currDate") String currDate) throws PartnerNotFoundException, ReservationNotFoundException {

        String message = "";
        try {
            Date currDateDate = dateTimeFormat.parse(currDate);
            message = reservationSessionBeanLocal.cancelReservationForPartner(username, reservationCode, currDateDate);
        } catch (ReservationNotFoundException ex) {
            throw new ReservationNotFoundException("Reservation Not Found");
        } catch (PartnerNotFoundException ex) {
            throw new PartnerNotFoundException("Partner Not Found");
        } catch (ParseException ex) {
            System.out.println("Invalid Date/Time Format");
        }
        return message;
    }

//    //MAYBE DONT NEED
//    @WebMethod(operationName = "partnerViewReservationDetails")
//    public void partnerViewReservationDetails(@WebParam(name = "partner") PartnerEntity partner) {
//
//    }

    @WebMethod(operationName = "retrieveReservationByCode")
    public ReservationEntity retrieveReservationByCode(@WebParam(name = "reservationCode") String resCode) throws ReservationNotFoundException {
        try {
            return reservationSessionBeanLocal.retrieveReservationByReservationCode(resCode);
        } catch (ReservationNotFoundException e) {
            throw new ReservationNotFoundException("Reservation Not Found");
        }
    }

    @WebMethod(operationName = "partnerViewAllReservations")
    public List<ReservationEntity> partnerViewAllReservations(@WebParam(name = "partnerID") Long partnerID) {

        return reservationSessionBeanLocal.retrieveReservationsOfPartnerID(partnerID);
    }

//    @WebMethod(operationName = "partnerLogout")
//    public void partnerLogout(/*@WebParam(name = "loggedInPartner") PartnerEntity loggedInPartner*/) {
//        return;
//    }

}

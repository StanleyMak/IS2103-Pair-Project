/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateless.CarSessionBeanLocal;
import ejb.session.stateless.OutletSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.RentalRateSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.PartnerEntity;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.InvalidLoginCredentialException;

/**
 *
 * @author stonley
 */
@WebService(serviceName = "PartnerWebService")
@Stateless()
public class PartnerWebService {

        
    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    @EJB
    private OutletSessionBeanLocal outletSessionBean;

    @EJB(name = "CarSessionBeanLocal")
    private CarSessionBeanLocal carSessionBeanLocal;

    @EJB(name = "ReservationSessionBeanLocal")
    private ReservationSessionBeanLocal reservationSessionBeanLocal;

    @EJB(name = "PartnerSessionBeanLocal")
    private PartnerSessionBeanLocal partnerSessionBeanLocal;
    
    @EJB(name = "RentalRateSessionBeanLocal")
    private RentalRateSessionBeanLocal rentalRateSessionBeanLocal;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "partnerLogin")
    public PartnerEntity partnerLogin(String username, String password) {
        try {
            PartnerEntity partner = partnerSessionBeanLocal.partnerLogin(username, password);
            return partner;
        } catch (InvalidLoginCredentialException e) {
            System.out.println("Error: " + e.getMessage() + "!\n");
        }
        return new PartnerEntity();
    }
    
    @WebMethod(operationName = "partnerSearchCar")
    public String partnerSearchCar(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    @WebMethod(operationName = "partnerReserveCar")
    public String partnerReserveCar(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    @WebMethod(operationName = "partnerCancelReservation")
    public String partnerCancelReservation(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    @WebMethod(operationName = "partnerViewReservationDetails")
    public String partnerViewReservationDetails(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    @WebMethod(operationName = "partnerViewAllReservations")
    public String partnerViewAllReservations(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }
    
    @WebMethod(operationName = "partnerLogout")
    public String partnerLogout(@WebParam(name = "name") String txt) {
        return "Hello " + txt + " !";
    }


}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package reservationclient;

import ejb.session.stateless.CarSessionBeanRemote;
import ejb.session.stateless.CustomerSessionBeanRemote;
import ejb.session.stateless.OutletSessionBeanRemote;
import ejb.session.stateless.RentalRateSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author stonley
 */
public class Main {

    @EJB(name = "RentalRateSessionBeanRemote")
    private static RentalRateSessionBeanRemote rentalRateSessionBeanRemote;

    @EJB(name = "OutletSessionBeanRemote")
    private static OutletSessionBeanRemote outletSessionBeanRemote;

    @EJB(name = "CarSessionBeanRemote")
    private static CarSessionBeanRemote carSessionBeanRemote;

    @EJB(name = "ReservationSessionBeanRemote")
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;

    @EJB(name = "CustomerSessionBeanRemote")
    private static CustomerSessionBeanRemote customerSessionBeanRemote;
    

    public static void main(String[] args) {
        MainApp mainApp = new MainApp(customerSessionBeanRemote, reservationSessionBeanRemote, carSessionBeanRemote, outletSessionBeanRemote, rentalRateSessionBeanRemote);
        mainApp.runApp();
    }
    
}

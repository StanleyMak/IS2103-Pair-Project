/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hanyang
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    public Long createNewReservation(ReservationEntity reservation) {
        em.persist(reservation);
        em.flush(); 
        
        return reservation.getReservationID(); 
    }
    
    public ReservationEntity retrieveReservationByID(Long reservationID) {
        return em.find(ReservationEntity.class, reservationID);
    }

}

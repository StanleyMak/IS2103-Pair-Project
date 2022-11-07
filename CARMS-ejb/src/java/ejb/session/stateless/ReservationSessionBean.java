/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author hanyang
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    @Override
    public Long createNewReservation(ReservationEntity reservation) {
        em.persist(reservation);
        em.flush(); 
        
        return reservation.getReservationID(); 
    }
    
    @Override
    public ReservationEntity retrieveReservationByID(Long reservationID) {
        ReservationEntity reservation = em.find(ReservationEntity.class, reservationID);
        //reservation.getXX().size();
        return reservation;
    }
    
    @Override
    public ReservationEntity retrieveReservationByReservationCode(String reservationCode) {
        Query query = em.createQuery("SELECT rc FROM ReservationEntity rc WHERE rc.resverationCode LIKE ?1")
                .setParameter(1, reservationCode); 
        
        ReservationEntity reservation  = (ReservationEntity) query; 
        //reservation.getXX().size();
        return reservation;
    }
    
    @Override
    public void deleteReservation(Long reservationID) {
        ReservationEntity reservation = retrieveReservationByID(reservationID);
        //dissociate
        em.remove(reservation);
    }
    
    @Override 
    public List<ReservationEntity> retrieveAllReservations() {
        Query query = em.createQuery("SELECT r FROM ReservationEntity r");
        return query.getResultList();
    }
    
    

}

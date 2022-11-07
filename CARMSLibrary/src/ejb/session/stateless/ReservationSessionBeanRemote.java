/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.ReservationEntity;
import java.util.List;
import javax.ejb.Remote;

/**
 *
 * @author hanyang
 */
@Remote
public interface ReservationSessionBeanRemote {
    
    public Long createNewReservation(ReservationEntity reservation);
    
    public ReservationEntity retrieveReservationByID(Long reservationID);
    
    public ReservationEntity retrieveReservationByReservationCode(String reservationCode);
    
    public void deleteReservation(Long reservationID);
    public List<ReservationEntity> retrieveAllReservations();
    
}

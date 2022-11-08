/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DispatchRecordEntity;
import entity.OutletEntity;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author stonley
 */
@Stateless
public class DispatchRecordSessionBean implements DispatchRecordSessionBeanRemote, DispatchRecordSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    public Long createNewDispatchRecord(DispatchRecordEntity dispatchRecord) {
        em.persist(dispatchRecord);
        em.flush();
        
        return dispatchRecord.getDispatchRecordID();
    }
    
    public DispatchRecordEntity retrieveDisptachRecordByDispatchRecordID(Long dispatchRecordID) {
        DispatchRecordEntity dispatchRecord = em.find(DispatchRecordEntity.class, dispatchRecordID);
        return dispatchRecord;
    }
    
    public DispatchRecordEntity retrieveDispatchRecordByDispatchRecordName(String dispatchRecordName) {
        Query query = em.createQuery("SELECT c FROM CarModel c WHERE c.model = ?1")
                .setParameter(1, dispatchRecordName);
        DispatchRecordEntity dispatchRecord = (DispatchRecordEntity) query.getSingleResult();
        
        return dispatchRecord;
    }
    
    public List<DispatchRecordEntity> retrieveDispatchRecordsForCurrentDayCurrentOutlet(Date date, OutletEntity outlet) {
        Query query = em.createQuery("SELECT d FROM DispatchRecordEntity WHERE d.outlet.address = ?1 AND d.pickUpTime = ?2")
                .setParameter(1, outlet.getAddress())
                .setParameter(2, date);
        List<DispatchRecordEntity> dispatchRecords = query.getResultList();
        return dispatchRecords;
    }
    
    private void updateDispatchRecordAsCompleted(Long dispatchRecordID) {
        DispatchRecordEntity dispatchRecord = retrieveDisptachRecordByDispatchRecordID(dispatchRecordID);
        dispatchRecord.setIsCompleted(Boolean.TRUE);
        em.merge(dispatchRecord);
    }
    
    private void deleteDispatchRecord(Long dispatchRecordID) {
        DispatchRecordEntity dispatchRecord = retrieveDisptachRecordByDispatchRecordID(dispatchRecordID);
        //dissociate
        
        em.remove(dispatchRecord);
    }
    
}

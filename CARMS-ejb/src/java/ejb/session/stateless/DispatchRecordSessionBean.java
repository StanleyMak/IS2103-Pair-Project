/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.DispatchRecordEntity;
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
    
    public DispatchRecordEntity retrieveCarModelByDispatchRecordID(Long dispatchRecordID) {
        DispatchRecordEntity dispatchRecord = em.find(DispatchRecordEntity.class, dispatchRecordID);
        return dispatchRecord;
    }
    
    public DispatchRecordEntity retrieveCarModelByDispatchRecordName(String dispatchRecordName) {
        Query query = em.createQuery("SELECT c FROM CarModel c WHERE c.model = ?1")
                .setParameter(1, dispatchRecordName);
        DispatchRecordEntity dispatchRecord = (DispatchRecordEntity) query.getSingleResult();
        
        return dispatchRecord;
    }
    
    private void updateDispatchRecord(DispatchRecordEntity dispatchRecord) {
        
    }
    
    private void deleteDispatchRecord(Long dispatchRecordID) {
        DispatchRecordEntity dispatchRecord = retrieveCarModelByDispatchRecordID(dispatchRecordID);
        //dissociate
        
        em.remove(dispatchRecord);
    }
    
}

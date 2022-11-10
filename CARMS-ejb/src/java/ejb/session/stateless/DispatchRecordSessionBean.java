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
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.DispatchRecordNameExistsException;
import util.exception.DispatchRecordNotFoundException;

/**
 *
 * @author stonley
 */
@Stateless
public class DispatchRecordSessionBean implements DispatchRecordSessionBeanRemote, DispatchRecordSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    @Override
    public Long createNewDispatchRecord(DispatchRecordEntity dispatchRecord) throws DispatchRecordNameExistsException {
        try {
            DispatchRecordEntity thisDispatchRecord = retrieveDispatchRecordByDispatchRecordName(dispatchRecord.getDispatchRecordName());
            throw new DispatchRecordNameExistsException("Dispatch Record Already Exists");
        } catch (DispatchRecordNotFoundException e) {
            em.persist(dispatchRecord);
            em.flush();

            return dispatchRecord.getDispatchRecordID();
        }

    }

    @Override
    public DispatchRecordEntity retrieveDisptachRecordByDispatchRecordID(Long dispatchRecordID) throws DispatchRecordNotFoundException {
        DispatchRecordEntity dispatchRecord = em.find(DispatchRecordEntity.class, dispatchRecordID);
        if (dispatchRecord != null) {
            return dispatchRecord;
        } else {
            throw new DispatchRecordNotFoundException("Dispatch Record Not Found");
        }

    }

    @Override
    public DispatchRecordEntity retrieveDispatchRecordByDispatchRecordName(String dispatchRecordName) throws DispatchRecordNotFoundException {
        Query query = em.createQuery("SELECT d FROM DispatchRecordEntity d WHERE d.dispatchRecordName = ?1")
                .setParameter(1, dispatchRecordName);
        try {
            return (DispatchRecordEntity) query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            throw new DispatchRecordNotFoundException("Dispatch Record Name " + dispatchRecordName + " does not exist!");
        }
    }

    @Override
    public List<DispatchRecordEntity> retrieveDispatchRecordsForCurrentDayCurrentOutlet(Date date, OutletEntity outlet) {
        Query query = em.createQuery("SELECT d FROM DispatchRecordEntity WHERE d.outlet.address = ?1 AND d.pickUpTime = ?2")
                .setParameter(1, outlet.getAddress())
                .setParameter(2, date);
        List<DispatchRecordEntity> dispatchRecords = query.getResultList();
        return dispatchRecords;
    }

    @Override
    public void updateDispatchRecordAsCompleted(Long dispatchRecordID) throws DispatchRecordNotFoundException {
        try {
            DispatchRecordEntity dispatchRecord = retrieveDisptachRecordByDispatchRecordID(dispatchRecordID);
            dispatchRecord.setIsCompleted(Boolean.TRUE);
            em.merge(dispatchRecord);
        } catch (DispatchRecordNotFoundException e) {
            throw new DispatchRecordNotFoundException("Dispatch Record ID " + dispatchRecordID + " does not exist!");
        }
    }
//    no need delete
//    public void deleteDispatchRecord(Long dispatchRecordID) throws DispatchRecordNotFoundException {
//        DispatchRecordEntity dispatchRecord = retrieveDisptachRecordByDispatchRecordID(dispatchRecordID);
//        //dissociate
//
//        em.remove(dispatchRecord);
//    }

}

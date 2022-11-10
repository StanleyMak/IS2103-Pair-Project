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
import javax.ejb.Remote;
import util.exception.DispatchRecordNameExistsException;
import util.exception.DispatchRecordNotFoundException;

/**
 *
 * @author stonley
 */
@Remote
public interface DispatchRecordSessionBeanRemote {
    
    public Long createNewDispatchRecord(DispatchRecordEntity dispatchRecord) throws DispatchRecordNameExistsException;

    public DispatchRecordEntity retrieveDisptachRecordByDispatchRecordID(Long dispatchRecordID) throws DispatchRecordNotFoundException;

    public DispatchRecordEntity retrieveDispatchRecordByDispatchRecordName(String dispatchRecordName) throws DispatchRecordNotFoundException;

    public List<DispatchRecordEntity> retrieveDispatchRecordsForCurrentDayCurrentOutlet(Date date, OutletEntity outlet);

    public void updateDispatchRecordAsCompleted(Long dispatchRecordID) throws DispatchRecordNotFoundException;
    
}

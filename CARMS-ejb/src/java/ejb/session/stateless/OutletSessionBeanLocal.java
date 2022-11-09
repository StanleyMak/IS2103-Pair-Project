/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OutletEntity;
import javax.ejb.Local;

/**
 *
 * @author stonley
 */
@Local
public interface OutletSessionBeanLocal {

    public OutletEntity retrieveOutletByOutletAddress(String outletAddress);

    public OutletEntity retrieveOutletByOutletID(Long outletID);
    
}

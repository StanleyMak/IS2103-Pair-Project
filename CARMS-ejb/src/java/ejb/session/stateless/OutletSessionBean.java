/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.OutletEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author stonley
 */
@Stateless
public class OutletSessionBean implements OutletSessionBeanRemote, OutletSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;

    @Override
    public OutletEntity retrieveOutletByOutletAddress(String outletAddress) {
        Query query = em.createQuery("SELECT o FROM OutletEntity o WHERE o.address = ?1")
                .setParameter(1, outletAddress);
        OutletEntity outlet = (OutletEntity) query.getSingleResult();
        outlet.getCars().size();
        
        return outlet;
    }
    
    @Override
    public OutletEntity retrieveOutletByOutletID(Long outletID) {
        OutletEntity outlet = em.find(OutletEntity.class, outletID);
        outlet.getCars().size();
        
        return outlet;
    }
}

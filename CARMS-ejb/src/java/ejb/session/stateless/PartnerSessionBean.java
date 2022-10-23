/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PartnerEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hanyang
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "CARMS-ejbPU")
    private EntityManager em;
    
    
    public Long createNewPartner(PartnerEntity partnerEntity) {
        em.persist(partnerEntity);
        em.flush(); 
        
        return partnerEntity.getPartnerID();
    }
    
    public PartnerEntity retrievePartnerEntityByID(Long partnerID) {
        return em.find(PartnerEntity.class, partnerID); 
    }
    
}

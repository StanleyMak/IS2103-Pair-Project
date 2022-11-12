/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PartnerEntity;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

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
    
    public PartnerEntity retrievePartnerByID(Long partnerID) {
        PartnerEntity partner = em.find(PartnerEntity.class, partnerID); 
        //partner.getXX().size();
        return partner; 
    }
    
    public PartnerEntity retrievePartnerByUsername(String username) throws PartnerNotFoundException {
        try {
            Query query = em.createQuery("SELECT p FROM PartnerEntity p WHERE p.username = ?1")
                    .setParameter(1, username);
            PartnerEntity partner = (PartnerEntity) query.getSingleResult();
            return partner;
        } catch (NoResultException | NonUniqueResultException e) {
            throw new PartnerNotFoundException("Partner username " + username + " not found");
        }
    }
    
    public void deletePartner(Long partnerID) {
        PartnerEntity partner = retrievePartnerByID(partnerID);
        //dissociate
        em.remove(partner);
    }
    
    @Override
    public PartnerEntity partnerLogin(String username, String password) throws InvalidLoginCredentialException {
        
        try {
            PartnerEntity partner = retrievePartnerByUsername(username);

            if (password.equals(partner.getPassword())) {
                return partner;
            } else {
                throw new InvalidLoginCredentialException("Invalid email or password");
            }
        } catch (PartnerNotFoundException ex) {
            throw new InvalidLoginCredentialException("Invalid username or password");
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.PartnerEntity;
import javax.ejb.Remote;
import util.exception.InvalidLoginCredentialException;
import util.exception.PartnerNotFoundException;

/**
 *
 * @author hanyang
 */
@Remote
public interface PartnerSessionBeanRemote {
    
    public PartnerEntity partnerLogin(String username, String password) throws InvalidLoginCredentialException;

    public Long createNewPartner(PartnerEntity partnerEntity);

    public PartnerEntity retrievePartnerByID(Long partnerID);

    public PartnerEntity retrievePartnerByUsername(String username) throws PartnerNotFoundException;

    public void deletePartner(Long partnerID);
    
}

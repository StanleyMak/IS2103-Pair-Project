/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RentalRateEntity;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author stonley
 */
@Local
public interface RentalRateSessionBeanLocal {

    public Long createNewRentalRate(RentalRateEntity rentalRate);

    public List<RentalRateEntity> retrieveAllRentalRates();

    public RentalRateEntity retrieveRentalRateByRentalRateID(Long rentalRateID);

    public RentalRateEntity retrieveRentalRateByRentalRateName(String rentalRateName);

    public void updateRentalRate(RentalRateEntity rentalRate);

    public void deleteRentalRate(Long rentalRateID);
    
}

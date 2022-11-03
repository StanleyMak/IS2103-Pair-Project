/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 *
 * @author hanyang
 */
@Entity
public class OwnCustomerEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ownCustomerID;

    public Long getOwnCustomerID() {
        return ownCustomerID;
    }

    public void setOwnCustomerID(Long ownCustomerID) {
        this.ownCustomerID = ownCustomerID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (ownCustomerID != null ? ownCustomerID.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the ownCustomerID fields are not set
        if (!(object instanceof OwnCustomerEntity)) {
            return false;
        }
        OwnCustomerEntity other = (OwnCustomerEntity) object;
        if ((this.ownCustomerID == null && other.ownCustomerID != null) || (this.ownCustomerID != null && !this.ownCustomerID.equals(other.ownCustomerID))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.OwnCustomerEntity[ id=" + ownCustomerID + " ]";
    }
    
}

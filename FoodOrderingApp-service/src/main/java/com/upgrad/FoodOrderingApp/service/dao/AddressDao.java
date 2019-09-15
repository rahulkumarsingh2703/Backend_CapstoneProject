package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class AddressDao {

    @PersistenceContext
    EntityManager entityManager;

    public AddressEntity getAddressByUUID (final String addressUUID) {
        try{
            return entityManager.createNamedQuery("addressByUUID", AddressEntity.class).setParameter("uuid", addressUUID).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AddressEntity saveAddress(AddressEntity addressEntity){
        entityManager.persist(addressEntity);
        return addressEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAddressEntity save(CustomerAddressEntity customerAddressEntity){
        entityManager.persist(customerAddressEntity);
        return customerAddressEntity;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteAddress(String address_uuid){
        try{
            AddressEntity addressEntity = getAddressByUUID(address_uuid);
            entityManager.remove(addressEntity);
            return addressEntity.getUuid();
        }
        catch (NoResultException nre) {
            return null;
        }
    }
}

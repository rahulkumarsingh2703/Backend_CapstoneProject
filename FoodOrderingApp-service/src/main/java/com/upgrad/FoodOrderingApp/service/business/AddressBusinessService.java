package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.AddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AddressBusinessService {

    @Autowired
    AddressDao addressDao;

    @Autowired
    StateDao  stateDao;

    @Autowired
    CustomerDao customerDao;

    @Autowired
    CustomerAddressDao customerAddressDao;



    public AddressEntity saveAddress(AddressEntity addressEntity, final String authorizationToken, final String stateUUID)
            throws  SaveAddressException, AuthorizationFailedException, AddressNotFoundException {

        // Validate Fields

        if (addressEntity.getCity() == null || addressEntity.getFlat_buil_number() == null
                || addressEntity.getLocality() == null || addressEntity.getPincode() == null) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }

        if (stateUUID == null) {
            throw new SaveAddressException("SAR-001", "No field can be empty");
        }

        // Validate State
        StateEntity stateEntity = checkState(stateUUID);

        if (stateEntity == null) {
            throw new AddressNotFoundException("ANF-002", "No state by this id");
        } else {
            addressEntity.setStateEntity(stateEntity);
        }


        // Validate User
        CustomerAuthTokenEntity customerAuth = customerDao.checkAuthToken(authorizationToken);

        if (customerAuth.equals(null)) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        final ZonedDateTime customerSignOutTime = customerAuth.getLogoutAt();

        if (customerSignOutTime != null && customerAuth != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime customerSessionExpireTime = customerAuth.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        if (customerSessionExpireTime.compareTo(currentTime) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }

        return addressDao.saveAddress(addressEntity);
    }


    public CustomerAddressEntity saveCustomerAddress(CustomerAddressEntity customerAddressEntity){

        return addressDao.save(customerAddressEntity);
    }

    public StateEntity checkState(String state_uuid) throws AddressNotFoundException{
        StateEntity stateEntity = stateDao.getStateByUUID(state_uuid);
        if(stateEntity == null){
            return null;
        }
        else {
            return stateEntity;
        }
    }

    public String deleteAddress(String address_uuid, String authorization) throws  AuthorizationFailedException, AddressNotFoundException {

        // Validate User
        CustomerAuthTokenEntity customerAuth = customerDao.checkAuthToken(authorization);
        AddressEntity addressEntity = addressDao.getAddressByUUID(address_uuid);
        CustomerAddressEntity customerAddressEntity = customerAddressDao.getSingleAddress(addressEntity);

        if (customerAuth.equals(null)) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        }
        final ZonedDateTime customerSignOutTime = customerAuth.getLogoutAt();

        if (customerSignOutTime != null && customerAuth != null) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        }

        final ZonedDateTime customerSessionExpireTime = customerAuth.getExpiresAt();
        ZonedDateTime currentTime = ZonedDateTime.now(ZoneId.systemDefault());
        if (customerSessionExpireTime.compareTo(currentTime) < 0) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access this endpoint.");
        }



        if(customerAuth.getCustomer().getId().equals(customerAddressEntity.getCustomerEntity().getId())) {
            return addressDao.deleteAddress(address_uuid);
        }else {
            throw new AuthorizationFailedException("ATHR-004", "You are not authorized to view/update/delete any one else's address");
        }

    }
}

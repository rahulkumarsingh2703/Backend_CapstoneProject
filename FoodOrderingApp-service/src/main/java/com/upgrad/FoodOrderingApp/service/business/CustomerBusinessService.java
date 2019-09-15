package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CustomerAddressDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class CustomerBusinessService {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    CustomerAddressDao customerAddressDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthTokenEntity getCustomer(final String authorizationToken){

        CustomerAuthTokenEntity customerAuth = customerDao.checkAuthToken(authorizationToken);
        if(customerAuth == null){
            return null;
        }
        else {
            return customerAuth;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomerByUUID (final String uuid){
        CustomerEntity customerEntity = customerDao.checkUUID(uuid);
        if(customerEntity == null){
            return null;
        }
        else {
            return customerEntity;
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<CustomerAddressEntity> getAddressByCustomer(final String authorizationToken) throws AuthorizationFailedException {

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

        List<CustomerAddressEntity> customerAddressEntity = customerAddressDao.getAddressByCustomer(customerAuth.getCustomer());


        return customerAddressEntity;

    }
}

package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class UpdateCustomerService {

    @Autowired
    CustomerDao customerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer (String accessToken , String firstName , String lastName)throws AuthorizationFailedException,UpdateCustomerException{
        CustomerAuthTokenEntity authCustomer = customerDao.checkAuthToken(accessToken);
        final ZonedDateTime current = ZonedDateTime.now();

        if(authCustomer==null)
        {
            throw new AuthorizationFailedException("ATHR-001","Customer is not logged in");

        }
        if(firstName==null)
        {
            throw new UpdateCustomerException("UCR-002","firstname should not be empty");
        }
        if(authCustomer.getExpiresAt().isBefore(current))
        {
            throw new AuthorizationFailedException("ATHR-003","Your session expired , Log in again to access this endpoint");
        }

            CustomerEntity customerEntity = authCustomer.getCustomer();
            customerEntity.setFirstname(firstName);
            customerEntity.setLastname(lastName);
            CustomerEntity updatedCustomer = customerDao.updateCustomerDetails(customerEntity);
            return updatedCustomer;


    }
}

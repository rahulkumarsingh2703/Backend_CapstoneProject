package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
public class LoginBusinessService {
    @Autowired
    CustomerDao customerDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthTokenEntity authenticate (final String contactNumber , final String password) throws AuthenticationFailedException {


        CustomerEntity customerEntity = customerDao.checkContactNo(contactNumber);
        if (customerEntity == null ) {
            throw new AuthenticationFailedException("ATH-001", "This contact number is not being registered");
        }
        String encryptedPassword = cryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (encryptedPassword.equals(customerEntity.getPassword())) {
            // new token will be created
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            // setting up the token variables in user_auth tables
            CustomerAuthTokenEntity customerAuthTokenEntity = new CustomerAuthTokenEntity();
            customerAuthTokenEntity.setCustomer(customerEntity);
            customerAuthTokenEntity.setUuid(UUID.randomUUID().toString());
            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            customerAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(customerAuthTokenEntity.getUuid(), now, expiresAt));
            customerAuthTokenEntity.setLoginAt(now);
            customerAuthTokenEntity.setExpiresAt(expiresAt);
            customerDao.createAuthToken(customerAuthTokenEntity);
            return customerAuthTokenEntity;
        }
        else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Ceredentials");
        }

    }
}
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

@Service
public class UpdatePasswordService {
    @Autowired
    CustomerDao customerDao;
    @Autowired
    PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updatePassword(String accessToken,String newPassword,String oldPassword) throws UpdateCustomerException,AuthorizationFailedException{
        CustomerAuthTokenEntity authCustomer = customerDao.checkAuthToken(accessToken);
        CustomerEntity updateCustomerPassword=authCustomer.getCustomer();

        if(authCustomer==null)
        {
            throw new AuthorizationFailedException("ATHR-001","Customer is not logged in");
        }
        if(newPassword==null || oldPassword==null)
        {
            throw new UpdateCustomerException("UCR-003","No field should be empty");
        }
        if(authCustomer.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","Customer is logged out login again to access this endpoint");
        }
        oldPassword=passwordCryptographyProvider.encrypt(oldPassword,authCustomer.getCustomer().getSalt());

        if(!oldPassword.equals(authCustomer.getCustomer().getPassword())){
            throw new UpdateCustomerException("UCR-004","Incorrect old password");
        }

        String[] encryptedText = passwordCryptographyProvider.encrypt(newPassword);
        updateCustomerPassword.setSalt(encryptedText[0]);
        updateCustomerPassword.setPassword(encryptedText[1]);
        CustomerEntity changedCustomerPassword = customerDao.updatePassword(updateCustomerPassword);
        return changedCustomerPassword;
    }
}

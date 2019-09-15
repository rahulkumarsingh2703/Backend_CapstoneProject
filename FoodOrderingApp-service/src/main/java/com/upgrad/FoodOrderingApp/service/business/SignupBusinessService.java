package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.upgrad.FoodOrderingApp.service.business.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SignupBusinessService {

    @Autowired
    public PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    public CustomerDao customerDao;

    private static final String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[@#$%]).{3,10})";

    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity signup(CustomerEntity customerEntity) throws SignUpRestrictedException {
        CustomerEntity checkContactNum = customerDao.checkContactNo(customerEntity.getContact_Number());
        if(checkContactNum != null){
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try other contact number.");

        }
        if(customerEntity.getFirstname() == null
                && customerEntity.getEmail() == null
                && customerEntity.getContact_Number() == null
                && customerEntity.getPassword()== null
                ){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        validateCustomerData(customerEntity);
        //validateEmail(customerEntity.getEmail());
        //validateContactNo(customerEntity.getContact_Number());
        //validatePassword(customerEntity.getPassword());

        String password = customerEntity.getPassword();
        String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
        customerEntity.setSalt(encryptedText[0]);
        customerEntity.setPassword(encryptedText[1]);
        return customerDao.createCustomer(customerEntity);
    }

    public CustomerEntity validateCustomerData(CustomerEntity customerEntity) throws SignUpRestrictedException{
        if(customerEntity.getFirstname() == null
                || customerEntity.getEmail() == null
                || customerEntity.getContact_Number() == null
                || customerEntity.getPassword()== null
                ){
            throw new SignUpRestrictedException("SGR-005","Except last name all fields should be filled");
        }
        else {
            validateEmail(customerEntity.getEmail());
            validateContactNo(customerEntity.getContact_Number());
            validatePassword(customerEntity.getPassword());
        }
        return customerEntity;
    }

    private void validateContactNo(String contactNo) throws SignUpRestrictedException{
        if(Pattern.matches("[0-9]{10}", contactNo) == false){
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number");
        }
    }

    private void validateEmail(String email) throws SignUpRestrictedException{
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher =VALID_EMAIL_ADDRESS_REGEX.matcher(email);
        if(matcher.find() == false){
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        }
    }

    private void validatePassword(String password) throws SignUpRestrictedException{
        /*
            StringBuilder patternBuilder = new StringBuilder("((?=.*[a-z])");
            patternBuilder.append("(?=.*[@#$%])");
            patternBuilder.append("(?=.*[A-Z])");
            patternBuilder.append("(?=.*d)");
            patternBuilder.append(".{8})");

            String pattern = patternBuilder.toString();
        */
        Pattern p = Pattern.compile(PASSWORD_PATTERN);
        Matcher m = p.matcher(password);
        if(m.matches() == false){
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        }
    }
}

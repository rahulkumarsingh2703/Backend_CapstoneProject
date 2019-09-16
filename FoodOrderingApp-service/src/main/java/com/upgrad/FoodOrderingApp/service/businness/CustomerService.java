package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CustomerService {

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private AuthTokenValidityService authTokenValidityService;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    /**
     * Save Customer
     * @param customerEntity
     * @return
     * @throws SignUpRestrictedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) throws SignUpRestrictedException {

        if (customerDao.getCustomerByContactNumber(customerEntity.getContactNumber()) != null) {
            throw new SignUpRestrictedException("SGR-001", "This contact number is already registered! Try another contact number.");
        } else if (customerEntity.getFirstName() == null || customerEntity.getEmail() == null || customerEntity.getContactNumber() == null || customerEntity.getPassword() == null) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled!");
        } else if (!isValidEmail(customerEntity.getEmail())) {
            throw new SignUpRestrictedException("SGR-002", "Invalid email-id format!");
        } else if (!isValidContactNumber(customerEntity.getContactNumber())) {
            throw new SignUpRestrictedException("SGR-003", "Invalid contact number");
        } else if (!isPasswordStrong(customerEntity.getPassword())) {
            throw new SignUpRestrictedException("SGR-004", "Weak password!");
        } else {
            String[] encryptedText = passwordCryptographyProvider.encrypt(customerEntity.getPassword());
            customerEntity.setSalt(encryptedText[0]);
            customerEntity.setPassword(encryptedText[1]);
            return customerDao.createCustomer(customerEntity);
        }
    }

    /**
     * authenticate based on the token
     * @param contactnumber
     * @param password
     * @return
     * @throws AuthenticationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity authenticate(final String contactnumber, final String password) throws AuthenticationFailedException {

        CustomerEntity customerEntity = customerDao.getCustomerByContactNumber(contactnumber);
        if (customerEntity == null) {
            throw new AuthenticationFailedException("ATH-001", "This contact number has not been registered!");
        }

        final String encryptedPassword = passwordCryptographyProvider.encrypt(password, customerEntity.getSalt());
        if (encryptedPassword.equals(customerEntity.getPassword())) {
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            CustomerAuthEntity customerAuthEntity = new CustomerAuthEntity();
            customerAuthEntity.setCustomer(customerEntity);

            final ZonedDateTime now = ZonedDateTime.now();
            final ZonedDateTime expiresAt = now.plusHours(8);
            customerAuthEntity.setAccessToken(jwtTokenProvider.generateToken(customerEntity.getUuid(), now, expiresAt));
            customerAuthEntity.setUuid(UUID.randomUUID().toString());
            customerAuthEntity.setLogin_at(now);
            customerAuthEntity.setExpires_at(expiresAt);
            return customerDao.createAuthToken(customerAuthEntity);
        } else {
            throw new AuthenticationFailedException("ATH-002", "Invalid Credentials");
        }
    }

    /**
     *  logout
     * @param Access_Token
     * @return
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerAuthEntity logout(final String Access_Token) throws AuthorizationFailedException {
        CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthToken(Access_Token);
        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in");
        } else if (authTokenValidityService.isLoggedOut(customerAuthEntity)) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        } else if (authTokenValidityService.isExpired(customerAuthEntity)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access the endpoint");
        } else {
            customerAuthEntity.setLogout_at(ZonedDateTime.now());
            customerDao.updateCustomerEntity(customerAuthEntity);
        }
        return customerAuthEntity;
    }

    /**
     *  validate and update password
     * @param oldPassword
     * @param newPassword
     * @param customer
     * @return
     * @throws UpdateCustomerException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomerPassword(String oldPassword, String newPassword, CustomerEntity customer) throws UpdateCustomerException {

        if (!isPasswordStrong(newPassword)) {
            throw new UpdateCustomerException("UCR-001", "Weak password!");
        }
        final String decryptedPassword = passwordCryptographyProvider.encrypt(oldPassword, customer.getSalt());
        if (!decryptedPassword.equals(customer.getPassword())) {
            throw new UpdateCustomerException("UCR-004", "Incorrect old password");
        }
        String[] encryptedText = passwordCryptographyProvider.encrypt(newPassword);
        customer.setSalt(encryptedText[0]);
        customer.setPassword(encryptedText[1]);
        CustomerEntity changedCustomerPassword = customerDao.updatePassword(customer);
        return changedCustomerPassword;
    }

    /**
     * update customer
     * @param customer
     * @return
     * @throws UpdateCustomerException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity updateCustomer(CustomerEntity customer) throws UpdateCustomerException {

        if (!StringUtils.isNotEmpty(customer.getFirstName())) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        return customerDao.updateCustomer(customer);
    }

    /**
     *  update the customer
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public CustomerEntity getCustomer(final String authorization) throws AuthorizationFailedException {
        final CustomerAuthEntity customerAuthEntity = customerDao.getCustomerAuthToken(authorization);
        if (customerAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "Customer is not Logged in.");
        } else if (authTokenValidityService.isLoggedOut(customerAuthEntity)) {
            throw new AuthorizationFailedException("ATHR-002", "Customer is logged out. Log in again to access this endpoint.");
        } else if (authTokenValidityService.isExpired(customerAuthEntity)) {
            throw new AuthorizationFailedException("ATHR-003", "Your session is expired. Log in again to access the endpoint");
        }
        return customerAuthEntity.getCustomer();
    }

    /**
     *  Validate the email
     * @param email
     * @return
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    /**
     *  Validate the contact number
     * @param number
     * @return
     */
    public static boolean isValidContactNumber(String number) {
        String contactNumberRegex = "^$|[0-9]{10}";
        Pattern pat = Pattern.compile(contactNumberRegex);
        if (number == null)
            return false;
        return pat.matcher(number).matches();
    }

    /**
     * Validate the password strength
     * @param newPassword
     * @return
     */
    public static boolean isPasswordStrong(String newPassword) {
        if (newPassword == null)
            return false;
        if (newPassword.length() < 8 || !newPassword.matches("(?=.*[0-9]).*") || !newPassword.matches("(?=.*[A-Z]).*") || !newPassword.matches("(?=.*[#@$%&*!^]).*")) {
            return false;
        }
        return true;
    }
}

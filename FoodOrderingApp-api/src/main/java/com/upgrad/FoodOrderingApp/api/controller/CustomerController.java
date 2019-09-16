package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.*;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SignUpRestrictedException;
import com.upgrad.FoodOrderingApp.service.exception.UpdateCustomerException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

@CrossOrigin
@RestController
@RequestMapping("/")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     *  1. signup endpoint
     * @param signupCustomerRequest
     * @return
     * @throws SignUpRestrictedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/signup", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(@RequestBody(required = false) final SignupCustomerRequest signupCustomerRequest) throws SignUpRestrictedException {

        if (StringUtils.isEmpty(signupCustomerRequest.getFirstName()) ||
                StringUtils.isEmpty(signupCustomerRequest.getEmailAddress()) ||
                StringUtils.isEmpty(signupCustomerRequest.getContactNumber()) ||
                StringUtils.isEmpty(signupCustomerRequest.getPassword())
        ) {
            throw new SignUpRestrictedException("SGR-005", "Except last name all fields should be filled");
        }

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setUuid(UUID.randomUUID().toString());
        customerEntity.setFirstName(signupCustomerRequest.getFirstName());
        customerEntity.setLastName(signupCustomerRequest.getLastName());
        customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
        customerEntity.setContactNumber(signupCustomerRequest.getContactNumber());
        customerEntity.setPassword(signupCustomerRequest.getPassword());
        customerEntity.setSalt("abc1234");
        final CustomerEntity createdCustomerEntity;
        createdCustomerEntity = customerService.saveCustomer(customerEntity);
        SignupCustomerResponse userResponse = new SignupCustomerResponse().id(createdCustomerEntity.getUuid()).status("CUSTOMER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupCustomerResponse>(userResponse, HttpStatus.CREATED);
    }

    /**
     *  2. login endpoint
     * @param authorization
     * @return
     * @throws AuthenticationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {
        //validate the decoding of the token
        String[] decodeArray;
        try {
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodeText = new String(decode);
            decodeArray = decodeText.split(":");
            if (decodeArray.length < 2) {
                throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
            throw new AuthenticationFailedException("ATH-003", "Incorrect format of decoded customer name and password");
        }
        //Authenticate the customer
        String contactNumber = decodeArray[0];
        String password = decodeArray[1];
        CustomerAuthEntity customerAuthEntity = customerService.authenticate(contactNumber, password);
        //Set the response
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setMessage("LOGGED IN SUCCESSFULLY");
        loginResponse.setId(customerAuthEntity.getCustomer().getUuid());
        loginResponse.setFirstName(customerAuthEntity.getCustomer().getFirstName());
        loginResponse.setLastName(customerAuthEntity.getCustomer().getLastName());
        loginResponse.setContactNumber(customerAuthEntity.getCustomer().getContactNumber());
        loginResponse.setEmailAddress(customerAuthEntity.getCustomer().getEmail());
        //Set the header
        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", customerAuthEntity.getAccessToken());
        List<String> header = new ArrayList<>();
        header.add("access-token");
        headers.setAccessControlExposeHeaders(header);
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }

    /**
     *  3. logout endpoint
     * @param authorization
     * @return
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = RequestMethod.POST, path = "/customer/logout", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {

        String accessToken = authorization.split("Bearer ")[1];
        CustomerAuthEntity loggedoutUserEntity = customerService.logout(accessToken);
        LogoutResponse logoutResponse = new LogoutResponse();
        logoutResponse.setMessage("LOGGED OUT SUCCESSFULLY");
        logoutResponse.setId(loggedoutUserEntity.getCustomer().getUuid());
        return new ResponseEntity<LogoutResponse>(logoutResponse, HttpStatus.OK);
    }

    /**
     *  4. update endpoint
     * @param authorization
     * @param updateCustomer
     * @return
     * @throws UpdateCustomerException
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = PUT, path = "/customer", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdateCustomerRequest> updateCustomer(@RequestHeader("authorization") String authorization, @RequestBody(required = false) final UpdateCustomerRequest updateCustomer)
            throws UpdateCustomerException, AuthorizationFailedException {
        //Validate the first name
        if (StringUtils.isEmpty(updateCustomer.getFirstName())) {
            throw new UpdateCustomerException("UCR-002", "First name field should not be empty");
        }
        //get the customer based on token
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        //set the first and last name
        customerEntity.setFirstName(updateCustomer.getFirstName());
        if (!updateCustomer.getLastName().equals("")) {
            customerEntity.setLastName(updateCustomer.getLastName());
        }
        //Update the customer
        CustomerEntity updatedCustomerEntity = customerService.updateCustomer(customerEntity);
        //Prepare the response
        UpdateCustomerResponse updateCustomerResponse = new UpdateCustomerResponse();
        updateCustomerResponse.setId(updatedCustomerEntity.getUuid());
        updateCustomerResponse.setFirstName(updatedCustomerEntity.getFirstName());
        updateCustomerResponse.setLastName(updatedCustomerEntity.getLastName());
        updateCustomerResponse.setStatus("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity(updateCustomerResponse, HttpStatus.OK);
    }

    /**
     *  5.change password endpoint
     * @param authorization
     * @param updatePasswordRequest
     * @return
     * @throws UpdateCustomerException
     * @throws AuthorizationFailedException
     */
    @RequestMapping(method = PUT, path = "/customer/password", consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UpdatePasswordRequest> updateCustomerPassword(@RequestHeader("authorization") String authorization, @RequestBody(required = false)final UpdatePasswordRequest updatePasswordRequest)
            throws UpdateCustomerException, AuthorizationFailedException {
        //validate if password is not empty
        if (updatePasswordRequest.getOldPassword().equals("") || updatePasswordRequest.getNewPassword().equals("")) {
            throw new UpdateCustomerException("UCR-003", "No field should be empty");
        }
        //get the customer based on the authorization
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);
        //update the password
        CustomerEntity updatedCustomerEntity = customerService.updateCustomerPassword(updatePasswordRequest.getOldPassword(), updatePasswordRequest.getNewPassword(), customerEntity);
        UpdatePasswordResponse updatePasswordResponse = new UpdatePasswordResponse();
        updatePasswordResponse.setId(updatedCustomerEntity.getUuid());
        updatePasswordResponse.setStatus("CUSTOMER DETAILS UPDATED SUCCESSFULLY");
        return new ResponseEntity(updatePasswordResponse, HttpStatus.OK);
    }
}

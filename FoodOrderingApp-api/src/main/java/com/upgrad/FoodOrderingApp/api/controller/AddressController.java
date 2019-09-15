package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.business.AddressBusinessService;
import com.upgrad.FoodOrderingApp.service.business.CustomerBusinessService;
import com.upgrad.FoodOrderingApp.service.business.SignupBusinessService;
import com.upgrad.FoodOrderingApp.service.business.StateBusinessService;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.*;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
@RequestMapping("/")
@CrossOrigin
public class AddressController {

    @Autowired
    StateBusinessService stateBusinessService;

    @Autowired
    AddressBusinessService addressBusinessService;

    @Autowired
    CustomerBusinessService customerBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/address", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(final SaveAddressRequest saveAddressRequest, @RequestHeader("authorization") final String authorization)
        throws  SaveAddressException,AuthorizationFailedException, AddressNotFoundException {

        String [] bearerToken = authorization.split("Bearer ");

        AddressEntity addressEntity = new AddressEntity();

        addressEntity.setFlat_buil_number(saveAddressRequest.getFlatBuildingName());
        addressEntity.setCity(saveAddressRequest.getCity());
        addressEntity.setLocality(saveAddressRequest.getLocality());
        addressEntity.setPincode(saveAddressRequest.getPincode());
        addressEntity.setUuid(UUID.randomUUID().toString());
        addressEntity.setActive(1);

        final AddressEntity createdAddress = addressBusinessService.saveAddress(addressEntity, bearerToken[1], saveAddressRequest.getStateUuid());
        final CustomerAuthTokenEntity customerAuthTokenEntity = customerBusinessService.getCustomer(bearerToken[1]);

        final CustomerAddressEntity customerAddressEntity = new CustomerAddressEntity();
        customerAddressEntity.setAddressEntity(createdAddress);
        customerAddressEntity.setCustomerEntity(customerAuthTokenEntity.getCustomer());
        addressBusinessService.saveCustomerAddress(customerAddressEntity);

        final SaveAddressResponse saveAddressResponse = new SaveAddressResponse().id(createdAddress.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");

        return new ResponseEntity<SaveAddressResponse>(saveAddressResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<StatesListResponse>> getAllStates(){
        List<StateEntity> SB = stateBusinessService.getAllStates();

        List<StatesListResponse> states = new ArrayList<>();

        for(StateEntity stateEntity : SB) {
            UUID stateUUID = UUID.fromString(stateEntity.getUuid());
            StatesList statesList = new StatesList().id(stateUUID).stateName(stateEntity.getState_name());
            StatesListResponse statesListResponse = new StatesListResponse().addStatesItem(statesList);
            states.add(statesListResponse);
        }

        return new ResponseEntity<List<StatesListResponse>>(states, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AddressListResponse>> getAllAddressByCustomer (@RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException {

        String [] bearerToken = authorization.split("Bearer ");

        List<CustomerAddressEntity> AE = customerBusinessService.getAddressByCustomer(bearerToken[1]);

        List<AddressListResponse> address = new ArrayList<>();

        for(CustomerAddressEntity ce : AE){
            AddressEntity a = ce.getAddressEntity();
            AddressListState addressListState = new AddressListState().id(UUID.fromString(a.getStateEntity().getUuid())).stateName(a.getStateEntity().getState_name());
            AddressList addressList = new AddressList()
                    .id(UUID.fromString(a.getUuid()))
                    .flatBuildingName(a.getFlat_buil_number())
                    .locality(a.getLocality())
                    .city(a.getCity())
                    .pincode(a.getPincode())
                    .state(addressListState);
            AddressListResponse alr = new AddressListResponse().addAddressesItem(addressList);
            address.add(alr);
        }

        return new ResponseEntity<List<AddressListResponse>>(address, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path="address/{addressId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@PathVariable("addressId") final String addressID, @RequestHeader("authorization") final String authorization)
        throws AuthorizationFailedException, AddressNotFoundException {

        String [] bearerToken = authorization.split("Bearer ");

        final String deleteAddress = addressBusinessService.deleteAddress(addressID,bearerToken[1]);

        DeleteAddressResponse deleteAddressResponse = new DeleteAddressResponse().id(UUID.fromString(deleteAddress)).status("Address Deleted");

        return  new ResponseEntity<DeleteAddressResponse>(deleteAddressResponse, HttpStatus.OK);

    }

}

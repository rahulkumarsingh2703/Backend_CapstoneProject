package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.service.businness.AddressService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.entity.AddressEntity;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import com.upgrad.FoodOrderingApp.service.exception.AddressNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.SaveAddressException;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AddressController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AddressService addressService;

    /**
     * 
     * @return ResponseEntity<SaveAddressResponse> type object along with HttpStatus Ok
     */
    @CrossOrigin
    @RequestMapping(method = RequestMethod.POST, path = "/address", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SaveAddressResponse> saveAddress(
            @RequestBody(required = false) final SaveAddressRequest saveAddressRequest,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, SaveAddressException, AddressNotFoundException
    {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        final AddressEntity address = new AddressEntity();
        address.setUuid(UUID.randomUUID().toString());
        address.setFlatBuilNo(saveAddressRequest.getFlatBuildingName());
        address.setLocality(saveAddressRequest.getLocality());
        address.setCity(saveAddressRequest.getCity());
        address.setPincode(saveAddressRequest.getPincode());
        address.setState(addressService.getStateByUUID(saveAddressRequest.getStateUuid()));
        address.setActive(1);

        final AddressEntity savedAddress = addressService.saveAddress(address, customerEntity);
        SaveAddressResponse addressResponse = new SaveAddressResponse().id(savedAddress.getUuid()).status("ADDRESS SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SaveAddressResponse>(addressResponse, HttpStatus.CREATED);
    }

    /**
     *  
     * @return ResponseEntity<AddressListResponse> type object along with HttpStatus OK
     */
    @RequestMapping(method = RequestMethod.GET, path = "/address/customer", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AddressListResponse> getAllSavedAddresses(
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException
    {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);


        List<AddressEntity> addressesList = addressService.getAllAddress(customerEntity);


        AddressListResponse addressListResponse = new AddressListResponse();

        for (AddressEntity addressEntity : addressesList) {
            AddressList addressesResponse = new AddressList()
                    .id(UUID.fromString(addressEntity.getUuid()))
                    .flatBuildingName(addressEntity.getFlatBuilNo())
                    .locality(addressEntity.getLocality())
                    .city(addressEntity.getCity())
                    .pincode(addressEntity.getPincode())
                    .state(new AddressListState().id(UUID.fromString(addressEntity.getState().getUuid()))
                            .stateName(addressEntity.getState().getStatename()));
            addressListResponse.addAddressesItem(addressesResponse);
        }

        return new ResponseEntity<AddressListResponse>(addressListResponse, HttpStatus.OK);
    }

    /**
     *
     *
     * @return ResponseEntity<StatesListResponse> type object along with HttpStatus OK
     */
    @RequestMapping(method = RequestMethod.GET, path = "/states", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<StatesListResponse> getAllStates()
    {
        // Get all states
        List<StateEntity> statesList = addressService.getAllStates();

        // Response for Get All States
        StatesListResponse statesListResponse = new StatesListResponse();

        for (StateEntity stateEntity : statesList) {
            StatesList listState = new StatesList()
                    .id(UUID.fromString(stateEntity.getUuid()))
                    .stateName(stateEntity.getStatename());
            statesListResponse.addStatesItem(listState);
        }

        return new ResponseEntity<StatesListResponse>(statesListResponse, HttpStatus.OK);
    }

    /**
     *
     * @return ResponseEntity<DeleteAddResponse> with HTTP status ok
     */
    @RequestMapping(method = RequestMethod.DELETE, path = "/address/{address_id}")
    public ResponseEntity<DeleteAddressResponse> deleteSavedAddress(
            @PathVariable("address_id") final String addressID,
            @RequestHeader("authorization") final String authorization)
            throws AuthorizationFailedException, AddressNotFoundException
    {
        String accessToken = authorization.split("Bearer ")[1];
        CustomerEntity customerEntity = customerService.getCustomer(accessToken);

        if (addressID.equals("")) {
            throw new AddressNotFoundException("ANF-005", "Address id can not be empty");
        }

        AddressEntity addressEntity = addressService.getAddressByUUID(addressID, customerEntity);
        AddressEntity deletedAddressEntity = addressService.deleteAddress(addressEntity);
        DeleteAddressResponse addDeleteResponse = new DeleteAddressResponse().id(UUID.fromString(deletedAddressEntity.getUuid())).status("ADDRESS DELETED SUCCESSFULLY");
        return new ResponseEntity<DeleteAddressResponse>(addDeleteResponse, HttpStatus.OK);
    }
}

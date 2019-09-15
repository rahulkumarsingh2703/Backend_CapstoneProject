package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.CouponDao;
import com.upgrad.FoodOrderingApp.service.dao.CustomerDao;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.entity.CustomerAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
public class CouponBusinessService {

    @Autowired
    CouponDao couponDao;

    @Autowired
    CustomerDao customerDao;

    /*
    *
    * public StateEntity checkState(String state_uuid) throws AddressNotFoundException{
        StateEntity stateEntity = stateDao.getStateByUUID(state_uuid);
        if(stateEntity == null){
            return null;
        }
        else {
            return stateEntity;
        }
    }
    * */

    public CouponEntity getCouponByName(String couponName, String authorization) throws CouponNotFoundException, AuthorizationFailedException{
        CouponEntity couponEntity = couponDao.getCouponByName(couponName);

        // Validate User
        CustomerAuthTokenEntity customerAuth = customerDao.checkAuthToken(authorization);

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

        if(couponEntity == null){
            throw new CouponNotFoundException("CPF-001","No coupon by this name");
        }else {
            return couponEntity;
        }
    }
}

package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CouponDetailsResponse;
import com.upgrad.FoodOrderingApp.service.business.CouponBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CouponNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/")
@CrossOrigin
public class OrderController {

    @Autowired
    CouponBusinessService couponBusinessService;
    @RequestMapping(method = RequestMethod.GET, path = "/order/coupon/{coupon_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    //public ResponseEntity<List<CategoryListResponse>> getAllCategories(){
    public ResponseEntity<CouponDetailsResponse> getCoupon(@PathVariable("coupon_name") final String couponName, @RequestHeader("authorization") final String authorization)
        throws AuthorizationFailedException, CouponNotFoundException
    {
        CouponEntity couponEntity = couponBusinessService.getCouponByName(couponName,authorization);

        CouponDetailsResponse couponDetailsResponse = new CouponDetailsResponse().id(UUID.fromString(couponEntity.getUUID())).couponName(couponEntity.getCouponName());

        return new ResponseEntity<CouponDetailsResponse>(couponDetailsResponse, HttpStatus.OK);
    }
}

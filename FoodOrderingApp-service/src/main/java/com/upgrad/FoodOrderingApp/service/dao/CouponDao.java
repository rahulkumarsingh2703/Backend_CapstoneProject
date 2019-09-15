package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.CouponEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class CouponDao {

    @PersistenceContext
    EntityManager entityManager;

    public CouponEntity getCouponByUUID(String couponUUID){
        try {
            return entityManager.createNamedQuery("getCouponByUUID", CouponEntity.class).setParameter("UUID", couponUUID).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public CouponEntity getCouponByName(String couponName){
        try {
            return entityManager.createNamedQuery("getCouponByName", CouponEntity.class).setParameter("couponName", couponName).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }
}

package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentBusinessService {

    @Autowired
    PaymentDao paymentDao;
    @Transactional(propagation = Propagation.REQUIRED)
    public List<PaymentEntity> getAllPayment(){
        List<PaymentEntity> payment = paymentDao.getAllPayment();
        return payment;
    }
}

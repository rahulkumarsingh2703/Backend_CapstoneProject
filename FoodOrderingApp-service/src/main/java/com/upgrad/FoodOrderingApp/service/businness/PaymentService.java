package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.PaymentDao;
import com.upgrad.FoodOrderingApp.service.entity.PaymentEntity;
import com.upgrad.FoodOrderingApp.service.exception.PaymentMethodNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PaymentService {

    @Autowired
    PaymentDao paymentDao;
    @Transactional(propagation = Propagation.REQUIRED)
    public List<PaymentEntity> getAllPaymentMethods(){
        List<PaymentEntity> payment = paymentDao.getAllPaymentMethods();
        return payment;
    }

    public PaymentEntity getPaymentByUUID(String uuid) throws PaymentMethodNotFoundException {
        PaymentEntity paymentEntity = paymentDao.getPaymentByUUID(uuid);
        if (paymentEntity == null) {
            throw new PaymentMethodNotFoundException("PNF-002", "No payment method found by this id");
        }
        return paymentEntity;
    }
}

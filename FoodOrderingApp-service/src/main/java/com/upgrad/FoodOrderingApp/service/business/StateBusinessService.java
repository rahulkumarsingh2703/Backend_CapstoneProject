package com.upgrad.FoodOrderingApp.service.business;

import com.upgrad.FoodOrderingApp.service.dao.StateDao;
import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateBusinessService {

    @Autowired
    StateDao stateDao;

    public List<StateEntity> getAllStates(){
        List<StateEntity> allStates = stateDao.getAllStates();

        return allStates;
    }

}

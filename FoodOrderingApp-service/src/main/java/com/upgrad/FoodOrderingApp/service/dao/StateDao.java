package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.StateEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class StateDao {

    @PersistenceContext
    EntityManager entityManager;

    public List<StateEntity> getAllStates() {
        try {
            Query query = entityManager.createQuery("select se from StateEntity se order by se.state_name");
            return new ArrayList<StateEntity>(query.getResultList());
        } catch (NoResultException nre) {
            return null;
        }
    }

    public StateEntity getStateByUUID(final String stateUUID){
        try {
            return entityManager.createNamedQuery("getStateByUUID",StateEntity.class).setParameter("uuid", stateUUID).getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }
}
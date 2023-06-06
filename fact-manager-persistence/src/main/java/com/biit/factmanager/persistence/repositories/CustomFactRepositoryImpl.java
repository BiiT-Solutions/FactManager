package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;


import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CustomFactRepositoryImpl<T extends Fact<?>> implements CustomFactRepository<T> {

    @PersistenceContext
    @Qualifier(value = "factmanagerSystemFactory")
    private EntityManager entityManager;

    private Class<T> entityTypeClass;

    private T data;


    public CustomFactRepositoryImpl() {
        //Get class from T.
        Field field;
        try {
            field = this.getClass().getDeclaredField("data");
            entityTypeClass = (Class<T>) field.getType();
        } catch (NoSuchFieldException e) {
            FactManagerLogger.errorMessage(this.getClass(), e);
        }

    }

    protected T getData() {
        return data;
    }

    protected void setData(T data) {
        this.data = data;
    }

    @Override
    public List<T> findByValueParameters(Pair<String, Object>... valueParameters) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> query = criteriaBuilder.createQuery(entityTypeClass);
        final Root<T> root = query.from(entityTypeClass);

        final List<Predicate> predicates = new ArrayList<>();
        for (final Pair<String, Object> condition : valueParameters) {
            //ON JSON numbers are not using quotes.
            if (condition.getSecond() instanceof Number) {
                predicates.add(criteriaBuilder.like(root.get("value"), String.format("%%\"%s\":%s%%", condition.getFirst(), condition.getSecond())));
            } else {
                predicates.add(criteriaBuilder.like(root.get("value"), String.format("%%\"%s\":\"%s\"%%", condition.getFirst(), condition.getSecond())));
            }
        }

        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
        query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(query).getResultList();
    }

    @Override
    public List<T> findBy(Class<T> entityTypeClass, String organization, String customer, String application, String tenant, String tag,
                          String group, String element, String process, LocalDateTime startDate, LocalDateTime endDate,
                          Boolean discriminatorValue, Pair<String, Object>... valueParameters) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> query = criteriaBuilder.createQuery(entityTypeClass == null ? this.entityTypeClass : entityTypeClass);
        final Root<T> root = query.from(entityTypeClass == null ? this.entityTypeClass : entityTypeClass);

        final List<Predicate> predicates = new ArrayList<>();
        if (organization != null) {
            predicates.add(criteriaBuilder.equal(root.get("organization"), organization));
        }
        if (customer != null) {
            predicates.add(criteriaBuilder.equal(root.get("customer"), customer));
        }
        if (application != null) {
            predicates.add(criteriaBuilder.equal(root.get("application"), application));
        }
        if (tenant != null) {
            predicates.add(criteriaBuilder.equal(root.get("tenant"), tenant));
        }
        if (process != null) {
            predicates.add(criteriaBuilder.equal(root.get("process"), process));
        }
        if (tag != null) {
            predicates.add(criteriaBuilder.equal(root.get("tag"), tag));
        }
        if (group != null) {
            predicates.add(criteriaBuilder.equal(root.get("group"), group));
        }
        if (element != null) {
            predicates.add(criteriaBuilder.equal(root.get("element"), element));
        }
        if (startDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }
        if (endDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }
        if (discriminatorValue != null) {
            predicates.add(criteriaBuilder.equal(root.type(), entityTypeClass == null ? this.entityTypeClass : entityTypeClass));
        }
        if (valueParameters != null) {
            for (final Pair<String, Object> condition : valueParameters) {
                //ON JSON numbers are not using quotes.
                if (condition != null) {
                    if (condition.getSecond() instanceof Number) {
                        predicates.add(criteriaBuilder.like(root.get("value"),
                                String.format("%%\"%s\":%s%%", condition.getFirst(), condition.getSecond())));
                    } else {
                        predicates.add(criteriaBuilder.like(root.get("value"),
                                String.format("%%\"%s\":\"%s\"%%", condition.getFirst(), condition.getSecond())));
                    }
                }
            }
        }

        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
        query.select(root).where(predicates.toArray(new Predicate[0]));

        /* For Hibernate */
        System.out.println(entityManager.createQuery(query).unwrap(org.hibernate.query.Query.class).getQueryString());

        return entityManager.createQuery(query).getResultList();
    }


}

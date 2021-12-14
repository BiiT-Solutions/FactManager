package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.persistence.entities.Fact;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Repository
public class CustomFactRepositoryImpl<T extends Fact<?>> implements CustomFactRepository<T> {

    @PersistenceContext
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
            e.printStackTrace();
        }

    }

    protected T getData() {
        return data;
    }

    protected void setData(T data) {
        this.data = data;
    }

    @Override
    public Collection<T> findByValueParameters(Pair<String, Object>... valueParameters) {
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

        query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(query).getResultList();
    }
}

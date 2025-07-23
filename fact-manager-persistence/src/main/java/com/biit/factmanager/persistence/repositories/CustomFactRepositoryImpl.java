package com.biit.factmanager.persistence.repositories;

import com.biit.factmanager.logger.FactDatabaseLogger;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.CustomProperty;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.server.persistence.entities.CreatedElement;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CustomFactRepositoryImpl<T extends Fact<?>> implements CustomFactRepository<T> {

    @PersistenceContext
    @Qualifier(value = "factmanagerSystemFactory")
    private EntityManager entityManager;

    private Class<T> entityTypeClass;

    private T data;


    public CustomFactRepositoryImpl() {
        //Get class from T.
        final Field field;
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
    public List<T> findByCustomProperty(Map<String, String> customProperties, Pageable pageable) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> query = criteriaBuilder.createQuery(entityTypeClass);
        final Root<T> root = query.from(entityTypeClass);

        //SubQuery
        final Subquery<Fact> propertiesQuery = query.subquery(Fact.class);
        final Root<CustomProperty> subqueryRoot = propertiesQuery.from(CustomProperty.class);

        final List<Predicate> predicates = new ArrayList<>();
        if (customProperties != null) {
            for (final Map.Entry<String, String> entry : customProperties.entrySet()) {
                if (entry != null) {
                    predicates.add(criteriaBuilder.and(
                            criteriaBuilder.equal(subqueryRoot.get("key"), entry.getKey()),
                            criteriaBuilder.equal(subqueryRoot.get("value"), entry.getValue())));
                }
            }
        }

        propertiesQuery.select(subqueryRoot.get("fact")).where(criteriaBuilder.or(predicates.toArray(new Predicate[0])));

        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
        query.select(root).where(root.in(propertiesQuery));
        return entityManager.createQuery(query).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();
    }

    @Override
    public List<T> findByValueParameters(Pageable pageable, Pair<String, Object>... valueParameters) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> query = criteriaBuilder.createQuery(entityTypeClass);
        final Root<T> root = query.from(entityTypeClass);

        final List<Predicate> predicates = new ArrayList<>();
        for (final Pair<String, Object> condition : valueParameters) {
            //ON JSON numbers are not using quotes.
            if (condition.getSecond() instanceof Number) {
                predicates.add(criteriaBuilder.like(root.get("value"), String.format("%%\"%s\" : %s%%", condition.getFirst(), condition.getSecond())));
            } else {
                predicates.add(criteriaBuilder.like(root.get("value"), String.format("%%\"%s\" : \"%s\"%%", condition.getFirst(), condition.getSecond())));
            }
        }

        query.orderBy(criteriaBuilder.desc(root.get("createdAt")));
        query.select(root).where(predicates.toArray(new Predicate[predicates.size()]));
        return entityManager.createQuery(query).setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();
    }

    @Override
    public List<T> findBy(Class<T> entityTypeClass, String organization, String unit, Collection<String> createdBy, String application,
                          String tenant, String group, String element, String elementName, String session, String subject, String factType,
                          LocalDateTime startDate, LocalDateTime endDate, Boolean latestByUser, Boolean discriminatorValue,
                          Map<String, String> customProperties, Pageable pageable,
                          Pair<String, Object>... valueParameters) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<T> query = criteriaBuilder.createQuery(entityTypeClass == null ? this.entityTypeClass : entityTypeClass);
        final Root<T> root = query.from(entityTypeClass == null ? this.entityTypeClass : entityTypeClass);

        final List<Predicate> predicates = new ArrayList<>();
        if (organization != null) {
            predicates.add(criteriaBuilder.equal(root.get("organization"), organization));
        }
        if (unit != null) {
            predicates.add(criteriaBuilder.equal(root.get("unit"), unit));
        }
        if (createdBy != null && !createdBy.isEmpty()) {
            predicates.add(root.get("createdByHash").in(createdBy));
        }
        if (application != null) {
            predicates.add(criteriaBuilder.equal(root.get("application"), application));
        }
        if (tenant != null) {
            predicates.add(criteriaBuilder.equal(root.get("tenant"), tenant));
        }
        if (session != null) {
            predicates.add(criteriaBuilder.equal(root.get("session"), session));
        }
        if (subject != null) {
            predicates.add(criteriaBuilder.equal(root.get("subject"), subject));
        }
        if (factType != null) {
            predicates.add(criteriaBuilder.equal(root.get("factType"), factType));
        }
        if (group != null) {
            predicates.add(criteriaBuilder.equal(root.get("group"), group));
        }
        if (element != null) {
            predicates.add(criteriaBuilder.equal(root.get("element"), element));
        }
        if (elementName != null) {
            predicates.add(criteriaBuilder.equal(root.get("elementName"), elementName));
        }
        if (startDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
        }
        if (endDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
        }
        //To work, ensure that a specific bean with entityClass is defined. Check FactManagerServicesServer beans declaration.
        if (discriminatorValue != null) {
            predicates.add(criteriaBuilder.equal(root.type(), entityTypeClass == null ? this.entityTypeClass.getName() : entityTypeClass));
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

        //SubQuery
        final Subquery<Fact> propertiesQuery = query.subquery(Fact.class);
        final Root<CustomProperty> subqueryRoot = propertiesQuery.from(CustomProperty.class);

        final List<Predicate> subPredicates = new ArrayList<>();
        if (customProperties != null) {
            for (final Map.Entry<String, String> entry : customProperties.entrySet()) {
                if (entry != null) {
                    subPredicates.add(criteriaBuilder.and(
                            criteriaBuilder.equal(subqueryRoot.get("key"), entry.getKey()),
                            criteriaBuilder.equal(subqueryRoot.get("value"), entry.getValue())));
                }
            }
        }

        propertiesQuery.select(subqueryRoot.get("fact")).where(criteriaBuilder.and(subPredicates.toArray(new Predicate[0])));

        if (customProperties == null) {
            query.select(root).where(predicates.toArray(new Predicate[0]));
        } else {
            query.select(root).where(root.in(propertiesQuery), criteriaBuilder.and(predicates.toArray(new Predicate[0])));
        }

        final List<T> results = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset()).setMaxResults(pageable.getPageSize()).getResultList();

        //I have not found any way of filtering this through criteriaBuilder.
        if (latestByUser != null && latestByUser) {
            return filterByLatest(results);
        }

        return results;
    }

    private List<T> filterByLatest(List<T> elements) {
        FactDatabaseLogger.info(this.getClass(), "Filtering elements '{}'.", elements);
        final Map<Optional<String>, List<T>> elementsByCreatedBy = elements.stream().collect(Collectors.groupingBy(e -> Optional.ofNullable(e.getCreatedBy())));
        final List<T> latestByCreatedBy = new ArrayList<>();
        for (Map.Entry<Optional<String>, List<T>> entry : elementsByCreatedBy.entrySet()) {
            latestByCreatedBy.add(entry.getValue().stream().max(Comparator.comparing(CreatedElement::getCreatedAt)).orElse(null));
        }
        FactDatabaseLogger.info(this.getClass(), "Elements filtered '{}'.", elements);
        return latestByCreatedBy;
    }


}

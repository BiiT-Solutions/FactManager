package com.biit.factmanager.core.providers;

import com.biit.factmanager.core.providers.exceptions.InvalidParameterException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import com.biit.server.providers.CrudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Primary
public class FactProvider<T extends Fact<?>> extends CrudProvider<T, Long, FactRepository<T>> {
    private final FactRepository<T> factRepository;

    private Class<T> entityClass = null;


    public FactProvider(Class<T> entityClass,
                        FactRepository<T> factRepository) {
        super(factRepository);
        this.entityClass = entityClass;
        this.factRepository = factRepository;
    }

    @Autowired
    public FactProvider(FactRepository<T> factRepository) {
        super(factRepository);
        final Field field;
        try {
            field = this.getClass().getDeclaredField("data");
            entityClass = (Class<T>) field.getType();
        } catch (NoSuchFieldException e) {
            entityClass = null;
        }
        this.factRepository = factRepository;
    }

    @Override
    public Optional<T> get(Long factId) {
        return factRepository.findById(factId);
    }

    @Override
    public List<T> getAll() {
        return factRepository.findAll();
    }

    public List<T> getFiltered(String group, String element) {
        return factRepository.findByElementAndGroup(element, group);
    }

    public List<T> getByValueParameter(Object... pairParameterValues) {
        if (pairParameterValues.length % 2 == 1) {
            throw new InvalidParameterException(this.getClass(), "Parameters '" + Arrays.toString(pairParameterValues) + "' must be even.");
        }
        final Pair<String, Object>[] pairs = new Pair[pairParameterValues.length / 2];
        for (int i = 0; i < pairParameterValues.length; i += 2) {
            pairs[i] = Pair.of(pairParameterValues[i].toString(), pairParameterValues[i + 1]);
        }
        return factRepository.findByValueParameters(pairs);
    }

    public List<T> findByOrganization(String organization) {
        return factRepository.findByOrganization(organization);
    }

    public List<T> findBy(String organization, Collection<String> customers, String application, String tenant, String session, String subject,
                          String group, String element, String elementName, String factType, LocalDateTime startDate,
                          LocalDateTime endDate, Integer lastDays, Boolean latestByUser,
                          Boolean discriminatorValue, Map<String, String> customProperties,
                          Pair<String, Object>... valueParameters) {
        if (lastDays == null) {
            return findBy(organization, customers, application, tenant, session, subject, group, element, elementName, factType,
                    startDate, endDate, latestByUser, discriminatorValue, customProperties, valueParameters);
        } else {
            return findBy(organization, customers, application, tenant, session, subject, group, element, elementName, factType,
                    lastDays, latestByUser, discriminatorValue, customProperties, valueParameters);
        }
    }


    public List<T> findBy(String organization, Collection<String> customers, String application, String tenant, String session, String subject,
                          String group, String element, String elementName, String factType, Integer lastDays, Boolean latestByUser,
                          Boolean discriminatorValue, Map<String, String> customProperties, Pair<String, Object>... valueParameters) {
        final LocalDateTime endDate = LocalDateTime.now();
        if (lastDays != null) {
            final LocalDateTime startDate = LocalDateTime.now().minusDays(lastDays);
            return factRepository.findBy(entityClass, organization, customers, application, tenant, group, element, elementName, session,
                    subject, factType, startDate, endDate, latestByUser, discriminatorValue, customProperties, valueParameters);
        }
        return factRepository.findBy(entityClass, organization, customers, application, tenant, group, element, elementName, session, subject,
                factType, null, endDate, latestByUser, discriminatorValue, customProperties, valueParameters);
    }

    public List<T> findBy(String organization, Collection<String> customers, String application, String tenant, String session, String subject,
                          String group, String element, String elementName, String factType, LocalDateTime startDate, LocalDateTime endDate,
                          Boolean latestByUser, Boolean discriminatorValue, Map<String, String> customProperties, Pair<String, Object>... valueParameters) {
        return factRepository.findBy(entityClass, organization, customers, application, tenant, group, element, elementName, session, subject, factType,
                startDate, endDate, latestByUser, discriminatorValue, customProperties, valueParameters);
    }

    /**
     * Saves a given entity. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param fact must not be {@literal null}.
     * @return the saved entity; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    @Override
    public T save(T fact) {
        return factRepository.save(fact);
    }

    /**
     * Saves the given entity List. Use the returned instance for further operations as the save operation might have changed the
     * entity instance completely.
     *
     * @param facts must not be {@literal null}.
     * @return the List of saved entities; will never be {@literal null}.
     * @throws IllegalArgumentException in case the given {@literal entity} is {@literal null}.
     */
    public List<T> save(List<T> facts) {
        final List<T> savedFacts = new ArrayList<>();
        for (final T fact : facts) {
            final T savedFact = save(fact);
            savedFacts.add(savedFact);
            FactManagerLogger.debug(this.getClass().getName(), "Saved fact '{}'.", fact);
        }
        return savedFacts;
    }

    @Override
    public T update(T fact) {
        return factRepository.save(fact);
    }

    @Override
    public void delete(T fact) {
        factRepository.delete(fact);
    }

    /**
     * Returns the number of entities available.
     *
     * @return the number of entities.
     */
    @Override
    public long count() {
        return factRepository.count();
    }

    public List<T> findBySession(String session) {
        return factRepository.findBySession(session);
    }

    List<T> findByCreatedBy(String createdBy) {
        return factRepository.findByCreatedBy(createdBy);
    }

}

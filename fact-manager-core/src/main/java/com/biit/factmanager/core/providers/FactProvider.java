package com.biit.factmanager.core.providers;

/*-
 * #%L
 * FactManager (core)
 * %%
 * Copyright (C) 2020 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

import com.biit.factmanager.core.providers.exceptions.InvalidParameterException;
import com.biit.factmanager.logger.FactManagerLogger;
import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.repositories.FactRepository;
import com.biit.server.providers.CrudProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
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

import static com.biit.database.encryption.KeyProperty.getEncryptionKey;

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

    public List<T> getByValueParameter(int page, int size, Object... pairParameterValues) {
        if (pairParameterValues == null) {
            return new ArrayList<>();
        }
        if (pairParameterValues.length % 2 == 1) {
            throw new InvalidParameterException(this.getClass(), "Parameters '" + Arrays.toString(pairParameterValues) + "' must be even.");
        }
        if (pairParameterValues.length > 0 && getEncryptionKey() != null && !getEncryptionKey().isBlank()) {
            throw new InvalidParameterException(this.getClass(), "Search by parameters not allowed if database encryption is set!");
        }
        final Pair<String, Object>[] pairs = new Pair[pairParameterValues.length / 2];
        for (int i = 0; i < pairParameterValues.length; i += 2) {
            pairs[i] = Pair.of(pairParameterValues[i].toString(), pairParameterValues[i + 1]);
        }
        return factRepository.findByValueParameters(PageRequest.of(page, size), pairs);
    }

    public List<T> findByOrganization(String organization) {
        return factRepository.findByOrganization(organization);
    }

    public List<T> findBy(String organization, String unit, Collection<String> customers, String application, String tenant, String session, String subject,
                          String group, String element, String elementName, String factType, LocalDateTime startDate,
                          LocalDateTime endDate, Integer lastDays, Boolean latestByUser,
                          Boolean discriminatorValue, Map<String, String> customProperties,
                          int page, int size, Pair<String, Object>... valueParameters) {
        if (valueParameters != null && valueParameters.length > 0 && getEncryptionKey() != null && !getEncryptionKey().isBlank()) {
            throw new InvalidParameterException(this.getClass(), "Search by parameters not allowed if database encryption is set!");
        }
        if (lastDays == null) {
            return findBy(organization, unit, customers, application, tenant, session, subject, group, element, elementName, factType,
                    startDate, endDate, latestByUser, discriminatorValue, customProperties, page, size, valueParameters);
        } else {
            return findBy(organization, unit, customers, application, tenant, session, subject, group, element, elementName, factType,
                    lastDays, latestByUser, discriminatorValue, customProperties, page, size, valueParameters);
        }
    }


    public List<T> findBy(String organization, String unit, Collection<String> customers, String application, String tenant, String session, String subject,
                          String group, String element, String elementName, String factType, Integer lastDays, Boolean latestByUser,
                          Boolean discriminatorValue, Map<String, String> customProperties, int page, int size,
                          Pair<String, Object>... valueParameters) {
        if (valueParameters != null && valueParameters.length > 0 && getEncryptionKey() != null && !getEncryptionKey().isBlank()) {
            throw new InvalidParameterException(this.getClass(), "Search by parameters not allowed if database encryption is set!");
        }
        final LocalDateTime endDate = LocalDateTime.now();
        if (lastDays != null) {
            final LocalDateTime startDate = LocalDateTime.now().minusDays(lastDays);
            return factRepository.findBy(entityClass, organization, unit, customers, application, tenant, group, element, elementName, session,
                    subject, factType, startDate, endDate, latestByUser, discriminatorValue, customProperties, PageRequest.of(page, size), valueParameters);
        }
        return factRepository.findBy(entityClass, organization, unit, customers, application, tenant, group, element, elementName, session, subject,
                factType, null, endDate, latestByUser, discriminatorValue, customProperties, PageRequest.of(page, size), valueParameters);
    }

    public List<T> findBy(String organization, String unit, Collection<String> customers, String application, String tenant, String session, String subject,
                          String group, String element, String elementName, String factType, LocalDateTime startDate, LocalDateTime endDate,
                          Boolean latestByUser, Boolean discriminatorValue, Map<String, String> customProperties, int page, int size,
                          Pair<String, Object>... valueParameters) {
        if (valueParameters != null && valueParameters.length > 0 && getEncryptionKey() != null && !getEncryptionKey().isBlank()) {
            throw new InvalidParameterException(this.getClass(), "Search by parameters not allowed if database encryption is set!");
        }
        return factRepository.findBy(entityClass, organization, unit, customers, application, tenant, group, element, elementName, session, subject, factType,
                startDate, endDate, latestByUser, discriminatorValue, customProperties, PageRequest.of(page, size), valueParameters);
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

    public List<T> findByCreatedBy(String createdBy) {
        final List<T> results;
        if (getEncryptionKey() != null && !getEncryptionKey().isBlank()) {
            results = factRepository.findByCreatedByHash(createdBy);
        } else {
            results = factRepository.findByCreatedBy(createdBy);
        }
        return results;
    }

}

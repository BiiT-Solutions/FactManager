package com.biit.factmanager.client.provider;

/*-
 * #%L
 * FactManager (Rest Client)
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

import com.biit.factmanager.client.FactClient;
import com.biit.factmanager.client.SearchParameters;
import com.biit.factmanager.dto.FactDTO;
import com.biit.rest.client.Header;
import com.biit.rest.exceptions.UnprocessableEntityException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ClientFactProvider {

    private final FactClient factClient;

    private final Factory<FactDTO> factory;

    public interface Factory<FactDTO> {
        FactDTO create();
    }

    public ClientFactProvider(FactClient factClient) {
        this.factClient = factClient;
        this.factory = FactDTO::new;
    }

    FactDTO createInstance() {
        return factory.create();
    }

    public FactDTO add(FactDTO fact) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(Collections.singletonList(convert(fact)), null);
        if (!rawFacts.isEmpty()) {
            return convertDTO(rawFacts.get(0));
        }
        return null;
    }

    public List<FactDTO> add(List<FactDTO> facts) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(convert(facts), null);
        return convertDTO(rawFacts);
    }

    public FactDTO add(FactDTO fact, List<Header> headers) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.post(Collections.singletonList(convert(fact)), headers);
        if (!rawFacts.isEmpty()) {
            return convertDTO(rawFacts.get(0));
        }
        return null;
    }

    public List<FactDTO> get(Map<SearchParameters, Object> filter) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.get(filter, null);
        return convertDTO(rawFacts);
    }

    public List<FactDTO> get(Map<SearchParameters, Object> filter, List<Header> headers) throws UnprocessableEntityException {
        final List<FactDTO> rawFacts = factClient.get(filter, headers);
        return convertDTO(rawFacts);
    }

    protected FactDTO convertDTO(FactDTO factDTO) {
        final FactDTO fact = createInstance();
        BeanUtils.copyProperties(factDTO, fact);
        fact.setValue(factDTO.getValue());
        return fact;
    }

    protected List<FactDTO> convertDTO(Collection<FactDTO> factsDTO) {
        final List<FactDTO> convertedFacts = new ArrayList<>();
        factsDTO.forEach(factDTO -> convertedFacts.add(convertDTO(factDTO)));
        return convertedFacts;
    }

    protected FactDTO convert(FactDTO fact) {
        final FactDTO factDTO = new FactDTO();
        BeanUtils.copyProperties(fact, factDTO);
        factDTO.setValue(fact.getValue());
        return factDTO;
    }

    protected List<FactDTO> convert(Collection<FactDTO> facts) {
        final List<FactDTO> convertedFacts = new ArrayList<>();
        facts.forEach(fact -> convertedFacts.add(convert(fact)));
        return convertedFacts;
    }
}

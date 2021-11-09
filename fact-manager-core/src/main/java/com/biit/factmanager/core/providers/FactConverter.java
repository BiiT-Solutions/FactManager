package com.biit.factmanager.core.providers;

import com.biit.factmanager.persistence.entities.Fact;
import com.biit.factmanager.persistence.entities.FormRunnerValue;
import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FactConverter extends Fact {

    public FactConverter factConverter () {
        return new FactConverter();
    }
    public Fact convert(FormrunnerFact formrunnerFact) {
        final Fact fact = null;
        fact.setTenantId(formrunnerFact.getPatientId());
        fact.setCategory(formrunnerFact.getCategory());
        //decided to set companyId as elementId it's arbitrary and needs to be checked
        fact.setElementId(String.valueOf(formrunnerFact.getCompanyId()));
        fact.setValue(createValue(formrunnerFact).toString());
        return fact;
    }

    private FormRunnerValue createValue (FormrunnerFact formrunnerFact) {
        final FormRunnerValue formRunnerValue = new FormRunnerValue();
        final ObjectMapper mapper = new ObjectMapper();
        final ObjectNode value = mapper.createObjectNode();
        value.put("organizationId", formrunnerFact.getOrganizationId());
        value.put("professionalId", formrunnerFact.getProfessionalId());
        value.put("question", formrunnerFact.getQuestion());
        value.put("answer", formrunnerFact.getAnswer());
        value.put("score", formrunnerFact.getScore());
        value.put("xpath", formrunnerFact.getXpath());
        value.put("examinationName", formrunnerFact.getExaminationName());
        value.put("examinationVersion", formrunnerFact.getExaminationVersion());
        value.put("createdAt", formrunnerFact.getCreatedAt().toString());
        formRunnerValue.setValue(value.toString());
        return formRunnerValue;
    }
}

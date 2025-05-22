package com.biit.factmanager.core.controllers;

import com.biit.drools.form.DroolsSubmittedForm;
import com.biit.drools.form.xls.FormsAsXls;
import com.biit.drools.form.xls.exceptions.InvalidXlsElementException;
import com.biit.factmanager.core.providers.exceptions.InvalidFactException;
import com.biit.factmanager.dto.FactDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class DroolsFormXmlController {

    public byte[] convert(Collection<FactDTO> facts) throws InvalidXlsElementException, InvalidFactException {
        final List<DroolsSubmittedForm> formResults = new ArrayList<>();
        for (FactDTO factDTO : facts) {
            try {
                formResults.add(DroolsSubmittedForm.getFromJson(factDTO.getValue()));
            } catch (JsonProcessingException e) {
                throw new InvalidFactException(this.getClass(), "Fact with id '" + factDTO.getId() + "' does not contain a Form Result.", e);
            }
        }


        final FormsAsXls xlsDocument = new FormsAsXls(formResults, null);
        return xlsDocument.generate();
    }
}

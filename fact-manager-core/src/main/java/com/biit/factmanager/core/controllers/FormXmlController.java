package com.biit.factmanager.core.controllers;

import com.biit.factmanager.core.providers.exceptions.InvalidFactException;
import com.biit.factmanager.dto.FactDTO;
import com.biit.form.result.FormResult;
import com.biit.form.result.xls.FormsAsXls;
import com.biit.form.result.xls.exceptions.InvalidXlsElementException;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Controller
public class FormXmlController {

    public byte[] convert(Collection<FactDTO> facts) throws InvalidXlsElementException, InvalidFactException {
        final List<FormResult> formResults = new ArrayList<>();
        for (FactDTO factDTO : facts) {
            try {
                formResults.add(FormResult.fromJson(factDTO.getValue()));
            } catch (JsonProcessingException e) {
                throw new InvalidFactException(this.getClass(), "Fact with id '" + factDTO.getId() + "' does not contain a Form Result.", e);
            }
        }


        final FormsAsXls xlsDocument = new FormsAsXls(formResults, null);
        return xlsDocument.generate();
    }
}

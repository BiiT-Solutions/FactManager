package com.biit.factmanager.core.controllers;

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

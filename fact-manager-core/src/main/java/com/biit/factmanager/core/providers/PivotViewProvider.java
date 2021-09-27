package com.biit.factmanager.core.providers;

import com.biit.factmanager.persistence.entities.FormrunnerFact;
import com.biit.factmanager.persistence.repositories.FormrunnerFactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.crypto.Data;
import java.text.DateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PivotViewProvider {
    private final FormrunnerFactRepository formrunnerFactRepository;

    @Autowired
    public PivotViewProvider(FormrunnerFactRepository formrunnerFactRepository) {
        this.formrunnerFactRepository = formrunnerFactRepository;
    }

    public Collection<FormrunnerFact> getAll() {
        return StreamSupport.stream(formrunnerFactRepository.findAll().spliterator(), false).collect(Collectors.toSet());
    }

    public Collection<FormrunnerFact> getBetweenDates(LocalDateTime startDate , LocalDateTime endDate) {
        return StreamSupport.stream(formrunnerFactRepository.findByCreatedAtGreaterThanEqualAndCreatedAtLessThanEqual(startDate,endDate).spliterator(), false).collect(Collectors.toSet());
    }

    public Collection<FormrunnerFact> getAfterDate(LocalDateTime date) {
        return StreamSupport.stream(formrunnerFactRepository.findByCreatedAtGreaterThan(date).spliterator(), false).collect(Collectors.toSet());
    }

    public Collection<FormrunnerFact> getBeforeDate(LocalDateTime date) {
        return StreamSupport.stream(formrunnerFactRepository.findByCreatedAtLessThan(date).spliterator(), false).collect(Collectors.toSet());
    }

    public Collection<FormrunnerFact> getOneYearOld (LocalDateTime endDate){
        LocalDateTime startDate = endDate.minusYears(1);
        return getBetweenDates(startDate,endDate);
    }

    public Collection<FormrunnerFact> getDayFacts(LocalDateTime date){
        LocalDateTime startHourDay = date.minusHours(date.getHour());
        LocalDateTime endHourDay = date.plusHours(23-date.getHour());
        return getBetweenDates(startHourDay,endHourDay);
    }

    public Collection<FormrunnerFact> getOneMonthOld(LocalDateTime date) {
        LocalDateTime startMonth = LocalDateTime.parse(LocalDate.of(date.getYear(), date.getMonth(), 1).toString());
        LocalDateTime endMonth = LocalDateTime.parse(LocalDate.of(date.getYear(), date.getMonth(), date.getMonth().minLength()).toString()); //Años bisiestos¿?
        return getBetweenDates(startMonth,endMonth);
    }
}

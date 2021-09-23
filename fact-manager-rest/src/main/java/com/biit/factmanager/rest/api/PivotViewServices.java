package com.biit.factmanager.rest.api;

import com.biit.factmanager.core.providers.FormrunnerFactProvider;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


public class PivotViewServices {

    private final FormrunnerFactProvider formrunnerFactProvider;

    @Autowired
    public PivotViewServices(FormrunnerFactProvider formrunnerFactProvider) {
        this.formrunnerFactProvider = formrunnerFactProvider;
    }

}

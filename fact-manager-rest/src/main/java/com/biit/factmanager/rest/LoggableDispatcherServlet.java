package com.biit.factmanager.rest;

import com.biit.usermanager.logger.UserManagerLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.util.ContentCachingRequestWrapper;


public class LoggableDispatcherServlet extends DispatcherServlet {
    private static final long serialVersionUID = -8650983209144626130L;

    @Override
    protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!(request instanceof ContentCachingRequestWrapper)) {
            request = new ContentCachingRequestWrapper(request);
        }
        try {
            super.doDispatch(request, response);
        } catch (Exception e) {
            UserManagerLogger.errorMessage(this.getClass(), e);
        }
    }
}

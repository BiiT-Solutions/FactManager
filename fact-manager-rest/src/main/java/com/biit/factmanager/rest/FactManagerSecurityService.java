package com.biit.factmanager.rest;

import com.biit.server.rest.SecurityService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Primary
@Service("securityService")
public class FactManagerSecurityService extends SecurityService {

    private static final String VIEWER = "FactManager_VIEWER";
    private static final String ADMIN = "FactManager_ADMIN";
    private static final String EDITOR = "FactManager_EDITOR";
    private static final String ORGANIZATION_ADMIN = "FactManager_ORGANIZATION_ADMIN";

    private String viewerPrivilege = null;
    private String adminPrivilege = null;
    private String editorPrivilege = null;
    private String organizationAdminPrivilege = null;

    @Override
    public String getViewerPrivilege() {
        if (viewerPrivilege == null) {
            viewerPrivilege = VIEWER.toUpperCase();
        }
        return viewerPrivilege;
    }

    @Override
    public String getAdminPrivilege() {
        if (adminPrivilege == null) {
            adminPrivilege = ADMIN.toUpperCase();
        }
        return adminPrivilege;
    }

    @Override
    public String getEditorPrivilege() {
        if (editorPrivilege == null) {
            editorPrivilege = EDITOR.toUpperCase();
        }
        return editorPrivilege;
    }

    @Override
    public String getOrganizationAdminPrivilege() {
        if (organizationAdminPrivilege == null) {
            organizationAdminPrivilege = ORGANIZATION_ADMIN.toUpperCase();
        }
        return organizationAdminPrivilege;
    }
}

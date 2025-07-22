package com.biit.factmanager.rest;

import com.biit.server.controllers.models.CreatedElementDTO;
import com.biit.server.persistence.entities.CreatedElement;
import com.biit.server.rest.SecurityService;
import com.biit.server.security.IUserOrganizationProvider;
import com.biit.server.security.model.IUserOrganization;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

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

    private final List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProviders;

    public FactManagerSecurityService(List<IUserOrganizationProvider<? extends IUserOrganization>> userOrganizationProviders) {
        this.userOrganizationProviders = userOrganizationProviders;
    }

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

    public void canBeDoneByOrganizationAdmin(CreatedElementDTO dto, Authentication authentication) {
        if (!userOrganizationProviders.isEmpty()) {
            super.canBeDoneByOrganizationAdmin(dto, authentication, userOrganizationProviders.get(0));
        }
    }


    public void canBeDoneByOrganizationAdmin(CreatedElement entity, Authentication authentication) {
        if (!userOrganizationProviders.isEmpty()) {
            super.canBeDoneByOrganizationAdmin(entity, authentication, userOrganizationProviders.get(0));
        }
    }


    public void canBeDoneByDifferentUsers(Collection<String> userNames, Authentication authentication) {
        if (!userOrganizationProviders.isEmpty()) {
            super.canBeDoneByDifferentUsers(userNames, authentication, userOrganizationProviders.get(0));
        }
    }


    public void canBeDoneByDifferentUsers(String userName, Authentication authentication) {
        if (!userOrganizationProviders.isEmpty()) {
            super.canBeDoneByDifferentUsers(userName, authentication, userOrganizationProviders.get(0));
        }
    }


    public void checkHasOrganizationAdminAccess(String organizationName, Authentication authentication,
                                                String... otherAuthoritiesAllowed) {
        if (!userOrganizationProviders.isEmpty()) {
            super.checkHasOrganizationAdminAccess(organizationName, authentication, userOrganizationProviders.get(0), otherAuthoritiesAllowed);
        }
    }


    public IUserOrganization getRequiredUserOrganization(Authentication authentication,
                                                         String... otherAuthoritiesAllowed) {
        if (!userOrganizationProviders.isEmpty()) {
            return super.getRequiredUserOrganization(authentication, userOrganizationProviders.get(0), otherAuthoritiesAllowed);
        }
        return null;
    }


    public void checkCreatedOn(CreatedElementDTO elementDTO, Authentication authentication,
                               String... otherAuthoritiesAllowed) {
        if (!userOrganizationProviders.isEmpty()) {
            super.checkCreatedOn(elementDTO, authentication, userOrganizationProviders.get(0), otherAuthoritiesAllowed);
        }
    }
}

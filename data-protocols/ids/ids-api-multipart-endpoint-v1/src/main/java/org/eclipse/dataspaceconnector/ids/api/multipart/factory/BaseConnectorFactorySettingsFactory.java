package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.eclipse.dataspaceconnector.ids.core.configuration.IllegalSettingException;
import org.eclipse.dataspaceconnector.ids.core.configuration.SettingResolver;
import org.eclipse.dataspaceconnector.ids.spi.types.SecurityProfile;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class BaseConnectorFactorySettingsFactory {
    private final SettingResolver settingResolver;

    public BaseConnectorFactorySettingsFactory(SettingResolver settingResolver) {
        this.settingResolver = settingResolver;
    }

    public BaseConnectorFactorySettingsFactoryResult createBaseConnectorFactorySettings() {

        List<String> errors = new ArrayList<>();

        String title;
        String description;
        URI id = null;
        URI maintainer = null;
        URI curator = null;
        URI connectorEndpoint = null;
        SecurityProfile securityProfile = null;

        try {
            id = settingResolver.resolveId();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        title = settingResolver.resolveTitle();
        description = settingResolver.resolveDescription();

        try {
            maintainer = settingResolver.resolveMaintainer();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        try {
            curator = settingResolver.resolveCurator();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        try {
            connectorEndpoint = settingResolver.resolveEndpoint();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        try {
            securityProfile = settingResolver.resolveSecurityProfile();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        var settings = BaseConnectorFactorySettings.Builder.newInstance()
                .id(id)
                .title(title)
                .description(description)
                .maintainer(maintainer)
                .curator(curator)
                .connectorEndpoint(connectorEndpoint)
                .securityProfile(securityProfile)
                .build();

        return new BaseConnectorFactorySettingsFactoryResult(settings, errors);
    }
}

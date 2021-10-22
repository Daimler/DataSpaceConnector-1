package org.eclipse.dataspaceconnector.ids.api.multipart.handler.description;

import org.eclipse.dataspaceconnector.ids.core.configuration.IllegalSettingException;
import org.eclipse.dataspaceconnector.ids.core.configuration.SettingResolver;

import java.net.URI;
import java.util.ArrayList;

public class ConnectorDescriptionRequestHandlerSettingsFactory {
    private final SettingResolver settingResolver;

    public ConnectorDescriptionRequestHandlerSettingsFactory(SettingResolver settingResolver) {
        this.settingResolver = settingResolver;
    }

    public ConnectorDescriptionRequestHandlerSettingsFactoryResult createConnectorDescriptionRequestHandlerSettings() {

        var errors = new ArrayList<String>();

        URI id = null;

        try {
            id = settingResolver.resolveId();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        var settings = new ConnectorDescriptionRequestHandlerSettings(id);

        return new ConnectorDescriptionRequestHandlerSettingsFactoryResult(settings, errors);
    }
}

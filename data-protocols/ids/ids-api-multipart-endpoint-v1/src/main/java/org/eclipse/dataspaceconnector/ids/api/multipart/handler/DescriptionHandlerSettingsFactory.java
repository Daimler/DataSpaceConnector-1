package org.eclipse.dataspaceconnector.ids.api.multipart.handler;

import org.eclipse.dataspaceconnector.ids.core.configuration.IllegalSettingException;
import org.eclipse.dataspaceconnector.ids.core.configuration.SettingResolver;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DescriptionHandlerSettingsFactory {

    private final SettingResolver settingResolver;

    public DescriptionHandlerSettingsFactory(SettingResolver settingResolver) {
        this.settingResolver = settingResolver;
    }

    public DescriptionHandlerSettingsFactoryResult createDescriptionHandlerSettings() {

        List<String> errors = new ArrayList<>();

        URI id = null;

        try {
            id = settingResolver.resolveId();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        var settings = DescriptionHandlerSettings.Builder.newInstance().id(id).build();

        return new DescriptionHandlerSettingsFactoryResult(settings, errors);
    }
}

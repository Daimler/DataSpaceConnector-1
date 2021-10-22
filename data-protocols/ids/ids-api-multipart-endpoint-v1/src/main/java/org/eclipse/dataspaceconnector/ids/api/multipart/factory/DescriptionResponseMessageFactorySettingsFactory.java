package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.eclipse.dataspaceconnector.ids.core.configuration.IllegalSettingException;
import org.eclipse.dataspaceconnector.ids.core.configuration.SettingResolver;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class DescriptionResponseMessageFactorySettingsFactory {
    private final SettingResolver settingResolver;

    public DescriptionResponseMessageFactorySettingsFactory(SettingResolver settingResolver) {
        this.settingResolver = settingResolver;
    }

    public DescriptionResponseMessageFactorySettingsFactoryResult createDescriptionResponseMessageFactorySettings() {

        List<String> errors = new ArrayList<>();

        URI id = null;

        try {
            id = settingResolver.resolveId();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        var settings = DescriptionResponseMessageFactorySettings.Builder.newInstance().id(id).build();

        return new DescriptionResponseMessageFactorySettingsFactoryResult(settings, errors);
    }
}

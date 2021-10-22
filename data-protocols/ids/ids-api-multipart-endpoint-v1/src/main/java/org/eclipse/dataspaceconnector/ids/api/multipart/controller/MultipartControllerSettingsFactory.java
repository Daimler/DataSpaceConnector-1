package org.eclipse.dataspaceconnector.ids.api.multipart.controller;

import org.eclipse.dataspaceconnector.ids.core.configuration.IllegalSettingException;
import org.eclipse.dataspaceconnector.ids.core.configuration.SettingResolver;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MultipartControllerSettingsFactory {

    private final SettingResolver settingResolver;

    public MultipartControllerSettingsFactory(SettingResolver settingResolver) {
        this.settingResolver = settingResolver;
    }

    public MultipartControllerSettingsFactoryResult createRejectionMessageFactorySettings() {

        List<String> errors = new ArrayList<>();

        URI id = null;

        try {
            id = settingResolver.resolveId();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        var settings = MultipartControllerSettings.Builder.newInstance().id(id).build();

        return new MultipartControllerSettingsFactoryResult(settings, errors);
    }
}

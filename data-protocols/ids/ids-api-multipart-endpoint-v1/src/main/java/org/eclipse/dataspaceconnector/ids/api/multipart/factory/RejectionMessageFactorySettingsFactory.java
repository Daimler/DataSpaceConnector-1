package org.eclipse.dataspaceconnector.ids.api.multipart.factory;

import org.eclipse.dataspaceconnector.ids.core.configuration.IllegalSettingException;
import org.eclipse.dataspaceconnector.ids.core.configuration.SettingResolver;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class RejectionMessageFactorySettingsFactory {

    private final SettingResolver settingResolver;

    public RejectionMessageFactorySettingsFactory(SettingResolver settingResolver) {
        this.settingResolver = settingResolver;
    }

    public RejectionMessageFactorySettingsFactoryResult createRejectionMessageFactorySettings() {

        List<String> errors = new ArrayList<>();

        URI id = null;

        try {
            id = settingResolver.resolveId();
        } catch (IllegalSettingException e) {
            errors.add(e.getMessage());
        }

        var settings = new RejectionMessageFactorySettings(id);

        return new RejectionMessageFactorySettingsFactoryResult(settings, errors);
    }
}

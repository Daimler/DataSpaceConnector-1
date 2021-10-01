package org.eclipse.dataspaceconnector.ids.core.configuration;

import org.eclipse.dataspaceconnector.spi.EdcSetting;

public final class IdsSettings {
    @EdcSetting
    public static final String EDC_IDS_ID = "edc.ids.id";

    @EdcSetting
    public static final String EDC_IDS_TITLE = "edc.ids.title";

    @EdcSetting
    public static final String EDC_IDS_DESCRIPTION = "edc.ids.description";

    @EdcSetting
    public static final String EDC_IDS_MAINTAINER = "edc.ids.maintainer";

    @EdcSetting
    public static final String EDC_IDS_CURATOR = "edc.ids.curator";

    @EdcSetting
    public static final String EDC_IDS_SECURITY_PROFILE = "edc.ids.security.profile";

    @EdcSetting
    public static final String EDC_IDS_ENDPOINT = "edc.ids.endpoint";
}

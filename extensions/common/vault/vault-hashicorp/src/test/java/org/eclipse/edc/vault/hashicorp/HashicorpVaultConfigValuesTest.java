/*
 *  Copyright (c) 2024 Mercedes-Benz Tech Innovation GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Mercedes-Benz Tech Innovation GmbH - Implement automatic Hashicorp Vault token renewal
 *
 */

package org.eclipse.edc.vault.hashicorp;

import okhttp3.HttpUrl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.eclipse.edc.vault.hashicorp.HashicorpVaultConfig.VAULT_TOKEN_RENEW_BUFFER_DEFAULT;
import static org.eclipse.edc.vault.hashicorp.HashicorpVaultConfig.VAULT_TOKEN_TTL_DEFAULT;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class HashicorpVaultConfigValuesTest {

    private static final String TOKEN = "token";
    private static final String URL = "https://test.com/vault";
    private static final HttpUrl HTTP_URL = HttpUrl.parse(URL);
    private static final String HEALTH_CHECK_PATH = "/healthcheck/path";
    private static final String SECRET_PATH = "/secret/path";

    @Test
    void createConfigValues_withDefaultValues_shouldSucceed() {
        var configValues = assertDoesNotThrow(() -> createConfigValues(
                URL,
                TOKEN,
                HEALTH_CHECK_PATH, VAULT_TOKEN_TTL_DEFAULT,
                VAULT_TOKEN_RENEW_BUFFER_DEFAULT));
        assertThat(configValues.url()).isEqualTo(HTTP_URL);
        assertThat(configValues.healthCheckEnabled()).isEqualTo(true);
        assertThat(configValues.healthCheckPath()).isEqualTo(HEALTH_CHECK_PATH);
        assertThat(configValues.healthStandbyOk()).isEqualTo(true);
        assertThat(configValues.token()).isEqualTo(TOKEN);
        assertThat(configValues.ttl()).isEqualTo(VAULT_TOKEN_TTL_DEFAULT);
        assertThat(configValues.renewBuffer()).isEqualTo(VAULT_TOKEN_RENEW_BUFFER_DEFAULT);
        assertThat(configValues.secretPath()).isEqualTo(SECRET_PATH);
    }

    @Test
    void createConfigValues_withVaultUrlNull_shouldThrowException() {
        var throwable = assertThrows(Exception.class, () -> createConfigValues(
                null,
                TOKEN,
                HEALTH_CHECK_PATH, VAULT_TOKEN_TTL_DEFAULT,
                VAULT_TOKEN_RENEW_BUFFER_DEFAULT));
        assertThat(throwable.getMessage()).isEqualTo("Vault url must not be null");
    }

    @Test
    void createConfigValues_withVaultUrlInvalid_shouldThrowException() {
        var throwable = assertThrows(Exception.class, () -> createConfigValues(
                "this is not valid",
                TOKEN,
                HEALTH_CHECK_PATH,
                VAULT_TOKEN_TTL_DEFAULT,
                VAULT_TOKEN_RENEW_BUFFER_DEFAULT));
        assertThat(throwable.getMessage()).isEqualTo("Vault url must be valid");
    }

    @Test
    void createConfigValues_withHealthCheckPathNull_shouldThrowException() {
        var throwable = assertThrows(Exception.class, () -> createConfigValues(
                URL,
                TOKEN,
                null,
                VAULT_TOKEN_TTL_DEFAULT,
                VAULT_TOKEN_RENEW_BUFFER_DEFAULT));
        assertThat(throwable.getMessage()).isEqualTo("Vault health check path must not be null");
    }

    @Test
    void createConfigValues_withVaultTokenNull_shouldThrowException() {
        var throwable = assertThrows(Exception.class, () -> createConfigValues(
                URL,
                null,
                HEALTH_CHECK_PATH, 
                VAULT_TOKEN_TTL_DEFAULT,
                VAULT_TOKEN_RENEW_BUFFER_DEFAULT));
        assertThat(throwable.getMessage()).isEqualTo("Vault token must not be null");
    }

    @Test
    void createConfigValues_withVaultTokenTtlLessThan5_shouldThrowException() {
        var throwable = assertThrows(Exception.class, () -> createConfigValues(
                URL,
                TOKEN,
                HEALTH_CHECK_PATH,
                4,
                VAULT_TOKEN_RENEW_BUFFER_DEFAULT));
        assertThat(throwable.getMessage()).isEqualTo("Vault token ttl minimum value is 5");
    }

    @ParameterizedTest
    @ValueSource(longs = {VAULT_TOKEN_TTL_DEFAULT, VAULT_TOKEN_TTL_DEFAULT + 1})
    void createConfigValues_withVaultTokenRenewBufferEqualOrGreaterThanTtl_shouldThrowException(long value) {
        var throwable = assertThrows(Exception.class, () -> createConfigValues(
                URL,
                TOKEN,
                HEALTH_CHECK_PATH, 
                VAULT_TOKEN_TTL_DEFAULT,
                value));
        assertThat(throwable.getMessage()).isEqualTo("Vault token renew buffer value must be less than ttl value");
    }

    private HashicorpVaultConfigValues createConfigValues(String url,
                                                          String token,
                                                          String healthCheckPath,
                                                          long ttl,
                                                          long renewBuffer) {
        return HashicorpVaultConfigValues.Builder.newInstance()
                .url(url)
                .healthCheckEnabled(true)
                .healthCheckPath(healthCheckPath)
                .healthStandbyOk(true)
                .token(token)
                .ttl(ttl)
                .renewBuffer(renewBuffer)
                .secretPath(SECRET_PATH)
                .build();
    }
}
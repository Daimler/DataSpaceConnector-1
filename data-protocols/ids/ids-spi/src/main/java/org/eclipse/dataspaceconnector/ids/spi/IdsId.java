/*
 *  Copyright (c) 2021 Daimler TSS GmbH
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Daimler TSS GmbH - Initial API and Implementation
 *
 */

package org.eclipse.dataspaceconnector.ids.spi;

import org.eclipse.dataspaceconnector.ids.spi.transform.IdsTypeTransformer;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.Objects;

/**
 * ID / URI generator for IDS resources.
 */
@Deprecated // This functionality will be moved to a transformer class
public class IdsId {
    private final IdsType type;
    private final String value;

    private IdsId(@NotNull IdsType type, @NotNull String value) {
        this.type = Objects.requireNonNull(type);
        this.value = Objects.requireNonNull(value);
    }

    public static IdsId message(String value) {
        return new IdsId(IdsType.MESSAGE, value);
    }

    public static IdsId participant(String value) {
        return new IdsId(IdsType.PARTICIPANT, value);
    }

    public static IdsId connector(String value) {
        return new IdsId(IdsType.CONNECTOR, value);
    }

    public static IdsId representation(String value) {
        return new IdsId(IdsType.REPRESENTATION, value);
    }

    public static IdsId resource(String value) {
        return new IdsId(IdsType.RESOURCE, value);
    }

    public static IdsId catalog(String value) {
        return new IdsId(IdsType.CATALOG, value);
    }

    public static IdsId artifact(String value) {
        return new IdsId(IdsType.ARTIFACT, value);
    }

    public IdsType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public static class Builder {
        private IdsType type;
        private String value;

        public static Builder newInstance() {
            return new Builder();
        }
        public Builder type(IdsType idsType) {
            this.type = idsType;
            return this;
        }
        public Builder value(String value) {
            this.value = value;
            return this;
        }
        public IdsId build() {
            return new IdsId(type, value);
        }
    }
}

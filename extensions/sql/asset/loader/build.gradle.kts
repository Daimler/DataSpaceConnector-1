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
 *       Daimler TSS GmbH - initial API and implementation
 *
 */

plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
}

dependencies {
    api(project(":spi"))
    api(project(":extensions:transaction:transaction-datasource-spi"))
    api(project(":extensions:dataloading"))
    implementation(project(":extensions:sql:common"))
}

publishing {
    publications {
        create<MavenPublication>("sql-asset-loader") {
            artifactId = "sql-asset-loader"
            from(components["java"])
        }
    }
}

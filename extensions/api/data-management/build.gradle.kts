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


val rsApi: String by project

plugins {
    `java-library`
}

dependencies {
    api(project(":spi"))

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")

    implementation(project(":common:libraries:clients:postgresql"))
    implementation(project(":common:libraries:clients:postgresql-repository"))

    testImplementation(testFixtures(project(":launchers:junit")))
    testImplementation("io.rest-assured:rest-assured:4.4.0")

    testImplementation(project(":extensions:in-memory:negotiation-store-memory"))

    implementation(project(":common:util"))
}

publishing {
    publications {
        create<MavenPublication>("data-management-api") {
            artifactId = "data-management-api"
            from(components["java"])
        }
    }
}

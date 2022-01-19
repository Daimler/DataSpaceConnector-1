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
val testContainersVersion: String by project

plugins {
    `java-library`
}

dependencies {
    api(project(":spi"))

    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")

    testImplementation(testFixtures(project(":launchers:junit")))
    testImplementation(project(":common:libraries:clients:postgresql"))
    testImplementation(testFixtures(project(":common:libraries:clients:postgresql-repository")))
    testImplementation(project(":core:protocol:web"))
    testImplementation(project(":extensions:api:data-management"))
    testImplementation("ch.qos.logback:logback-classic:1.2.6")
    testImplementation("org.testcontainers:postgresql:${testContainersVersion}")
    testImplementation("org.testcontainers:junit-jupiter:${testContainersVersion}")

    implementation(project(":common:util"))
}

publishing {
    publications {
        create<MavenPublication>("data-management-api-client") {
            artifactId = "data-management-api-client"
            from(components["java"])
        }
    }
}

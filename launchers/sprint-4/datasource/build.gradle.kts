/*
 *  Copyright (c) 2020, 2021 Microsoft Corporation
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       Microsoft Corporation - initial API and implementation
 *
 */

plugins {
    `java-library`
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val jupiterVersion: String by project
val rsApi: String by project
val okHttpVersion: String by project

dependencies {
    implementation(project(":spi"))
    implementation(project(":extensions:transaction:transaction-datasource-spi"))
    implementation("jakarta.ws.rs:jakarta.ws.rs-api:${rsApi}")
    api("com.squareup.okhttp3:okhttp:${okHttpVersion}")

    implementation(project(":extensions:sql:asset-loader"))
    implementation(project(":extensions:sql:contract-definition-loader"))

    implementation("org.postgresql:postgresql:42.3.1")

}

publishing {
    publications {
        create<MavenPublication>("sprint4-datasource") {
            artifactId = "sprint4-datasource"
            from(components["java"])
        }
    }
}

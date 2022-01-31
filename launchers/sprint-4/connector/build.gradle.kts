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

dependencies {
    implementation(project(":core"))
    implementation(project(":data-protocols:ids"))

    implementation(project(":extensions:in-memory:transfer-store-memory"))
    implementation(project(":extensions:in-memory:negotiation-store-memory"))

    implementation(project(":extensions:iam:iam-mock"))
    implementation(project(":extensions:transaction:transaction-local"))

    implementation(project(":extensions:api:control"))

    implementation(project(":extensions:filesystem:configuration-fs"))

    implementation(project(":extensions:sql:asset-index"))
    implementation(project(":extensions:sql:asset-loader"))
    implementation(project(":extensions:sql:contract-definition-store"))
    implementation(project(":extensions:sql:contract-definition-loader"))

    implementation(project(":launchers:sprint-4:datasource"))
    implementation(project(":launchers:sprint-4:fake-extensions"))

    testImplementation(testFixtures(project(":common:util")))
}

application {
    mainClass.set("org.eclipse.dataspaceconnector.dataloader.cli.ConnectorRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("connector.jar")
}

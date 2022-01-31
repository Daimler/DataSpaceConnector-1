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
    implementation(project(":core:boot"))
    implementation(project(":core:base"))

    implementation(project(":extensions:filesystem:configuration-fs"))

    implementation(project(":extensions:sql:asset-loader"))
    implementation(project(":extensions:sql:contract-definition-loader"))

    testImplementation(testFixtures(project(":common:util")))
}

application {
    mainClass.set("org.eclipse.dataspaceconnector.dataloader.cli.DataMgtRuntime")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("dataloader.jar")
}

publishing {
    publications {
        create<MavenPublication>("dataloader-cli") {
            artifactId = "dataloader-cli"
            from(components["java"])
        }
    }
}


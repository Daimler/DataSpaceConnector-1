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
    id("application")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

val jupiterVersion: String by project

dependencies {
    implementation(project(":core:bootstrap"))

    implementation(project(":extensions:filesystem:configuration-fs"))
    implementation("com.univocity:univocity-parsers:2.9.1")

    implementation("info.picocli:picocli:4.6.2")
}

application {
    @Suppress("DEPRECATION")
    mainClassName = "org.eclipse.dataspaceconnector.dataloader.cli.Runtime"
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    exclude("**/pom.properties", "**/pom.xm")
    mergeServiceFiles()
    archiveFileName.set("edc.jar")
}

publishing {
    publications {
        create<MavenPublication>("data-loader-cli-new") {
            artifactId = "data-loader-cli-new"
            from(components["java"])
        }
    }
}


/*
 * Copyright (c) Microsoft Corporation.
 * All rights reserved.
 */

val slf4jVersion: String by project

plugins {
    `java-library`
    `maven-publish`
}

dependencies {
    api(project(":spi"))
    api(project(":core:policy:policy-model"))
    api("org.slf4j:slf4j-api:${slf4jVersion}")
}

publishing {
    publications {
        create<MavenPublication>("core.contract") {
            artifactId = "core.contract"
            from(components["java"])
        }
    }
}

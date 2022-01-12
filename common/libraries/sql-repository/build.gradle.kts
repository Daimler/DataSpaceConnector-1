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

val jupiterVersion: String by project
val testContainersVersion: String by project

dependencies {
    implementation(project(":spi"))
    implementation(project(":common:libraries:sql-lib"))

    testFixturesImplementation(project(":common:libraries:sql-pool-commons-lib"))
    testFixturesImplementation(project(":common:libraries:sql-lib"))
    /* TODO Check License */
    testFixturesImplementation("com.h2database:h2:1.4.200")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-api:${jupiterVersion}")
    testFixturesImplementation("org.junit.jupiter:junit-jupiter-params:${jupiterVersion}")
}

publishing {
    publications {
        create<MavenPublication>("postgresql-asset-repository") {
            artifactId = "postgresql-asset-repository"
            from(components["java"])
        }
    }
}

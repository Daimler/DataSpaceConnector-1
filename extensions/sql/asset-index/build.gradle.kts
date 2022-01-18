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

dependencies {
    api(project(":spi"))
    implementation(project(":common:libraries:sql-pool-commons-lib"))
    implementation(project(":common:libraries:sql-postgresql-lib"))
    implementation(project(":common:libraries:sql-operations-lib"))

    implementation(project(":common:libraries:kafka-lib"))
    implementation("org.apache.kafka:kafka-clients:3.0.0")

    implementation(project(":extensions:transaction:tx-spi"))

    testImplementation(testFixtures(project(":common:libraries:sql-operations-lib")))
}

publishing {
    publications {
        create<MavenPublication>("postgresql-asset-index") {
            artifactId = "postgresql-asset-index"
            from(components["java"])
        }
    }
}

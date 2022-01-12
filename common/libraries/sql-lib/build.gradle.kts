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
 *       Daimler TSS GmbH - initial build file
 *
 */

plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
}

val h2Version: String by project

dependencies {
    testImplementation("com.h2database:h2:${h2Version}")
}

publishing {
    publications {
        create<MavenPublication>("common-sql-lib") {
            artifactId = "common-sql-lib"
            from(components["java"])
        }
    }
}

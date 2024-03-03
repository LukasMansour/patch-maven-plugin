plugins {
    id("java")
    id("de.benediktritter.maven-plugin-development") version ("0.4.2")
    id("com.gradleup.nmcp") version ("0.0.4")
    `maven-publish`
    signing
}

group = "io.github.lukasmansour"
version = "1.1.0-SNAPSHOT"
description = "A Maven patch plugin with no dependency on GNU Patch."

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
        vendor = JvmVendorSpec.ADOPTIUM
    }
    withSourcesJar()
    withJavadocJar()
}

repositories {
    mavenCentral()
}

dependencies {
    // Maven plugin dependency
    implementation("org.apache.maven:maven-plugin-api:3.9.6")
    // Compile time dependency
    compileOnly("org.apache.maven.plugin-tools:maven-plugin-annotations:3.11.0")
    // Java platform agnostic diffing utility
    implementation("io.github.java-diff-utils:java-diff-utils:4.12")
}

publishing {
    publications {
        // create a publication so that the plugin can be published to the local Maven repository
        create<MavenPublication>("patch") {
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()

            from(components["java"])
            pom {
                name.set("patch-maven-plugin")
                description.set(project.description)
                url.set("https://github.com/LukasMansour/patch-maven-plugin")
                licenses {
                    license {
                        name.set("Apache 2.0")
                        url.set("https://opensource.org/license/apache-2-0")
                    }
                }
                developers {
                    developer {
                        id.set("LukasMansour")
                        name.set("Lukas Mansour")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/LukasMansour/patch-maven-plugin.git")
                    developerConnection.set("scm:git:ssh://github.com/LukasMansour/patch-maven-plugin.git")
                    url.set("https://github.com/LukasMansour/patch-maven-plugin/tree/main")
                }
            }
        }
    }
}

signing {
    if (System.getenv()["CI"] != null) {
        useInMemoryPgpKeys(System.getenv()["SIGNING_KEY"], System.getenv()["SIGNING_PASSWORD"])
        // Only attempt to sign if we are in the CI.
        // If you are publishing to maven local then it doesn't need signing.
        sign(publishing.publications["patch"])
    }
}

nmcp {
    // nameOfYourPublication must point to an existing publication
    publish("patch") {
        username = System.getenv()["SONATYPE_CENTRAL_USERTOKEN"]
                ?: (if (hasProperty("SONATYPE_CENTRAL_USERTOKEN")) (property("SONATYPE_CENTRAL_USERTOKEN") as String) else "")
        password = System.getenv()["SONATYPE_CENTRAL_PASSWORD"]
                ?: (if (hasProperty("SONATYPE_CENTRAL_PASSWORD")) (property("SONATYPE_CENTRAL_PASSWORD") as String) else "")
        // publish manually from the portal
        publicationType = "USER_MANAGED"
    }
}
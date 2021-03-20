plugins {
    id("signing")
    id("maven-publish")
    id("org.jetbrains.kotlin.jvm").version("1.4.31")
}

val gitTag: String = (findProperty("github.tag") ?: "") as String

group = "com.github.lero4ka16"
description = "Fast, lightweight and easy template engine"
version = gitTag

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            pom {
                name.set(project.name)
                description.set(project.description)

                url.set("https://github.com/lero4ka16/te4j")

                organization {
                    name.set("com.github.lero4ka16")
                    url.set("https://github.com/lero4ka16")
                }

                issueManagement {
                    system.set("GitHub")
                    url.set("https://github.com/lero4ka16/te4j/issues")
                }

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }

                scm {
                    url.set("https://github.com/lero4ka16/te4j")
                    connection.set("scm:https://github.com/lero4ka16/te4j.git")
                    developerConnection.set("scm:git://github.com/lero4ka16/te4j.git")
                }

                developers {
                    developer {
                        id.set("lero4ka16")
                        name.set("Lero4ka16")
                        email.set("lero4ka6916@gmail.com")
                    }
                }
            }

            version = gitTag
            artifactId = project.name

            from(components["java"])
        }
    }

    repositories {
        maven {
            if (gitTag.endsWith("-SNAPSHOT")) {
                setUrl("https://s01.oss.sonatype.org/content/repositories/snapshots")
            } else {
                setUrl("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            }

            credentials {
                username = findProperty("sonatype.user").toString()
                password = findProperty("sonatype.password").toString()
            }
        }
    }
}


signing {
    sign(publishing.publications)
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks {
    jar {
        archiveBaseName.set("te4j")
    }

    test {
        useJUnitPlatform()
    }

    compileTestKotlin {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"

        kotlinOptions {
            jvmTarget = "1.8"
        }
    }

    compileKotlin {
        sourceCompatibility = "1.8"
        targetCompatibility = "1.8"

        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}
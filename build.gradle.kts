plugins {
    id("java")
    id("com.diffplug.spotless") version "7.0.2"
    id("xyz.jpenilla.run-paper") version "2.3.1"
    id("net.kyori.blossom") version "2.1.0"
}

group = "net.strokkur"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.guice)
    compileOnly(libs.configurate.hocon)
}

tasks {
    runServer {
        minecraftVersion("1.21.5")
        jvmArgs("-Xmx4G", "-Xms4G", "-Dcom.mojang.eula.agree=true")
    }
    
    processResources {
        filesMatching("paper-plugin.yml") {
            expand("version" to version)
        }
    }
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    targetCompatibility = JavaVersion.VERSION_21
    sourceCompatibility = JavaVersion.VERSION_21
}

sourceSets.main {
    blossom.javaSources  {
        property("guice", libs.versions.guice.get())
        property("configurate", libs.versions.configurate.get())
    }
}

spotless {
    java {
        licenseHeaderFile(rootProject.file("HEADER"))
        target("**/*.java")
        targetExclude("fastinv/*.java")
    }
}
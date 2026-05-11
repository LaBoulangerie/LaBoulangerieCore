group = "net.laboulangerie"
version = "1.2.0"
description = "LaBoulangerieCore"

plugins {
    java
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.21"
}

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.viaversion.com")
    maven("https://repo.betonquest.org/betonquest/")
    maven("https://repo.minebench.de/")
}

configurations {
    "compileClasspath" {
        resolutionStrategy.force("com.google.guava:guava:33.2.1-jre")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.2")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.ghostchu:quickshop-bukkit:6.2.0.11:shaded")
    compileOnly("com.ghostchu:quickshop-common:6.2.0.11:shaded")
    compileOnly("com.github.LaBoulangerie:Gringotts:master-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.16")
    compileOnly("org.betonquest:betonquest:3.0.0-SNAPSHOT")
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

tasks {
  compileJava {
    options.encoding = Charsets.UTF_8.name()
    options.release.set(21)
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name()
  }
  processResources {
    filteringCharset = Charsets.UTF_8.name()
  }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

group = "net.laboulangerie"
version = "1.1.2"
description = "LaBoulangerieCore"

plugins {
    java
    id("io.papermc.paperweight.userdev") version "1.7.3"
}

repositories {
    mavenLocal()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://jitpack.io")
    maven("https://nexus.betonquest.org/repository/betonquest/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.viaversion.com")
}

configurations {
    "compileClasspath" {
        resolutionStrategy.force("com.google.guava:guava:33.2.1-jre")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")
    compileOnly("com.github.LaBoulangerie:LaBoulangerieMMO:2.3.1")
    compileOnly("org.betonquest:betonquest:2.1.3")
    compileOnly("com.ghostchu:quickshop-bukkit:5.2.0.6:shaded")
    compileOnly("com.ghostchu:quickshop-common:5.2.0.6:shaded")
    compileOnly("com.github.LaBoulangerie:Gringotts:master-SNAPSHOT")
    compileOnly("com.comphenix.protocol:ProtocolLib:5.3.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.12")
    compileOnly("com.github.angeschossen:LandsAPI:7.10.13")
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

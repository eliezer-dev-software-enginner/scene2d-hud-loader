plugins {
    id("java-library")
    id("maven-publish")
}

group = "megalodonte"
version = "1.0.0-beta"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    withSourcesJar()
}

dependencies {
    // `api`, not `implementation`: HudView/HudLoader's public API returns real
    // gdx types (Skin, Group, Actor) directly, so consumers need them on their
    // own compile classpath too, not just at runtime.
    api("com.badlogicgames.gdx:gdx:1.14.2")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}

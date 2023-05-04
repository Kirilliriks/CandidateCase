import Build_gradle.Constants.imguiVersion
import Build_gradle.Constants.lwjglNatives
import Build_gradle.Constants.lwjglVersion
import Build_gradle.Constants.mainClassName

plugins {
    id("java")
}

group = "me.kirillirik"
version = "1.0-SNAPSHOT"

object Constants {
    const val mainClassName = "me.kirillirik.Main"
    const val lwjglVersion = "3.3.1"
    const val lwjglNatives = "natives-windows"
    const val imguiVersion = "1.86.4"
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly("org.jetbrains:annotations:23.0.0")

    implementation("org.joml:joml:1.10.4")

    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-assimp")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-openal")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")
    implementation("org.lwjgl:lwjgl::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-assimp::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-openal::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    implementation("org.lwjgl:lwjgl-stb::$lwjglNatives")
    implementation("io.github.spair:imgui-java-binding:$imguiVersion")
    implementation("io.github.spair:imgui-java-lwjgl3:$imguiVersion")
    implementation("io.github.spair:imgui-java-natives-windows:$imguiVersion")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("commons-dbutils:commons-dbutils:1.7")
    implementation("org.apache.commons:commons-lang3:3.0")
    implementation("org.postgresql:postgresql:42.2.27")
    implementation("org.slf4j:slf4j-simple:2.0.6")
}

tasks.compileJava {
    options.encoding = "UTF-8"
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    manifest {
        attributes["Main-Class"] = mainClassName
    }

    from(configurations.compileClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}
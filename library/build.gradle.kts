import com.fpliu.gradle.bintrayUploadAndroidExtension

buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        //对android-maven-gradle-plugin和gradle-bintray-plugin两个插件的包装、简化插件
        //https://github.com/leleliu008/BintrayUploadAndroidGradlePlugin
        classpath("com.fpliu:BintrayUploadAndroidGradlePlugin:1.0.0")
    }
}

apply {
    plugin("com.fpliu.bintray.upload.android")
}

plugins {
    java
    maven

    //Kotlin编译的插件
    //http://kotlinlang.org/docs/reference/using-gradle.html
    kotlin("jvm").version("1.2.21")

    //用于上传maven包到jCenter中
    //https://github.com/bintray/gradle-bintray-plugin
    id("com.jfrog.bintray").version("1.7.3")
}

java {
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib"))

    //https://github.com/square/okhttp
    implementation("com.squareup.okhttp3:okhttp:3.10.0")

    //https://github.com/square/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.4.0")

    //https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    implementation("com.squareup.retrofit2:converter-gson:2.4.0")

    //https://github.com/square/retrofit/tree/master/retrofit-adapters/rxjava2
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.4.0")

    compileOnly("org.json:org.json:2.0")
}

val rootProjectName: String = rootProject.name

// 这里是groupId,必须填写,一般填你唯一的包名
group = "com.fpliu"

//这个是版本号，必须填写
version = "1.0.0"

bintrayUploadAndroidExtension {
    developerName = "leleliu008"
    developerEmail = "leleliu008@gamil.com"

    projectSiteUrl = "https://github.com/$developerName/$rootProjectName"
    projectGitUrl = "https://github.com/$developerName/$rootProjectName"

    bintrayUserName = "fpliu"
    bintrayOrganizationName = "fpliu"
    bintrayRepositoryName = "newton"
    bintrayApiKey = "xxxxxxxxxxxxxxxxxxxxxxxxxxxx"
}
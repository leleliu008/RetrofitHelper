plugins {
    kotlin("jvm").version("1.2.21")
    java
}

java {
    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin")
        }
    }
}

dependencies {
    project(":library")

    //可以使用我扩展了的Retrofit，当然也可以直接使用默认的Retrofit
//    implementation("com.fpliu:RetrofitHelper:1.0.0") {
//        exclude("com.squareup.retrofit2", "retrofit")
//    }
//
//    implementation("com.fpliu:retrofit:2.4.0")

    implementation(kotlin("stdlib"))

    //Java版中必须添加该依赖
    implementation("org.json:org.json:2.0")

    //https://github.com/square/okhttp
    implementation("com.squareup.okhttp3:okhttp:3.10.0")

    //https://github.com/square/retrofit
    implementation("com.squareup.retrofit2:retrofit:2.4.0")

    //https://github.com/square/retrofit/tree/master/retrofit-converters/gson
    implementation("com.squareup.retrofit2:converter-gson:2.4.0")

    //https://github.com/square/retrofit/tree/master/retrofit-adapters/rxjava2
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.4.0")
}

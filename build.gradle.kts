buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        //Kotlin编译的插件
        //http://kotlinlang.org/docs/reference/using-gradle.html
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.2.21")
    }
}

allprojects {
    repositories {
        jcenter()
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}

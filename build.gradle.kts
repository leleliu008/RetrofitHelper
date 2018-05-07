allprojects {
    repositories {
        jcenter()
    }
}

task("clean", Delete::class) {
    delete(rootProject.buildDir)
}

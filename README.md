# Arrow
**Version 0.2**

Java library and annotation processor that makes dagger multibinding easy.


# Setup
In the build.gradle of your project add:

```gradle
repositories {
    maven {
       url 'https://dl.bintray.com/ulrichraab/maven'
    }
}
```

In the build.gradle of your app module add:

```gradle
dependencies {
    compile 'de.ulrichraab.arrow:arrow:<version>'
    annotationProcessor 'de.ulrichraab.arrow:arrow-processor:<version>'
}
```

# Example
See demo app
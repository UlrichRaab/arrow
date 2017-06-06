# Arrow
**Version 0.4**

Using Dagger 2 for dependency injection in Android is popular and common
today. Getting familiar with this library, its features and use cases
can be challenging, but if you use it correctly you will maybe write
cleaner code.

I assume that you already know how to use Dagger 2. If not, you should
read [Dependency Injection with Dagger 2](https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2)
and [Activities Subcomponents Multibinding in Dagger 2](https://medium.com/azimolabs/activities-subcomponents-multibinding-in-dagger-2-85d6053d6a95).

A common Dagger setup on Android normally involves an app component and
an app module. The module provides dependencies and the component is
used to inject these dependencies into objects like activities or
fragments. Injection is done by using a code like the one shown below:

```java
public class MyActivity extends Activity {

    @Inject TwitterApi twitterApi;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((MyApp)getApplication())
            .getAppComponent()
            .inject(this);
    }
}
```

However, this simple example breaks a core principle of DI:
**a class shouldn’t know anything about how it is injected**.





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
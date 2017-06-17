# Arrow
**Version 0.6**

> **Arrow is currently in development and NOT STABLE. Use it on your own risk.**

I assume that you already know how to use Dagger 2. If not, you should
read [Dependency Injection with Dagger 2](https://github.com/codepath/android_guides/wiki/Dependency-Injection-with-Dagger-2)
and [Activities Subcomponents Multibinding in Dagger 2](https://medium.com/azimolabs/activities-subcomponents-multibinding-in-dagger-2-85d6053d6a95).

A common Dagger setup on Android normally involves an app component and
an app module. The module provides dependencies and the component is
used to inject these dependencies into objects like activities or
fragments. Injection is done by using a code like the one shown below:

```java
public class MyActivity extends Activity {

    ...

    private void injectDependencies() {
        ((MyApp)getApplication())
            .getAppComponent()
            .inject(this);
    }
}
```

The example looks good and not to complicated but as soon as you want to
inject dependencies into more then a couple activities your AppComponent
will get cluttered.

TODO Add example code

A good solution is to use subcomponents for every activity or group of
activities. This leads to the question how to create subcomponents. I
personally prefer to use multibinding to solve this problem.
Implementing multibinding can be challenging and there is also some
boilerplate code which you need to write to make it work.

See: [Activities Subcomponents Multibinding in Dagger 2](https://medium.com/azimolabs/activities-subcomponents-multibinding-in-dagger-2-85d6053d6a95)

I created Arrow to simplify this by using an annotation processor. Arrow
uses the dagger Subcomponent and Subcomponent.Builder annotations and
generates the multibinding module for you. You can then create
subcomponents by using the Arrow class.

```java
public class MyActivity extends Activity {

    ...

    private void injectDependencies() {
        Arrow.subcomponentBuilder(MyActivitySubcomponent.Builder.class)
             .build()
             .inject(this);
    }
}
```

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
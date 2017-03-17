package de.ulrichraab.arrow;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public @interface ArrowInjector {

    /**
     * Class of the injector builder.
     */
    Class<?> builder ();

    /**
     * Class of the target in which to inject dependencies.
     */
    Class<?> target ();
}

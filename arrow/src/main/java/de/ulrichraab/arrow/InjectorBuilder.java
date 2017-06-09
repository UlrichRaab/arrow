package de.ulrichraab.arrow;


/**
 * Generic builder for injectors.
 * @author Ulrich Raab
 */
public abstract class InjectorBuilder<T> {

    /**
     * Builds and returns a new injector.
     */
    public abstract T build ();
}

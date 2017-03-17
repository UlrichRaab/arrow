package de.ulrichraab.arrow;


import dagger.BindsInstance;
import dagger.MembersInjector;


/**
 * Performs members injection.
 * @param <T> The type of the object, in which to inject dependencies.
 * @author Ulrich Raab
 */
public interface Injector<T> extends MembersInjector<T> {

    /**
     * Builder for injectors.
     * @param <T> The concrete injector type build by this builder.
     */
    abstract class Builder<T> {

        /**
         * Provides the injection target to be used in the binding graph of the build injector.
         */
        @BindsInstance
        public abstract Builder<T> target (Object target);

        /**
         * Returns a newly created injector.
         */
        public abstract T build ();
    }
}
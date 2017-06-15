package de.ulrichraab.arrow.app.rng;


import dagger.Subcomponent;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
@Subcomponent
public interface RngSubcomponent {

    void inject (Rng rng);

    @Subcomponent.Builder
    abstract class Builder {

        public abstract RngSubcomponent build ();
    }
}

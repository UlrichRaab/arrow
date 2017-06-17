package de.ulrichraab.arrow.app.rng;


import java.security.SecureRandom;

import javax.inject.Inject;

import de.ulrichraab.arrow.Arrow;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public class Rng {

    @Inject
    SecureRandom secureRandom;

    public Rng () {
        Arrow.subcomponentBuilder(RngSubcomponent.Builder.class)
             .build()
             .inject(this);
    }

    public Long generate() {
        return secureRandom.nextLong();
    }
}

package de.ulrichraab.arrow.app.rng;


import java.security.SecureRandom;

import javax.inject.Inject;

import de.ulrichraab.arrow.Arrow;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public class Rng {

    @Inject
    SecureRandom secureRandom;

    public Rng () {
        Arrow.getSubcomponentBuilder(RngSubcomponent.Builder.class)
             .build()
             .inject(this);
    }

    public Long generate () {
        return secureRandom.nextLong();
    }
}

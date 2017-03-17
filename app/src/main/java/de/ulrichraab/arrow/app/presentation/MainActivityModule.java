package de.ulrichraab.arrow.app.presentation;


import java.security.SecureRandom;

import dagger.Module;
import dagger.Provides;
import de.ulrichraab.arrow.app.di.scope.ActivityScope;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
@Module
public class MainActivityModule {

    @Provides
    @ActivityScope
    Long random () {
        return new SecureRandom().nextLong();
    }
}

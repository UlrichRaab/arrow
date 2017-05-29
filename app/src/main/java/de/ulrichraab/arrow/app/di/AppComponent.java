package de.ulrichraab.arrow.app.di;


import javax.inject.Singleton;

import dagger.Component;
import dagger.MembersInjector;
import de.ulrichraab.arrow.Arrow;
import de.ulrichraab.arrow.ArrowConfiguration;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
@Singleton
@Component(modules = {
    AppModule.class,
    MultibindingModule.class
})
@ArrowConfiguration(module = "de.ulrichraab.arrow.app.di.MultibindingModule")
public interface AppComponent extends MembersInjector<Arrow> {}

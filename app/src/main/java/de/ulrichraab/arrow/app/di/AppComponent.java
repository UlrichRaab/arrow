package de.ulrichraab.arrow.app.di;


import javax.inject.Singleton;

import dagger.Component;
import dagger.MembersInjector;
import de.ulrichraab.arrow.Arrow;
import de.ulrichraab.arrow.ArrowMultibindingModule;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
@Singleton
@Component(modules = {
    AppModule.class,
    MultibindingModule.class
})
@ArrowMultibindingModule("de.ulrichraab.arrow.app.di.MultibindingModule")
public interface AppComponent extends MembersInjector<Arrow> {}

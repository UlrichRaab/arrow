package de.ulrichraab.arrow.app.presentation;


import dagger.BindsInstance;
import dagger.Subcomponent;
import de.ulrichraab.arrow.ArrowInjector;
import de.ulrichraab.arrow.Injector;
import de.ulrichraab.arrow.app.di.scope.ActivityScope;
import de.ulrichraab.arrow.app.model.User;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
@Subcomponent(modules = {
    MainActivityModule.class
})
@ArrowInjector(key = "di://main-activity", builder = MainActivityInjector.Builder.class)
@ActivityScope
public interface MainActivityInjector extends Injector {

    void inject (MainActivity mainActivity);

    @Subcomponent.Builder
    abstract class Builder extends Injector.Builder<MainActivityInjector> {

        @BindsInstance
        public abstract Builder user (User user);

        public abstract Builder mainActivityModule (MainActivityModule module);
    }
}

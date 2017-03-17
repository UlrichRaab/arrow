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
@ArrowInjector(
    builder = MainActivityInjector.Builder.class,
    target = MainActivity.class
)
@ActivityScope
public interface MainActivityInjector extends Injector<MainActivity> {

    @Subcomponent.Builder
    abstract class Builder extends Injector.Builder<MainActivityInjector> {

        @BindsInstance
        public abstract Builder user (User user);

        public abstract Builder mainActivityModule (MainActivityModule module);
    }
}

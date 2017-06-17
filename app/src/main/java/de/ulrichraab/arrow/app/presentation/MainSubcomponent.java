package de.ulrichraab.arrow.app.presentation;


import dagger.BindsInstance;
import dagger.Subcomponent;
import de.ulrichraab.arrow.app.di.scope.ActivityScope;
import de.ulrichraab.arrow.app.model.User;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
@ActivityScope
@Subcomponent
public interface MainSubcomponent {

    void inject (MainActivity mainActivity);

    @Subcomponent.Builder
    abstract class Builder {

        @BindsInstance
        public abstract Builder user (User user);

        public abstract MainSubcomponent build ();
    }
}

package de.ulrichraab.arrow.app;


import android.app.Application;

import de.ulrichraab.arrow.Arrow;
import de.ulrichraab.arrow.app.di.AppComponent;
import de.ulrichraab.arrow.app.di.AppModule;
import de.ulrichraab.arrow.app.di.DaggerAppComponent;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public class App extends Application {

    @Override
    public void onCreate () {
        super.onCreate();
        initializeDependencyInjection();
    }

    private void initializeDependencyInjection () {

        AppModule appModule = new AppModule(this);
        AppComponent appComponent = DaggerAppComponent
            .builder()
            .appModule(appModule)
            .build();

        Arrow.initialize(appComponent);
    }
}

package de.ulrichraab.arrow.app.di;


import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.ulrichraab.arrow.app.model.Device;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
@Module
public class AppModule {

    private Context appContext;

    public AppModule (@NonNull Context appContext) {
        this.appContext = appContext;
    }

    @Singleton
    @Provides
    @Named("appContext")
    Context appContext () {
        return appContext;
    }

    @Singleton
    @Provides
    Device device () {
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
        return new Device(deviceName);
    }
}

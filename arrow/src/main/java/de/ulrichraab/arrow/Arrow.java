/*
 * Copyright (C) 2017 Ulrich Raab.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.ulrichraab.arrow;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import dagger.MembersInjector;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public final class Arrow {

    private static final Arrow INSTANCE = new Arrow();

    @Inject
    Map<String, Provider<InjectorBuilder<?>>> providers;

    /**
     * Private constructor. Instance creation not allowed
     */
    private Arrow () {}

    /**
     * Initializes arrow using the specified injector.
     */
    public static void initialize (MembersInjector<Arrow> injector) {
        if (injector == null) {
            throw new NullPointerException("injector == null");
        }
        injector.injectMembers(INSTANCE);
    }


    public static <B> B injector (String key, Class<B> injectorClass) {

        Map<String, Provider<InjectorBuilder<?>>> providers = INSTANCE.providers;
        if (providers == null || providers.isEmpty()) {
            throw new IllegalStateException("Arrow is not initialized");
        }

        Provider<InjectorBuilder<?>> provider = INSTANCE.providers.get(key);
        return injectorClass.cast(provider.get().build());
    }

    /**
     * Creates a new injector builder.
     * @param key
     * @param builderClass The class of the injector builder.
     * @param <B> The concrete type of the injector builder.
     * @throws IllegalArgumentException If {@code target} or {@code builderClass} are {@code null}.
     * @throws IllegalStateException If arrow is not initialized.
     */
    @SuppressWarnings("ConstantConditions")
    public static <B extends InjectorBuilder<?>> B injectorBuilder (String key, Class<B> builderClass) {

        // Ensure arguments are not null
        List<String> missing = new ArrayList<>();
        if (key == null || key.isEmpty()) {
            missing.add("key");
        }
        if (builderClass == null) {
            missing.add(" builderClass");
        }
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "Missing required arguments %1$s",
                Arrays.toString(missing.toArray())
            ));
        }

        Map<String, Provider<InjectorBuilder<?>>> providers = INSTANCE.providers;
        if (providers == null || providers.isEmpty()) {
            throw new IllegalStateException("Arrow is not initialized");
        }

        Provider<InjectorBuilder<?>> provider = INSTANCE.providers.get(key);
        return builderClass.cast(provider.get());
    }
}

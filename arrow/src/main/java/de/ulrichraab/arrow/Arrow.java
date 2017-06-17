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


import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import dagger.MembersInjector;


/**
 * TODO Write javadoc
 * @author Ulrich Raab
 */
public final class Arrow {

    private static final Arrow INSTANCE = new Arrow();

    @Inject
    @Named("arrow://subcomponent-builders")
    Map<Class<?>, Provider<Object>> providers;

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

    /**
     * Returns a subcomponent builder mapped by the specified builder class.
     * @param builderClass The class of the requested subcomponent.
     * @param <B> The subcomponent type.
     * @return The subcomponent mapped by the specified builder class.
     * @throws IllegalArgumentException If {@code builderClass} is {@code null}.
     * @throws IllegalStateException If arrow is not initialized.
     */
    public static <B> B subcomponentBuilder (Class<B> builderClass) {

        if (builderClass == null) {
            throw new IllegalArgumentException("builderClass must be not null");
        }

        Map<Class<?>, Provider<Object>> providers = INSTANCE.providers;
        if (providers == null || providers.isEmpty()) {
            throw new IllegalStateException("Arrow is not initialized");
        }

        Provider<Object> provider = providers.get(builderClass);
        return builderClass.cast(provider.get());
    }
}

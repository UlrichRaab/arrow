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


import dagger.BindsInstance;
import dagger.MembersInjector;


/**
 * Performs members injection.
 * @param <T> The type of the object, in which to inject dependencies.
 * @author Ulrich Raab
 */
public interface Injector<T> extends MembersInjector<T> {

    /**
     * Builder for injectors.
     * @param <T> The concrete injector type build by this builder.
     */
    abstract class Builder<T> {

        /**
         * Provides the injection target to be used in the binding graph of the build injector.
         */
        @BindsInstance
        public abstract Builder<T> target (Object target);

        /**
         * Returns a newly created injector.
         */
        public abstract T build ();
    }
}
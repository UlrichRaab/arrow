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
package de.ulrichraab.arrow.processor;


import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;

import de.ulrichraab.arrow.ArrowInjector;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
class ArrowInjectorAnnotationHelper {

    /**
     * This is a utility class. Instance creation not allowed.
     */
    private ArrowInjectorAnnotationHelper () {}

    /**
     * Returns the builder class as {@link String}.
     */
    static String getBuilder (ArrowInjector annotation) {
        // This is a workaround.
        // See http://stackoverflow.com/questions/7687829/java-6-annotation-processing-getting-a-class-from-an-annotation
        try {
            Class<?> builder = annotation.builder();
            return builder.getName() + ".class";
        }
        catch (MirroredTypeException e) {
            TypeMirror builder = e.getTypeMirror();
            return builder + ".class";
        }
    }

    /**
     * Returns the target class as {@link String}.
     */
    static String getTarget (ArrowInjector annotation) {
        // This is a workaround.
        // See http://stackoverflow.com/questions/7687829/java-6-annotation-processing-getting-a-class-from-an-annotation
        try {
            Class<?> target = annotation.target();
            return target.getName() + ".class";
        }
        catch (MirroredTypeException e) {
            TypeMirror target = e.getTypeMirror();
            return target + ".class";
        }
    }
}

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

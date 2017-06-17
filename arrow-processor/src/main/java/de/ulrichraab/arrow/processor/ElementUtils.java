package de.ulrichraab.arrow.processor;


import java.lang.annotation.Annotation;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.SimpleElementVisitor6;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
class ElementUtils {

    static boolean isAnnotationPresent (Element element, Class<? extends Annotation> annotationClass) {
        return getAnnotationMirror(element, annotationClass) != null;
    }

    /**
     * Returns an {@link AnnotationMirror} for the annotation of type {@code annotationClass} on
     * {@code element}, or null if no such annotation exists.
     */
    static AnnotationMirror getAnnotationMirror (Element element, Class<? extends Annotation> annotationClass) {
        String annotationClassName = annotationClass.getCanonicalName();
        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            TypeElement annotationTypeElement = asType(annotationMirror.getAnnotationType().asElement());
            if (annotationTypeElement.getQualifiedName().contentEquals(annotationClassName)) {
                return annotationMirror;
            }
        }
        return null;
    }

    static TypeElement asType(Element element) {
        return element.accept(TypeElementVisitor.INSTANCE, null);
    }

    private static final class TypeElementVisitor extends CastingElementVisitor<TypeElement> {

        private static final TypeElementVisitor INSTANCE = new TypeElementVisitor();

        TypeElementVisitor () {
            super("type element");
        }

        @Override
        public TypeElement visitType (TypeElement e, Void ignore) {
            return e;
        }
    }

    private abstract static class CastingElementVisitor<T> extends SimpleElementVisitor6<T, Void> {

        private final String label;

        CastingElementVisitor (String label) {
            this.label = label;
        }

        @Override
        protected final T defaultAction (Element e, Void ignore) {
            throw new IllegalArgumentException(e + " does not represent a " + label);
        }
    }
}

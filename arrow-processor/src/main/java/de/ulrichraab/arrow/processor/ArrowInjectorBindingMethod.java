package de.ulrichraab.arrow.processor;


import de.ulrichraab.arrow.ArrowInjector;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public class ArrowInjectorBindingMethod {

    private final String classKey;
    private final String builder;
    private final String name;

    ArrowInjectorBindingMethod (ArrowInjector annotation) {
        classKey = ArrowInjectorAnnotationHelper.getTarget(annotation);
        builder = ArrowInjectorAnnotationHelper.getBuilder(annotation).replace(".class", "");
        name = builder.replaceAll("\\.", "_");
    }

    public String getClassKey () {
        return classKey;
    }

    public String getBuilder () {
        return builder;
    }

    public String getName () {
        return name;
    }
}

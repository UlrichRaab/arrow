package de.ulrichraab.arrow.processor.model;


import javax.lang.model.type.MirroredTypeException;

import de.ulrichraab.arrow.ArrowInjector;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public class BindsInjectorBuilderMethod {

    private String builder;
    private String key;
    private String name;

    public BindsInjectorBuilderMethod (ArrowInjector annotation) {

        // Set builder class. !! This is a workaround !!
        // See http://stackoverflow.com/questions/7687829/java-6-annotation-processing-getting-a-class-from-an-annotation
        try {
            builder = annotation.builder().getName();
        }
        catch (MirroredTypeException e) {
            builder = e.getTypeMirror().toString();
        }

        key = '"' + annotation.key() + '"';
        name = builder.replaceAll("\\.", "_");
    }

    public String getBuilder () {
        return builder;
    }

    public String getKey () {
        return key;
    }

    public String getName () {
        return name;
    }
}

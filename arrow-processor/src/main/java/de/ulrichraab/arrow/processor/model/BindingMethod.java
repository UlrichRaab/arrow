package de.ulrichraab.arrow.processor.model;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public class BindingMethod {

    private final String builder;
    private final String name;

    public BindingMethod (String builder, String name) {
        this.builder = builder;
        this.name = name;
    }

    public String getBuilder () {
        return builder;
    }

    public String getName () {
        return name;
    }

    public String getKey () {
        return builder + ".class";
    }
}

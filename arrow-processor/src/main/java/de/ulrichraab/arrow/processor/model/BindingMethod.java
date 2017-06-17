package de.ulrichraab.arrow.processor.model;


import java.util.Comparator;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
public class BindingMethod {

    private final String builder;
    private final String name;
    private final String key;

    public BindingMethod (String builder, String name, String key) {
        this.builder = builder;
        this.name = name;
        this.key = key;
    }

    public String getBuilder () {
        return builder;
    }

    public String getName () {
        return name;
    }

    public String getKey () {
        return key;
    }

    /**
     * Compares binding methods by method name.
     */
    public static class NameComparator implements Comparator<BindingMethod> {

        @Override
        public int compare (BindingMethod l, BindingMethod r) {
            return l.getName().compareTo(r.getName());
        }
    }
}

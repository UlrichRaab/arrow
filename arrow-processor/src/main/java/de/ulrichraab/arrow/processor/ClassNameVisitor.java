package de.ulrichraab.arrow.processor;


import javax.lang.model.element.Element;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
class ClassNameVisitor implements ElementVisitor<String, Void> {

    @Override
    public String visit (Element e, Void v) {
        return null;
    }

    @Override
    public String visit (Element e) {
        return null;
    }

    @Override
    public String visitPackage (PackageElement e, Void v) {
        return null;
    }

    @Override
    public String visitType (TypeElement typeElement, Void v) {
        return typeElement.getQualifiedName().toString();
    }

    @Override
    public String visitVariable (VariableElement e, Void v) {
        return null;
    }

    @Override
    public String visitExecutable (ExecutableElement e, Void v) {
        return null;
    }

    @Override
    public String visitTypeParameter (TypeParameterElement e, Void aVoid) {
        return null;
    }

    @Override
    public String visitUnknown (Element e, Void aVoid) {
        return null;
    }
}

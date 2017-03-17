package de.ulrichraab.arrow.processor;


import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;

import de.ulrichraab.arrow.ArrowInjector;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
@SupportedAnnotationTypes("de.ulrichraab.arrow.ArrowInjector")
public class ArrowAnnotationProcessor extends AbstractProcessor {

    private VelocityEngine velocityEngine;

    public ArrowAnnotationProcessor () {
        try {
            initializeVelocity();

        }
        catch (Exception e) {
            throw new RuntimeException("Initializing velocity failed:" + e.getMessage(), e);
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion () {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process (Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        Set<? extends Element> arrowInjectorElements = roundEnv.getElementsAnnotatedWith(ArrowInjector.class);
        VelocityContext vc = new VelocityContext();
        vc.put("packageName", "de.ulrichraab.arrow");
        vc.put("arrowInjectors", getArrowInjectors(arrowInjectorElements));
        vc.put("arrowInjectorBindingMethods", getArrowInjectorBindingMethods(arrowInjectorElements));
        writeSourceFile(vc);

        return true;
    }

    private void initializeVelocity () throws Exception {

        URL url = this.getClass().getClassLoader().getResource("velocity.properties");
        if (url == null) {
            return;
        }

        Properties props = new Properties();
        props.load(url.openStream());

        velocityEngine = new VelocityEngine(props);
        velocityEngine.init();
    }

    private List<String> getArrowInjectors (Set<? extends Element> elements) {

        List<String> arrowInjectors = new ArrayList<>();
        for (Element element : elements) {
            String arrowInjector = element.accept(new ClassNameVisitor(), null) + ".class";
            arrowInjectors.add(arrowInjector);
        }

        return arrowInjectors;
    }

    private List<ArrowInjectorBindingMethod> getArrowInjectorBindingMethods (Set<? extends Element> elements) {

        List<ArrowInjectorBindingMethod> methods = new ArrayList<>();
        for (Element element : elements) {
            ArrowInjector annotation = element.getAnnotation(ArrowInjector.class);
            ArrowInjectorBindingMethod method = new ArrowInjectorBindingMethod(annotation);
            methods.add(method);
        }

        return methods;
    }


    private void writeSourceFile (VelocityContext context) {
        try {
            JavaFileObject sourceFile = processingEnv
                .getFiler()
                .createSourceFile("de.ulrichraab.arrow.ArrowMultibindingModule");
            Writer writer = sourceFile.openWriter();
            Template vt = velocityEngine.getTemplate("arrow_multibinding_module.vm");
            vt.merge(context, writer);
            writer.close();
        }
        catch (IOException e) {
            // Note: calling e.printStackTrace() will print IO errors
            // That occurs if the file already exists after its first run, this is normal.
        }
        catch (Exception e) {
            throw new RuntimeException("Writing source file failed", e);
        }
    }
}

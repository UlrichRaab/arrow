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

import de.ulrichraab.arrow.ArrowMultibindingModule;
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

        Set<? extends Element> arrowConfigurationElements = roundEnv.getElementsAnnotatedWith(ArrowMultibindingModule.class);
        String modulePackage = getModulePackage(arrowConfigurationElements);
        String moduleClass = getModuleClass(arrowConfigurationElements);

        Set<? extends Element> arrowInjectorElements = roundEnv.getElementsAnnotatedWith(ArrowInjector.class);
        VelocityContext vc = new VelocityContext();
        vc.put("packageName", modulePackage);
        vc.put("className", moduleClass);
        vc.put("arrowInjectors", getArrowInjectors(arrowInjectorElements));
        vc.put("arrowInjectorBindingMethods", getArrowInjectorBindingMethods(arrowInjectorElements));
        writeSourceFile(vc, modulePackage + "." + moduleClass);

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

    private String getModulePackage (Set<? extends Element> elements) {

        for (Element element : elements) {
            ArrowMultibindingModule annotation = element.getAnnotation(ArrowMultibindingModule.class);
            String value = annotation.value();
            int index = value.lastIndexOf(".");
            if (index <= 0 || index >= value.length()) {
                continue;
            }
            return value.substring(0, index);
        }

        return null;
    }

    private String getModuleClass (Set<? extends Element> elements) {

        for (Element element : elements) {
            ArrowMultibindingModule annotation = element.getAnnotation(ArrowMultibindingModule.class);
            String value = annotation.value();
            int index = value.lastIndexOf(".");
            if (index <= 0 || index >= value.length()) {
                continue;
            }
            return value.substring(index + 1);
        }

        return null;
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

    private void writeSourceFile (VelocityContext context, String sourceFileName) {
        try {
            JavaFileObject sourceFile = processingEnv
                .getFiler()
                .createSourceFile(sourceFileName);

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

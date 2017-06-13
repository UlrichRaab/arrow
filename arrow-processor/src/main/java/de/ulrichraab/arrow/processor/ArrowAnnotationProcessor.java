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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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

import dagger.Subcomponent;
import de.ulrichraab.arrow.ArrowConfiguration;
import de.ulrichraab.arrow.processor.model.BindingMethod;
import de.ulrichraab.arrow.processor.model.ModuleClass;


/**
 * TODO Write documentation
 * @author Ulrich Raab
 */
@SupportedAnnotationTypes({
    "de.ulrichraab.arrow.ArrowConfiguration",
    "dagger.Subcomponent",
    "dagger.Subcomponent.Builder"
})
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

        ModuleClass.Builder moduleClassBuilder = createModuleClassBuilder(roundEnv);
        if (moduleClassBuilder == null) {
            return false;
        }

        List<String> subcomponentClasses = getSubcomponentClasses(roundEnv);
        moduleClassBuilder.subcomponentClasses(subcomponentClasses);

        List<BindingMethod> bindingMethods = getBindingMethods(roundEnv);
        moduleClassBuilder.bindingMethods(bindingMethods);

        ModuleClass moduleClass = moduleClassBuilder.build();

        VelocityContext vc = new VelocityContext();
        vc.put("model", moduleClass);

        writeSourceFile(vc, moduleClass.getSourceFileName());

        return true;
    }

    private ModuleClass.Builder createModuleClassBuilder (RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ArrowConfiguration.class);
        for (Element element : elements) {
            ArrowConfiguration annotation = element.getAnnotation(ArrowConfiguration.class);
            String value = annotation.module();
            int index = value.lastIndexOf(".");
            if (index <= 0 || index >= value.length()) {
                continue;
            }

            return new ModuleClass.Builder()
                .packageName(value.substring(0, index))
                .className(value.substring(index + 1));
        }

        return null;
    }

    private List<String> getSubcomponentClasses (RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Subcomponent.class);
        Set<String> subcomponentClasses = new HashSet<>(elements.size());

        for (Element element : elements) {
            String subcomponentClass = ElementUtils.asType(element).getQualifiedName().toString();
            subcomponentClass = subcomponentClass + ".class";
            subcomponentClasses.add(subcomponentClass);
        }

        // Sort the subcomponent classes for better readability of generated code
        List<String> temp = new ArrayList<>(subcomponentClasses);
        Collections.sort(temp);

        return temp;
    }

    private List<BindingMethod> getBindingMethods (RoundEnvironment roundEnv) {

        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(Subcomponent.Builder.class);
        Set<BindingMethod> bindingMethods = new HashSet<>(elements.size());

        for (Element element : elements) {

            String builderClass = ElementUtils.asType(element).getQualifiedName().toString();
            String methodName = builderClass.replaceAll("\\.", "_");

            BindingMethod bindingMethod = new BindingMethod(builderClass, methodName);
            bindingMethods.add(bindingMethod);
        }

        // Sort the binding methods for better readability of generated code
        List<BindingMethod> temp = new ArrayList<>(bindingMethods);
        Collections.sort(temp, new Comparator<BindingMethod>() {
            @Override
            public int compare (BindingMethod l, BindingMethod r) {
                return l.getName().compareTo(r.getName());
            }
        });

        return new ArrayList<>(bindingMethods);
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

    private void writeSourceFile (VelocityContext context, String sourceFileName) {
        try {
            JavaFileObject sourceFile = processingEnv
                .getFiler()
                .createSourceFile(sourceFileName);

            Writer writer = sourceFile.openWriter();
            Template vt = velocityEngine.getTemplate("arrow_multibinding_module_v2.vm");
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

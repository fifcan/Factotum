package net.riotopsys.factotum.compiler;

import com.google.auto.service.AutoService;
import net.riotopsys.factotum.api.annotation.Task;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

/**
 * Created by afitzgerald on 8/27/14.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({
        "net.riotopsys.factotum.api.annotation.Task"
})
public class TaskProcessor extends AbstractProcessor {

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Messager messager = processingEnv.getMessager();
        try {
            for (Element elem : roundEnv.getElementsAnnotatedWith(Task.class)) {

                TypeElement typeElement = Util.getTypeElement(elem);
                if ( typeElement.getKind() == ElementKind.INTERFACE){
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Annotation (%s) not supported on interfaces (%s)",
                                    Task.class.getCanonicalName(),
                                    typeElement.getQualifiedName().toString()),
                            elem);
                    continue;
                }

                if ( !Util.hasDefaultConstructor(typeElement) ){
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Annotation (%s) requires a default non private constructor in class (%s)",
                                    Task.class.getCanonicalName(),
                                    typeElement.getQualifiedName().toString()),
                            elem);
                    continue;
                }

                if ( typeElement.getTypeParameters().size() > 0 ){
                    messager.printMessage(Diagnostic.Kind.ERROR,
                            String.format("Annotation (%s) not supported on templatized type (%s)",
                                    Task.class.getCanonicalName(),
                                    typeElement.getQualifiedName().toString()),
                            elem);
                    continue;
                }

                new RequestWriter( processingEnv, (ExecutableElement) elem ).write();
            }
        } catch ( IOException e ){
            throw new RuntimeException(e);
        }

        return true;
    }

}

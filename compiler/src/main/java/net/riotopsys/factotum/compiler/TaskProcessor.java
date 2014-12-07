package net.riotopsys.factotum.compiler;

import com.google.auto.service.AutoService;
import net.riotopsys.factotum.api.annotation.Task;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
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
        RequestWriter requestWriter = new RequestWriter( processingEnv );

        try {
            for (Element elem : roundEnv.getElementsAnnotatedWith(Task.class)) {
                requestWriter.write((ExecutableElement) elem);
            }
        } catch ( IOException e ){
            throw new RuntimeException(e);
        }

        return true;
    }



}

package net.riotopsys.factotum.compiler.test;

import com.google.testing.compile.JavaFileObjects;
import net.riotopsys.factotum.compiler.TaskProcessor;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.tools.JavaFileObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

/**
 * Created by afitzgerald on 8/27/14.
 */
public class TaskProcessorTest {

    @Ignore
    @Test
    public void testSimpleTask(){

//        JavaFileObject result = JavaFileObjects.forResource("SpecialTaskHandler.java");

        ASSERT.about(javaSource())
                .that(JavaFileObjects.forSourceString("test/SimpleTask",
                        "package test;\n" +
                        "\n" +
                        "import net.riotopsys.factotum.api.annotation.Task;\n" +
                        "\n" +
                        "/**\n" +
                        " * Created by afitzgerald on 8/27/14.\n" +
                        " */\n" +
                        "public class SimpleTask {\n" +
                        "\n" +
                        "    @Task()\n" +
                        "    public String specialTaskHandler( String stuff ){\n" +
                        "        return null;\n" +
                        "    }\n" +
                        "}"))
                .processedWith(new TaskProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forSourceString("test/SpecialTaskHandlerRequest",
                        "package test;\n" +
                        "\n" +
                        "import net.riotopsys.factotum.api.abstracts.AbstractRequest;\n" +
                        "\n" +
                        "/**\n" +
                        " * Created by afitzgerald on 8/27/14.\n" +
                        " */\n" +
                        "public final class SpecialTaskHandlerRequest extends AbstractRequest{\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public Class HandleingClass() {\n" +
                        "        return SimpleTask.class;\n" +
                        "    }\n" +
                        "\n" +
                        "    @Override\n" +
                        "    public Object execute(Object handler) {\n" +
                        "        return null;\n" +
//                        "        return new Object();\n" +
                        "    }\n" +
                        "}\n"));
    }

    @Ignore
    @Test
    public void testSimpleTask2(){

        try {
            JavaFileObject source = JavaFileObjects.forSourceString("test/SimpleTask", readResource("/SimpleTask.java"));
            JavaFileObject expected = JavaFileObjects.forSourceString("test/SpecialTaskHandlerRequest", readResource("/SpecialTaskHandlerRequest.java"));


            ASSERT.about(javaSource())
                    .that(source)
                    .processedWith(new TaskProcessor())
                    .compilesWithoutError()
                    .and()
                    .generatesSources(expected);
            ;
        } catch ( Exception e ){
            Assert.fail();
        }

    }


    @Test
    public void testSimpleTask3(){

        try {
            JavaFileObject source = getSource("/test/SimpleTask.java");
            JavaFileObject expected = getSource("/test/SpecialTaskHandlerRequest.java");


            ASSERT.about(javaSource())
                    .that(source)
                    .processedWith(new TaskProcessor())
                    .compilesWithoutError()
                    .and()
                    .generatesSources(expected);
            ;
        } catch ( Exception e ){
            e.printStackTrace();
            Assert.fail();
        }

    }

    private JavaFileObject getSource(String path) throws IOException {
        return JavaFileObjects.forSourceString(path.substring(1).replace(".java",""), readResource(path));
    }


    private String readResource(String path) throws IOException {

        BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream(path)));
        String line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");

        while( ( line = reader.readLine() ) != null ) {
            stringBuilder.append( line );
            stringBuilder.append( ls );
        }

        return stringBuilder.toString();
    }

}

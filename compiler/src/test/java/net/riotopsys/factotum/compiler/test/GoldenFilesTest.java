package net.riotopsys.factotum.compiler.test;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.testing.compile.JavaFileObjects;
import net.riotopsys.factotum.compiler.TaskProcessor;
import net.riotopsys.factotum.compiler.test.auxiliary.SourceTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import javax.tools.JavaFileObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

/**
 * Created by afitzgerald on 8/29/14.
 */
@RunWith(Parameterized.class)
public class GoldenFilesTest {

    private final SourceTestCase testCase;

    @Parameters(name = "{index}: {1}")
    public static Collection<Object[]> buildTestCases(){
        Gson gson = new Gson();

        List<SourceTestCase> cases = gson.fromJson(
                new BufferedReader(new InputStreamReader(SourceTestCase.class.getResourceAsStream("/test_cases.json"))),
                new TypeToken<List<SourceTestCase>>() {}.getType());

        LinkedList<Object[]> temp = new LinkedList<Object[]>();
        for ( SourceTestCase singleCase: cases ) {
            Object[] temp2 = new Object[2];
            temp2[0] = singleCase;
            temp2[1] = singleCase.name;
            temp.add(temp2);
        }

        return temp;
    }

    public GoldenFilesTest( SourceTestCase testCase, String name){
        this.testCase = testCase;
    }

    @Test
    public void runTestCase(){

        try {
            JavaFileObject source = getSource(testCase.initialSource);

            List<JavaFileObject> expected = new LinkedList<JavaFileObject>();

            for ( String path: testCase.generatedSources ){
                expected.add(getSource(path));
            }

            ASSERT.about(javaSource())
                    .that(source)
                    .processedWith(new TaskProcessor())
                    .compilesWithoutError()
                    .and()
                    .generatesSources(expected.remove(0), expected.toArray(new JavaFileObject[expected.size()]));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private JavaFileObject getSource(String path) throws IOException {
        return JavaFileObjects.forSourceString(path.substring(1).replace(".java", ""), readResource(path));
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

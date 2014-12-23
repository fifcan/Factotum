package test;

import net.riotopsys.factotum.api.annotation.Task;
import java.util.List;

public class Templated {

    @Task()
    public List<String> templated(String stuff) {
        return null;
    }

}
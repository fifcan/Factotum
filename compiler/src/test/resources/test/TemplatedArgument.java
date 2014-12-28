package test;

import net.riotopsys.factotum.api.annotation.Task;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class TemplatedArgument {

    @Task()
    public List<String> templatedArg(List<String> bob) {
        return null;
    }

    @Task
    public Set<? extends Comparator<String>> templatedArgMark2(List<Set<? extends Comparator<String>>> bob) {
        return null;
    }

}
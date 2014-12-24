package test;

import net.riotopsys.factotum.api.annotation.Task;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Nested {

    @Task()
    public List<Map<String, Set<Integer> > > nested(String stuff) {
        return null;
    }

}
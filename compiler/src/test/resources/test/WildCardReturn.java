package test;

import net.riotopsys.factotum.api.annotation.Task;
import java.util.Set;
import java.util.Comparator;

public class WildCardReturn {

    @Task()
    public Set<? extends Comparator<String>> wildcard(String stuff) {
        return null;
    }

}
package test;

import net.riotopsys.factotum.api.annotation.Task;

public class SimpleTask {

    @Task()
    public String specialTaskHandler(String stuff) {
        return null;
    }

}
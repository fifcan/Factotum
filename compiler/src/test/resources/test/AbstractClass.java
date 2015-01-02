package test;

import net.riotopsys.factotum.api.annotation.Task;

public abstract class AbstractClass {

    @Task()
    public String specialTaskHandler(String stuff) {
        return null;
    }

}
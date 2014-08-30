package test;

import net.riotopsys.factotum.api.annotation.Task;

public class MultipuleTasks {

    @Task()
    public String taskA(String stuff) {
        return null;
    }

    @Task()
    public Integer taskB(Float stuff) {
        return null;
    }

    @Task()
    public MultipuleTasks taskC(Object stuff) {
        return null;
    }

}
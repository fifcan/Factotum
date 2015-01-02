package test;

import net.riotopsys.factotum.api.annotation.Task;

public interface AnnotatedInterface {

    @Task
    public int task(String bob);

}
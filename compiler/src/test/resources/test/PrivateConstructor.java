package test;

import net.riotopsys.factotum.api.annotation.Task;

public class PrivateConstructor {

    private PrivateConstructor(){
    }

    @Task
    public int task(String bob);

}
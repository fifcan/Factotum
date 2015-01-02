package test;

import net.riotopsys.factotum.api.annotation.Task;

public class ArgumentedConstructor {

    public ArgumentedConstructor(int stuff){
    }

    @Task
    public int task(String bob);

}
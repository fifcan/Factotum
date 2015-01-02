package test;

import net.riotopsys.factotum.api.annotation.Task;

public class TemplatedClass<T> {

    @Task()
    public T bob(T stuff) {
        return null;
    }

}
package net.riotopsys.factotum.api.customize;

/**
 * Created by afitzgerald on 8/29/14.
 */
public interface ITaskHandlerFactory {

    public Object getHandler( Class clazz );

}

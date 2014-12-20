package net.riotopsys.factotum.api.customize;

import net.riotopsys.factotum.api.concurent.ResultWrapper;

/**
 * Created by afitzgerald on 12/7/14.
 */
public interface IOnTaskCompletionCallback {

    public void OnTaskCompletion(ResultWrapper object);
}

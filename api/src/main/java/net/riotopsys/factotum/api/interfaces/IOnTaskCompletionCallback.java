package net.riotopsys.factotum.api.interfaces;

import net.riotopsys.factotum.api.internal.ResultWrapper;

/**
 * Created by afitzgerald on 12/7/14.
 */
public interface IOnTaskCompletionCallback {

    void onTaskCompletion(ResultWrapper object);
}

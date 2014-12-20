package net.riotopsys.factotum.api.concurent;

import net.riotopsys.factotum.api.AbstractRequest;

/**
 * Created by afitzgerald on 12/16/14.
 */
public class ResultWrapper {

    public final AbstractRequest request;
    public final Object result;

    public ResultWrapper(AbstractRequest request, Object result) {
        this.request = request;
        this.result = result;
    }

}

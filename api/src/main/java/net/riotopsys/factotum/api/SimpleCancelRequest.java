package net.riotopsys.factotum.api;

import net.riotopsys.factotum.api.customize.ICancelRequest;

/**
 * Created by afitzgerald on 8/29/14.
 */
public class SimpleCancelRequest implements ICancelRequest {


    private final Object template;

    public SimpleCancelRequest( Object object ){
        template = object;
    }

    @Override
    public boolean match(Object object) {
        return template.equals(object);
    }
}

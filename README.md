# Factotum

[![Build Status](https://travis-ci.org/riotopsys/Factotum.svg?branch=master)](https://travis-ci.org/riotopsys/Factotum)

## Overview
An asynchronous task queue for Java based on the [command pattern][command]. Utilizing code generation to reduce boilerplate class definitions.

## Using Factotum

### Defining Tasks and Requests

Create a non-abstract class with a default zero argument constructor. Within this class create a method that contains the code that will be run asynchronously.

```
package com.example;

import net.riotopsys.factotum.api.annotation.Task;

public class SimpleTasks {

    @Task
    public Long delay(Long millis) throws InterruptedException {
        Thread.sleep(millis);
        return millis;
    }
    
}
``` 

The annotation processor will automatically generate a request class `com.example.DelayRequest` from the annotated delay method above. The constructor for a request will always match the arguments of the method it was created for.

### Using Request Objects

The simplest usage is to new up a request and add it to a factotum instance. This will run the task but will not return any values.

```
Factotum factotum = new Factotum.Builder()

factotum.addRequest(new DelayRequest(1000));
```

Callbacks can be added directly to a request. All callbacks are kept as week references to ease memory leak concerns. 

```
ICallback<Long> callback = new ICallback<> {

    void onSuccess(final AbstractRequest request, final Long result){
    	//do something
    }

    void onFailure(final AbstractRequest request, final Object error){
    	//handle error
    }

}

factotum.addRequest(new DelayRequest(1000).setCallback(callback));
```
### Canceling and Grouping Requests 

All requests can be canceled by calling the cancel method on them. 

```
Delay request = new DelayRequest(1000)

factotum.addRequest(request);

request.cancel()
```

A series of requests can be given a group, later this group may be used to cancel requests en masse.

```
factotum.addRequest(new DelayRequest(1000).setGroup("delays"));
factotum.addRequest(new DelayRequest(5000).setGroup("delays"));
factotum.addRequest(new DelayRequest(10000).setGroup("delays"));
factotum.addRequest(new DelayRequest(50000).setGroup("delays"));

factotum.issueCancellation(new SimpleCancelRequest("delays"))
```
## Android: Receiving Callbacks on the UI thread

Factotum delivers all callbacks on the same thread that processed the task. If you need the callback on the UI thread an adapted callback object can be used. This is an example of one way this can be achieved.

```
public abstract class HandlerCallback<T> implements ICallback<T> {
    public abstract void onSuccessInHandler(AbstractRequest abstractRequest, T result);
    public abstract void onFailureInHandler(AbstractRequest abstractRequest, Object error);

    private Handler handler;

    public HandlerCallback(Handler handler){
        this.handler = handler;
    }

    @Override
    public void onSuccess(final AbstractRequest abstractRequest, final T o) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onSuccessInHandler(abstractRequest, o);
            }
        });
    }

    @Override
    public void onFailure(final AbstractRequest abstractRequest, final Object o) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                onFailureInHandler(abstractRequest, o);
            }
        });
    }
}
```


## Dependency Injection

Dependency Injection can be used with Factotum by adding a `IOnTaskCreationCallback` when building a Factotum instance. This is an example using [Dagger][dagger].

### Factotum Building with DI
```
CoreModule coreModule = new CoreModule();
objectGraph = ObjectGraph.create(coreModule);

Factotum factotum = new Factotum.Builder()
                .setOnTaskCreationCallback(new DaggerTaskInjector(objectGraph))
                .build();
```

### Concrete IOnTaskCreationCallback 
```
public class DaggerTaskInjector implements IOnTaskCreationCallback {

    private final ObjectGraph objectGraph;

    public DaggerTaskInjector( ObjectGraph objectGraph ){
        this.objectGraph = objectGraph;
    }

    @Override
    public void onTaskCreation(Object task) {
        objectGraph.inject(task);
    }
}
```

## Download
Factotum is avaliable on maven central.

### Gradle
```
compile 'net.riotopsys.factotum:factotum:0.0.0'
compile 'net.riotopsys.factotum:factotum-compiler:0.0.0'
```

###Maven
```
<dependency>
    <groupId>net.riotopsys.factotum</groupId>
    <artifactId>factotum</artifactId>
    <version>0.0.0</version>
</dependency>
<dependency>
    <groupId>net.riotopsys.factotum</groupId>
    <artifactId>factotum-compiler</artifactId>
    <version>0.0.0</version>
</dependency>
```

## Contributing

If you want to contribute, just fork and submit a pull request!

## License
```
Copyright 2015 C. A. Fitzgerald

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
```



[command]: http://en.wikipedia.org/wiki/Command_pattern
[dagger]: http://square.github.io/dagger/
package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public abstract class GetTask<T, V> extends SetTask<T> {

    private V returnObject;

    public GetTask(T inputObject, AuthToken authToken, Handler messageHandler) {
        super(inputObject, authToken, messageHandler);
    }

    public void setReturnObject(V returnObject) {
        this.returnObject = returnObject;
    }

    public V getReturnObject() {
        return returnObject;
    }
}

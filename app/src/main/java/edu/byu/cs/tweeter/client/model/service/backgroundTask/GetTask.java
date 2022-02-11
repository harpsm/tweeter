package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import java.io.Serializable;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public abstract class GetTask<T, V extends Serializable> extends SetTask<T> {

    public static final String RETURN_OBJECT_KEY = "return-object";

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

    @Override
    protected void addBundleData(Bundle msgBundle) {
        msgBundle.putSerializable(RETURN_OBJECT_KEY, getReturnObject());
    }
}

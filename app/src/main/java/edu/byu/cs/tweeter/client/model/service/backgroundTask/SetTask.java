package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public abstract class SetTask<T> extends AuthorizedTask {

    private T inputObject;

    public SetTask(T inputObject, AuthToken authToken, Handler messageHandler) {
        super(authToken, messageHandler);
        this.inputObject = inputObject;
    }

    public T getInputObject() {
        return inputObject;
    }

    public void setInputObject(T inputObject) {
        this.inputObject = inputObject;
    }
}

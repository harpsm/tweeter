package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * Background task that posts a new status sent by a user.
 */
public class PostStatusTask extends SetTask<Status> {

    public PostStatusTask(AuthToken authToken, Status status, Handler messageHandler) {
        super(status, authToken, messageHandler);
        setLogTag("PostStatusTask");
    }

    @Override
    protected void talkToServer() {
        getInputObject();
    }

    @Override
    protected void addBundleData(Bundle msgBundle) {}

}

package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that removes a following relationship between two users.
 */
public class UnfollowTask extends SetTask<User> {

    public UnfollowTask(AuthToken authToken, User followee, Handler messageHandler) {
        super(followee, authToken, messageHandler);
        setLogTag("UnfollowTask");
    }

    @Override
    protected void talkToServer() {
        getInputObject();
    }

    @Override
    protected void addBundleData(Bundle msgBundle) {}

}

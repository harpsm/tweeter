package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Bundle;
import android.os.Handler;

import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * Background task that returns the profile for a specified user.
 */
public class GetUserTask extends GetTask<String, User> {
    public GetUserTask(AuthToken authToken, String alias, Handler messageHandler) {
        super(alias, authToken, messageHandler);
        setLogTag("GetUserTask");
    }

    @Override
    protected void talkToServer() {
        setReturnObject(getUser());
    }

    private User getUser() {
        User user = getFakeData().findUserByAlias(getInputObject());
        return user;
    }
}

package edu.byu.cs.tweeter.client.model.service;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.Task;
import edu.byu.cs.tweeter.client.presenter.FeedPresenter;
import edu.byu.cs.tweeter.client.presenter.FollowersPresenter;
import edu.byu.cs.tweeter.client.presenter.FollowingPresenter;
import edu.byu.cs.tweeter.client.presenter.LoginPresenter;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.presenter.StoryPresenter;
import edu.byu.cs.tweeter.client.view.main.MainActivity;
import edu.byu.cs.tweeter.client.view.main.feed.FeedFragment;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class UserService extends Service {

    public interface GetUserObserver extends GetObjectObserver<User> {}
    public interface LoginObserver extends GetObjectObserver<User> {}
    public interface RegisterObserver extends GetObjectObserver<User> {}
    public interface LogoutObserver extends SetObjectObserver {}


    //GETUSERTASK
    public void getUser(AuthToken currUserAuthToken, String userAlias, GetUserObserver getUserObserver) {
        GetUserTask getUserTask = new GetUserTask(currUserAuthToken, userAlias, new GetUserHandler(getUserObserver));
        executeTask(getUserTask);
    }
    //GetUserHandler
    private class GetUserHandler extends GetObjectHandler<User> {
        public GetUserHandler(GetUserObserver observer) {
            super(observer);
        }
    }

    //LOGINTASK
    public void login(String alias, String password, LoginPresenter.LoginObserver loginObserver) {
        // Send the login request.
        LoginTask loginTask = new LoginTask(alias, password,
                new LoginHandler(loginObserver));
        executeTask(loginTask);
    }
    public User logUserIn(Message msg) {
        User loggedInUser = (User) msg.getData().getSerializable(LoginTask.USER_KEY);
        AuthToken authToken = (AuthToken) msg.getData().getSerializable(LoginTask.AUTH_TOKEN_KEY);
        // Cache user session information
        Cache.getInstance().setCurrUser(loggedInUser);
        Cache.getInstance().setCurrUserAuthToken(authToken);
        return loggedInUser;
    }
    //LoginHandler
    private class LoginHandler extends GetObjectHandler<User> {
        public LoginHandler(LoginObserver observer) {
            super(observer);
        }
        @Override
        protected void handlerHandleSuccess(Message msg) {
            returnObject = logUserIn(msg);
            getObjectObserver.handleSuccess(returnObject);
        }
    }


    //REGISTERTASK
    public void register(String firstName, String lastName, String alias,
                         String password, String imageBytesBase64, RegisterObserver registerObserver) {
        // Send register request.
        RegisterTask registerTask = new RegisterTask(firstName, lastName, alias, password,
                imageBytesBase64, new RegisterHandler(registerObserver));
        executeTask(registerTask);
    }
    // RegisterHandler
    private class RegisterHandler extends GetObjectHandler<User> {
        public RegisterHandler(RegisterObserver observer) {
            super(observer);
        }
        @Override
        protected void handlerHandleSuccess(Message msg) {
            returnObject = logUserIn(msg);
            getObjectObserver.handleSuccess(returnObject);
        }
    }


    //LOGOUTTASK
    public void logout(AuthToken currUserAuthToken, MainPresenter.LogoutObserver logoutObserver) {
        LogoutTask logoutTask = new LogoutTask(currUserAuthToken, new LogoutHandler(logoutObserver));
        executeTask(logoutTask);
    }
    // LogoutHandler
    private class LogoutHandler extends SetObjectHandler {
        public LogoutHandler(LogoutObserver observer) { super(observer); }
    }
}

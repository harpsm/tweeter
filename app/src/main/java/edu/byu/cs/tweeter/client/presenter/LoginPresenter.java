package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter extends SignInPresenter {

    public interface LoginView extends SignInView {}

    public LoginPresenter(LoginView view) {
        super(view);
    }

    public void login(String alias, String password) {
        Cache.getInstance().getUserService().login(alias, password, new LoginObserver());
    }

    public class LoginObserver implements UserService.LoginObserver {
        @Override
        public void handleSuccess(User loggedInUser) {
            view.loginSuccessful(loggedInUser);
        }
        @Override
        public void handleFailure(String message) { view.displayMessage("Failed to login: " + message); }
        @Override
        public void handleException(Exception exception) { view.displayMessage("Failed to login because of exception: " + exception.getMessage()); }
    }
}

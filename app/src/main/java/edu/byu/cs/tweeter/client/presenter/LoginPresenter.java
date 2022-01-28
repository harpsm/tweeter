package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class LoginPresenter {

    public interface View {
        void loginSuccessful(User loggedInUser);
        void displayMessage(String message);
    }

    private View view;
    private UserService userService;

    public LoginPresenter(View view) {
        this.view = view;
        userService = new UserService();
    }



    public void login(String alias, String password) {
        userService.login(alias, password, new LoginObserver());
    }

    public class LoginObserver implements UserService.LoginObserver {

        @Override
        public void handleSuccess(User loggedInUser) {
            view.loginSuccessful(loggedInUser);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to login: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to login because of exception: " + exception.getMessage());
        }
    }
}

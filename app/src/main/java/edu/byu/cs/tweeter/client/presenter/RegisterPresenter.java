package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter {

    public interface View {
        void registrationSuccessful(User registeredUser);
        void displayMessage(String message);
    }

    public RegisterPresenter(View view) {
        this.view = view;
        userService = new UserService();
    }

    private View view;
    private UserService userService;

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64) {
        userService.register(firstName, lastName, alias, password, imageBytesBase64, new RegisterObserver());
    }

    public class RegisterObserver implements UserService.RegisterObserver {

        @Override
        public void handleSuccess(User registeredUser) {
            view.registrationSuccessful(registeredUser);
        }

        @Override
        public void handleFailure(String message) {
            view.displayMessage("Failed to register: " + message);
        }

        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to register because of exception: " + exception.getMessage());
        }
    }
}

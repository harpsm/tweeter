package edu.byu.cs.tweeter.client.presenter;

import android.graphics.drawable.Drawable;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.User;

public class RegisterPresenter extends SignInPresenter {

    public interface RegisterView extends SignInView {}

    public RegisterPresenter(RegisterView view) {
        super(view);
    }

    public void register(String firstName, String lastName, String alias, String password, String imageBytesBase64) {
        Cache.getInstance().getUserService().register(firstName, lastName, alias, password, imageBytesBase64, new RegisterObserver());
    }

    public class RegisterObserver implements UserService.RegisterObserver {
        @Override
        public void handleSuccess(User registeredUser) { view.loginSuccessful(registeredUser); }
        @Override
        public void handleFailure(String message) { view.displayMessage("Failed to register: " + message); }
        @Override
        public void handleException(Exception exception) { view.displayMessage("Failed to register because of exception: " + exception.getMessage()); }
    }

    public void validateRegistration(String firstName, String lastName, String alias, String password, Drawable imageToUpload) {
        if (firstName.length() == 0) {
            throw new IllegalArgumentException("First Name cannot be empty.");
        }
        if (lastName.length() == 0) {
            throw new IllegalArgumentException("Last Name cannot be empty.");
        }
        if (alias.length() == 0) {
            throw new IllegalArgumentException("Alias cannot be empty.");
        }
        if (alias.charAt(0) != '@') {
            throw new IllegalArgumentException("Alias must begin with @.");
        }
        if (alias.length() < 2) {
            throw new IllegalArgumentException("Alias must contain 1 or more characters after the @.");
        }
        if (password.length() == 0) {
            throw new IllegalArgumentException("Password cannot be empty.");
        }
        if (imageToUpload == null) {
            throw new IllegalArgumentException("Profile image must be uploaded.");
        }
    }
}

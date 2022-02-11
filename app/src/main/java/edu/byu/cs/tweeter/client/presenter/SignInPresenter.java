package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public abstract class SignInPresenter extends Presenter {

    public interface SignInView extends View {
        void loginSuccessful(User loggedInUser);
    }

    protected SignInView view;

    public SignInPresenter(SignInView view) {
        super(view);
        try {
            this.view = (SignInView) view;
        }
        catch (Exception e) {
            view.displayMessage("Failed to login because of exception: " + e.getMessage());
        }
    }

}

package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.model.domain.User;

public class Presenter {
    public interface View {
        void displayMessage(String message);
    }

    protected View view;

    public Presenter(View view) {
        this.view = view;
    }
}

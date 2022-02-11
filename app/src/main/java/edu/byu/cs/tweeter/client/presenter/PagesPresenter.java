package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.Service;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagesPresenter<T> extends Presenter {

    protected static final int PAGE_SIZE = 10;

    public interface PagesView<T> extends View {
        void setLoadingFooterStatus(boolean activated);
        void addObjects(List<T> pageOfObjects);
        void selectNewUser(User user);
    }

    protected PagesView<T> view;
    private T lastObject;
    private boolean hasMorePages;
    private boolean isLoading = false;


    public T getLastObject() {
        return lastObject;
    }

    public boolean hasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public PagesPresenter(PagesView<T> view) {
        super(view);
        this.view = view;
    }


    public void getUser(String userAlias) {
        Cache.getInstance().getUserService().getUser(Cache.getInstance().getCurrUserAuthToken(), userAlias, new GetUserObserver());
    }

    public class GetUserObserver implements UserService.GetUserObserver {
        @Override
        public void handleSuccess(User user) {
            view.selectNewUser(user);
        }
        @Override
        public void handleFailure(String message) { view.displayMessage("Failed to get user's profile: " + message); }
        @Override
        public void handleException(Exception exception) {
            view.displayMessage("Failed to get user's profile because of exception: " + exception.getMessage());
        }
    }

    public void loadMoreItems(User user) {
        if (!isLoading) {   // This guard is important for avoiding a race condition in the scrolling code.
            isLoading = true;
            view.setLoadingFooterStatus(true);

            getItemsFromServer(user);
        }
    }

    public class GetPagesObserver implements Service.GetPagesObserver<T> {

        @Override
        public void handleSuccess(List<T> objectList, boolean hasMorePages) {
            isLoading = false;
            view.setLoadingFooterStatus(false);
            setHasMorePages(hasMorePages);
            lastObject = (objectList.size() > 0) ? objectList.get(objectList.size() - 1) : null;
            view.addObjects(objectList);
        }
        @Override
        public void handleFailure(String message) {
            isLoading = false;
            view.setLoadingFooterStatus(false);
            view.displayMessage("Failed to get feed: " + message);
        }
        @Override
        public void handleException(Exception exception) {
            isLoading = false;
            view.setLoadingFooterStatus(false);
            view.displayMessage("Failed to get feed because of exception: " + exception.getMessage());
        }
    }

    protected abstract void getItemsFromServer(User user);
}

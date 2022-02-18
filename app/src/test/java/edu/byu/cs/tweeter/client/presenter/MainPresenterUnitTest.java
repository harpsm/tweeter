package edu.byu.cs.tweeter.client.presenter;

import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.text.ParseException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenterUnitTest {

    private MainPresenter.MainView mockView;
    private UserService mockUserService;
    private StatusService mockStatusService;
    private Cache mockCache;

    //private Status mockStatus;

    private MainPresenter mainPresenterSpy;

    @Before
    public void setup() {
        //Create Mocks
        mockView = Mockito.mock(MainPresenter.MainView.class);
        mockUserService = Mockito.mock(UserService.class);
        mockStatusService = Mockito.mock(StatusService.class);
        mockCache = Mockito.mock(Cache.class);
        Mockito.when(mockCache.getUserService()).thenReturn(mockUserService);
        Mockito.when(mockCache.getStatusService()).thenReturn(mockStatusService);
        mockCache.setCurrUser(new User("Bob", "Smith", ""));

        mainPresenterSpy = Mockito.spy(new MainPresenter(mockView));

        Cache.setInstance(mockCache);
    }

    //logout()
    @Test
    public void testLogout_logoutSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //Function Call with arguments:
                //.logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver(LOGOUT_DESCRIPTION))
                MainPresenter.LogoutObserver observer = invocation.getArgument(1, MainPresenter.LogoutObserver.class);
                observer.handleSuccess();
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockUserService).logout(Mockito.any(), Mockito.any());

        mainPresenterSpy.logout();
        Mockito.verify(mockView).displayMessage("Logging Out...");
        Mockito.verify(mockCache).clearCache();
        Mockito.verify(mockView).logoutSuccessful();
    }
    @Test
    public void testLogout_logoutFailedWithMessage() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //Function Call with arguments:
                //.logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver(LOGOUT_DESCRIPTION))
                MainPresenter.LogoutObserver observer = invocation.getArgument(1, MainPresenter.LogoutObserver.class);
                observer.handleFailure("the error message");
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockUserService).logout(Mockito.any(), Mockito.any());

        mainPresenterSpy.logout();
        Mockito.verify(mockView).displayMessage("Logging Out...");
        Mockito.verify(mockCache, Mockito.times(0)).clearCache();
        Mockito.verify(mockView).displayMessage("Failed to " + MainPresenter.LOGOUT_DESCRIPTION + ": the error message");
    }
    @Test
    public void testLogout_logoutFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //Function Call with arguments:
                //.logout(Cache.getInstance().getCurrUserAuthToken(), new LogoutObserver(LOGOUT_DESCRIPTION))
                MainPresenter.LogoutObserver observer = invocation.getArgument(1, MainPresenter.LogoutObserver.class);
                observer.handleException(new Exception("the exception message"));
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockUserService).logout(Mockito.any(), Mockito.any());

        mainPresenterSpy.logout();
        Mockito.verify(mockView).displayMessage("Logging Out...");
        Mockito.verify(mockCache, Mockito.times(0)).clearCache();
        Mockito.verify(mockView).displayMessage("Failed to " + MainPresenter.LOGOUT_DESCRIPTION + " because of exception: the exception message");
    }

    //postStatus()
    @Test
    public void testPostStatus_createNewStatusSuccessful() {
        String post = "this is a post";

        //call postStatus(post) and ensure all parameters are correctly called
        mainPresenterSpy.postStatus(post);
        //create valid Status object
        Mockito.verify(mockCache).getCurrUser();
        try { Mockito.verify(mainPresenterSpy).getFormattedDateTime(); }
        catch (Exception e) { fail(); }
        Mockito.verify(mainPresenterSpy).parseURLs(post);
        Mockito.verify(mainPresenterSpy).parseMentions(post);
        //retrieve AuthToken
        Mockito.verify(mockCache).getCurrUserAuthToken();
        //test for a valid observer in later tests by testing the observer's responses
    }
    @Test
    public void testPostStatus_createNewStatusFailedWithException() {
        //This tests whether the code successfully handles a ParseException thrown during
        //the MainPresenter.getFormattedDateTime() function
        Answer<Void> answer = new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                throw new ParseException("the exception message", 0);
            }
        };
        try { Mockito.when(mainPresenterSpy.getFormattedDateTime()).thenAnswer(answer); }
        catch (Exception ex) { fail(); }

        String post = "this is a post";

        //call postStatus(post) and test all function calls
        mainPresenterSpy.postStatus(post);

        Mockito.verify(mockView).displayMessage("Failed to " + MainPresenter.POST_STATUS_DESCRIPTION + " because of exception: the exception message");
    }
    @Test
    public void testPostStatus_postSuccessful() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //Function Call with arguments:
                //.postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver(POST_STATUS_DESCRIPTION))
                MainPresenter.PostStatusObserver observer = invocation.getArgument(2, MainPresenter.PostStatusObserver.class);
                observer.handleSuccess();
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());
        String post = "this is a post";

        mainPresenterSpy.postStatus(post);

        Mockito.verify(mockView).statusPostedSuccessful();
    }
    @Test
    public void testPostStatus_postFailedWithMessage() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //Function Call with arguments:
                //.postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver(POST_STATUS_DESCRIPTION))
                MainPresenter.PostStatusObserver observer = invocation.getArgument(2, MainPresenter.PostStatusObserver.class);
                observer.handleFailure("the error message");
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());
        String post = "this is a post";

        mainPresenterSpy.postStatus(post);

        Mockito.verify(mockView, Mockito.times(0)).statusPostedSuccessful();
        Mockito.verify(mockView).displayMessage("Failed to " + MainPresenter.POST_STATUS_DESCRIPTION + ": the error message");
    }
    @Test
    public void testPostStatus_postFailedWithException() {
        Answer<Void> answer = new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                //Function Call with arguments:
                //.postStatus(Cache.getInstance().getCurrUserAuthToken(), newStatus, new PostStatusObserver(POST_STATUS_DESCRIPTION))
                MainPresenter.PostStatusObserver observer = invocation.getArgument(2, MainPresenter.PostStatusObserver.class);
                observer.handleException(new Exception("the exception message"));
                return null;
            }
        };
        Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any(), Mockito.any());
        String post = "this is a post";

        mainPresenterSpy.postStatus(post);

        Mockito.verify(mockView, Mockito.times(0)).statusPostedSuccessful();
        Mockito.verify(mockView).displayMessage("Failed to " + MainPresenter.POST_STATUS_DESCRIPTION + " because of exception: the exception message");
    }
}

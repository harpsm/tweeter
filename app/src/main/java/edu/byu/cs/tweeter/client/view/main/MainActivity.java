package edu.byu.cs.tweeter.client.view.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.byu.cs.client.R;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowingCountTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.client.view.login.LoginActivity;
import edu.byu.cs.tweeter.client.view.login.StatusDialogFragment;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

/**
 * The main activity for the application. Contains tabs for feed, story, following, and followers.
 */
public class MainActivity extends AppCompatActivity implements StatusDialogFragment.Observer {

    public static final String CURRENT_USER_KEY = "CurrentUser";

    private Toast logOutToast;
    private Toast postingToast;
    private TextView followeeCount;
    private TextView followerCount;
    private Button followButton;

    private MainPresenter presenter;
    private MainPresenterView presenterView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        presenterView = new MainPresenterView();
        presenter = new MainPresenter(presenterView);

        presenter.setSelectedUser((User) getIntent().getSerializableExtra(CURRENT_USER_KEY));
        if (presenter.getSelectedUser() == null) {
            throw new RuntimeException("User not passed to activity");
        }

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager(), presenter.getSelectedUser());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StatusDialogFragment statusDialogFragment = new StatusDialogFragment();
                statusDialogFragment.show(getSupportFragmentManager(), "post-status-dialog");
            }
        });

        presenter.updateSelectedUserFollowingAndFollowers();

        TextView userName = findViewById(R.id.userName);
        userName.setText(presenter.getSelectedUser().getName());

        TextView userAlias = findViewById(R.id.userAlias);
        userAlias.setText(presenter.getSelectedUser().getAlias());

        ImageView userImageView = findViewById(R.id.userImage);
        Picasso.get().load(presenter.getSelectedUser().getImageUrl()).into(userImageView);

        followeeCount = findViewById(R.id.followeeCount);
        followeeCount.setText(getString(R.string.followeeCount, "..."));

        followerCount = findViewById(R.id.followerCount);
        followerCount.setText(getString(R.string.followerCount, "..."));

        followButton = findViewById(R.id.followButton);

        if (presenter.getSelectedUser().compareTo(Cache.getInstance().getCurrUser()) == 0) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);
            presenter.isFollower();
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followButton.setEnabled(false);

                if (followButton.getText().toString().equals(v.getContext().getString(R.string.following))) {
                    presenter.unfollow();
                    Toast.makeText(MainActivity.this, "Removing " + presenter.getSelectedUser().getName() + "...", Toast.LENGTH_LONG).show();
                } else {
                    presenter.follow();
                    Toast.makeText(MainActivity.this, "Adding " + presenter.getSelectedUser().getName() + "...", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public class MainPresenterView implements MainPresenter.View {

        @Override
        public void updateFollowButton(boolean isFollower) {
            // If logged in user if a follower of the selected user, display the follow button as "following"
            if (isFollower) {
                followButton.setText(R.string.following);
                followButton.setBackgroundColor(getResources().getColor(R.color.white));
                followButton.setTextColor(getResources().getColor(R.color.lightGray));
            } else {
                followButton.setText(R.string.follow);
                followButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));
            }
        }

        @Override
        public void displayMessage(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        }

        @Override
        public void unfollowedUser() {
            updateFollowButton(false);
        }

        @Override
        public void enableFollowButton() {
            followButton.setEnabled(true);
        }

        @Override
        public void followedUser() {
            updateFollowButton(true);
        }

        @Override
        public void logoutSuccessful() {
            logOutToast.cancel();
            resetMainActivity();
        }

        @Override
        public void statusPostedSuccessful() {
            postingToast.cancel();
            Toast.makeText(MainActivity.this, "Successfully Posted!", Toast.LENGTH_LONG).show();
        }

        @Override
        public void updateFollowerCount(int followersCount) {
            followerCount.setText(getString(R.string.followerCount, String.valueOf(followersCount)));
        }

        @Override
        public void updateFollowingCount(int followingCount) {
            followeeCount.setText(getString(R.string.followeeCount, String.valueOf(followingCount)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logoutMenu) {
            logOutToast = Toast.makeText(this, "Logging Out...", Toast.LENGTH_LONG);
            logOutToast.show();

            presenter.logout();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public void resetMainActivity() {
        //Revert to login screen.
        Intent intent = new Intent(this, LoginActivity.class);
        //Clear everything so that the main activity is recreated with the login page.
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onStatusPosted(String post) {
        postingToast = Toast.makeText(this, "Posting Status...", Toast.LENGTH_LONG);
        postingToast.show();

        presenter.postStatus(post);
    }
}

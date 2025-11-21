package info.androidhive.navigationdrawer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

// ✅ FIXED: AndroidX Imports
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.varunest.sparkbutton.SparkButton;
import com.varunest.sparkbutton.SparkEventListener;

import info.androidhive.navigationdrawer.R;

public class ChatStartActivity extends AppCompatActivity {

    private static final String APP_ID = "40511F56-F9AB-4806-83D9-7A4EA99698CB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_start);

        SparkButton star = findViewById(R.id.star_button);

        SendBird.init(APP_ID, this.getApplicationContext());

        star.setEventListener(new SparkEventListener() {
            @Override
            public void onEvent(ImageView button, boolean buttonState) {
                if (buttonState)
                {
                    //code when star is active
                    String userId = "test";
                    userId = userId.replaceAll("\\s", "");
                    String userNickname = "Admin";

                    connectToSendBird(userId, userNickname);

                } else
                {
                    //code when star is inactive
                }
            }

            @Override
            public void onEventAnimationEnd(ImageView button, boolean buttonState) {

            }

            @Override
            public void onEventAnimationStart(ImageView button, boolean buttonState) {

            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    private void connectToSendBird(final String userId, final String userNickname) {

        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {

                if (e != null) {
                    // Error!
                    Toast.makeText(
                                    ChatStartActivity.this, "" + e.getCode() + ": " + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();

                    // Show login failure snackbar
                    return;
                }

                // Update the user's nickname
                updateCurrentUserInfo(userNickname);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(ChatStartActivity.this, ChatActivity.class);
                        startActivity(i);
                    }
                }, 1050);

            }
        });
    }

    private void updateCurrentUserInfo(String userNickname) {
        SendBird.updateCurrentUserInfo(userNickname, null, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    // Error!
                    Toast.makeText(
                                    ChatStartActivity.this, "" + e.getCode() + ":" + e.getMessage(),
                                    Toast.LENGTH_SHORT)
                            .show();

                    return;
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
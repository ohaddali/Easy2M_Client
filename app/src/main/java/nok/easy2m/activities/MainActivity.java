package nok.easy2m.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.microsoft.windowsazure.notifications.NotificationsManager;

import nok.easy2m.*;
import nok.easy2m.R;
import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.communityLayer.HttpConnection;
import nok.easy2m.models.Services;
import nok.easy2m.models.User;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button loginBtn;
    Button registerBtn;
    TextView usernameText;
    TextView passwordText;
    HttpConnection httpConnection;
    private static final String TAG = "MainActivity";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Uri uri = getIntent().getData(); //get URI
        if(uri != null) //if we come from URI
        {
            String key = "";
            String parameter = uri.getQueryParameter(key); //get query parameter from URI s.t. get X for query ?X=Value
        }*/

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginBtn.setOnClickListener(this);

        pref = getSharedPreferences("label",0);

        Intent intent = new Intent(this , AddWorkersActivity.class);
        startActivity(intent);
        NotificationsManager.handleNotifications(this, NotificationSettings.SenderId, MyHandler.class);
    }

    @Override
    public void onClick(View view)
    {
        httpConnection = HttpConnection.getInstance(getApplicationContext());
        if(view.getId() == loginBtn.getId()) {
            String username = usernameText.getText().toString();
            String password = usernameText.getText().toString();
            Activity activity = this;
            CallBack<User> responseCallBack = (user) ->
            {
                if (user.isLoggedIn()) {
                  
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putLong("userId" , user.getId());
                    editor.putBoolean("admin" , user.isAdmin());
                    editor.commit();
                    activity.runOnUiThread(() -> registerWithNotificationHubs());
                    Intent intent = new Intent(activity , companiesListActivity.class);
                    activity.runOnUiThread(() -> startActivity(intent));

                } else {
                    activity.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "username or password is incorrect", Toast.LENGTH_LONG).show());
                }
            };
            login(username, password, responseCallBack);
        }
    }
    private void login(String username , String password, CallBack<User> responseCallBack)
    {
        Pair <String , Object> pair1 = new Pair<>("userName",username);
        Pair <String , Object> pair2 = new Pair<>("password",password);
        httpConnection.send(Services.auth,"login",responseCallBack , User.class , null , pair1, pair2);
    }

    public void registerWithNotificationHubs()
    {
        if (checkPlayServices()) {
            // Start IntentService to register this application with FCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported by Google Play Services.");
                finish();
            }
            return false;
        }
        return true;
    }


}

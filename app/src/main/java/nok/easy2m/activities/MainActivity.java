package nok.easy2m.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;


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
    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginBtn.setOnClickListener(this);
        pref = getSharedPreferences("label" , 0);


        Intent i = getIntent();
        String str = i.getStringExtra("username");
        if(str != null)
            usernameText.setText(str);
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
                    /*if (user.isAdmin()) {
                        Toast.makeText(getApplicationContext(), "Admin Success", Toast.LENGTH_LONG);
                    } else {
                        Toast.makeText(getApplicationContext(), "Worker Success", Toast.LENGTH_LONG);
                    }*/
                    Intent intent = new Intent(activity , companiesListActivity.class);
                    activity.runOnUiThread(() -> startActivity(intent));
                } else {
                    activity.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "username or password is incorrect", Toast.LENGTH_LONG).show());
                }
            };
            login(username, password, responseCallBack);
        }
        else if(view.getId() == registerBtn.getId())
        {
            Intent regIntent = new Intent(this,RegisterActivity.class);
            regIntent.putExtra("adminRegister",true);
            startActivity(regIntent);
            finish();
        }
    }


    private void login(String username , String password, CallBack<User> responseCallBack)
    {
        Pair <String , Object> pair1 = new Pair<>("userName",username);
        Pair <String , Object> pair2 = new Pair<>("password",password);
        httpConnection.send(Services.auth,"login",responseCallBack , User.class , null , pair1, pair2);
    }


}

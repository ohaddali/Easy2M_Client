package nok.easy2m.activities;

import android.app.Activity;
import android.content.Intent;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginBtn.setOnClickListener(this);

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
            CallBack<JSONObject> responseCallBack = (JSONObject) ->
            {

                User user = new User();
                user.fromJSONObject(JSONObject);

                if (user.isLoggedIn()) {
                    if (user.isAdmin()) {

                        Toast.makeText(getApplicationContext(), "Admin Success", Toast.LENGTH_LONG);
                    } else {
                        Toast.makeText(getApplicationContext(), "Worker Success", Toast.LENGTH_LONG);
                    }
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


    private void login(String username , String password, CallBack<JSONObject> responseCallBack)
    {
        Pair <String , Object> pair1 = new Pair<>("userName",username);
        Pair <String , Object> pair2 = new Pair<>("password",password);
        httpConnection.send(Services.auth,"login",responseCallBack , JSONObject.class , null , pair1, pair2);
    }


}

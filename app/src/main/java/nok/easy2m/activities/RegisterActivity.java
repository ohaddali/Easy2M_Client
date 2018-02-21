package nok.easy2m.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import nok.easy2m.activities.*;
import nok.easy2m.R;
import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.communityLayer.HttpConnection;
import nok.easy2m.models.Services;


public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    TextView usernameTxt;
    TextView fullnameTxt;
    TextView passwordTxt;
    TextView phoneTxt;
    TextView birthdateTxt;
    Button registerBtn;
    Button backBtn;
    boolean adminRegister;
    HttpConnection httpConnection;
    String invitationToken;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameTxt = findViewById(R.id.usernameTxt2);
        passwordTxt = findViewById(R.id.passwordTxt2);
        fullnameTxt = findViewById(R.id.fullnameTxt);
        phoneTxt = findViewById(R.id.phoneTxt);
        birthdateTxt = findViewById(R.id.birthdateTxt);
        registerBtn = findViewById(R.id.registerBtn2);
        backBtn = findViewById(R.id.regBackBtn);
        registerBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        Uri uri = getIntent().getData(); //get URI
        if(uri != null) //if we come from URI
        {
            String key = "token";
            invitationToken = uri.getQueryParameter(key); //get query parameter from URI s.t. get X for query ?X=Value
        } //TODO : gets companyId and RoleId from the url.

        Intent i = getIntent();
        adminRegister = i.getBooleanExtra("adminRegister",false);
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId() == registerBtn.getId()) {
            httpConnection = HttpConnection.getInstance(getApplicationContext());

            String username = usernameTxt.getText().toString();
            String password = passwordTxt.getText().toString();
            String fullName = fullnameTxt.getText().toString();
            String phone = phoneTxt.getText().toString();
            String birthdate = birthdateTxt.getText().toString();
            Activity activity = this;
            CallBack<Boolean> responseCallBack = (resp) ->
            {
                if (resp) {
                    Intent loginIntent = new Intent(this, MainActivity.class);
                    loginIntent.putExtra("username", usernameTxt.getText().toString());
                    startActivity(loginIntent);
                    finish();
                } else
                    activity.runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show());
            };


            register(username, password, fullName, birthdate, phone, adminRegister, responseCallBack);
        }
        else if(view.getId() == backBtn.getId())
        {
            Intent loginIntent = new Intent(this, MainActivity.class);
            startActivity(loginIntent);
            finish();
        }

    }

    private void register(String username , String password, String fullName, String birthdate ,String phone ,boolean admin,
                          CallBack<Boolean> responseCallBack)
    {
        Pair<String , Object> pair1 = new Pair<>("username",username);
        Pair <String , Object> pair2 = new Pair<>("password",password);
        Pair <String , Object> pair3 = new Pair<>("fullname",fullName);
        Pair <String , Object> pair4 = new Pair<>("birthdate",birthdate);
        Pair <String , Object> pair5 = new Pair<>("phone",phone);
        Pair <String , Object> pair6 = new Pair<>("admin",admin);

        httpConnection.send(Services.auth,"register",responseCallBack,Boolean.class, null,
                pair1,pair2,pair3,pair4,pair5,pair6);
    }

}

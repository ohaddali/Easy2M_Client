package nok.easy2m;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nok.easy2m.models.User;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button loginBtn;
    Button registerBtn;
    TextView usernameText;
    TextView passwordText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        usernameText = findViewById(R.id.usernameText);
        passwordText = findViewById(R.id.passwordText);
        loginBtn.setOnClickListener(this);


    }


    @Override
    public void onClick(View view)
    {
        if(view.getId() == loginBtn.getId())
        {
            String username = usernameText.getText().toString();
            String password = usernameText.getText().toString();
            User user =loginStub(username,password);
            if(user.isLoggedIn()) {
                if (user.isAdmin())
                {
                    //Admin Intent
                }
                else
                {
                    //Worker Intent
                }
            }
        }

    }



    private User loginStub(String username, String password)
    {
        return new User();
    }
}

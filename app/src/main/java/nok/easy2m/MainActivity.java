package nok.easy2m;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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

        HttpConnection httpConnection = HttpConnection.getInstance(this);
        String serviceName = "x";
        String methodName = "Y";
        CallBack<JSONObject> responseCallBack = (jsonObject) ->{
            User user = new User();
            user.fromJSONObject(jsonObject[0]);


        };
        Map<String,String> map = new HashMap<>();
        map.put("a","1");
        Pair<String,Object> param1 = new Pair<>("param1Name" , map);
        Pair<String,Object> param2 = new Pair<>("param2Name" , "s");
        httpConnection.send(serviceName , methodName , responseCallBack , null , param1 , param2);



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
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG);
                }
                else
                {
                    //Worker Intent
                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"username or password is incorrect",Toast.LENGTH_LONG);
            }
        }

    }



    private User loginStub(String username, String password)
    {
        return new User();
    }
}

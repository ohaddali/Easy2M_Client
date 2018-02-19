package nok.easy2m;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HttpConnection httpConnection = HttpConnection.getInstance(this);
        String serviceName = "AuthService";
        String methodName = "register";
        CallBack<JSONObject> responseCallBack = (jsonObject) ->{


        };

        Pair<String,Object> param1 = new Pair<>("param1Name" , "ohad");
        Pair<String,Object> param2 = new Pair<>("param2Name" , "123");
        Pair<String,Object> param3 = new Pair<>("param2Name" , "true");
        httpConnection.send(serviceName , methodName , responseCallBack , null , param1 , param2 , param3);
    }
}

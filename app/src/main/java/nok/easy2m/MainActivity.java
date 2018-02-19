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
    }
}

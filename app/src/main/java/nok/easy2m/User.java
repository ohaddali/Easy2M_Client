package nok.easy2m;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by naordalal on 19/02/2018.
 */

class User implements SerializableObject{
    private String name;
    @Override
    public Map<String, String> toJSON() {
        Map<String,String> map = new HashMap<>();
        map.put("name" , name);
        return map;
    }

    @Override
    public void fromJSONObject(JSONObject jsonObject) {
        try {
            name = jsonObject.getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

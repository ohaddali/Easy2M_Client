package nok.easy2m;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by naordalal on 19/02/2018.
 */

public interface SerializableObject
{
    default JSONObject toJSON()
    {
        Gson gson = new Gson();
        try {
            return new JSONObject(gson.toJson(this));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    void fromJSONObject(JSONObject jsonObject); // Deserialize json into object fields
}

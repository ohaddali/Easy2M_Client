package nok.easy2m;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by naordalal on 19/02/2018.
 */

public interface SerializableObject
{
    Map<String,String> toJSON();
    void fromJSONObject(JSONObject jsonObject); // Deserialize json into object fields
}

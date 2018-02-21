package nok.easy2m.communityLayer;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Created by naordalal on 19/02/2018.
 */

public class SerializableObject
{
    public static <T> JSONObject toJSON(T object)
    {
        Gson gson = new Gson();
        try {
            return new JSONObject(gson.toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static <T> JSONArray toJSONArray(T[] object)
    {
        Gson gson = new Gson();
        try {
            return new JSONArray(gson.toJson(object));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static <T> T fromJSONObject(String json,Class<T> type)
    {
        Gson gson = new Gson();
        return gson.fromJson(json , type);
    }
}

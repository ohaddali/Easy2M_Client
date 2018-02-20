package nok.easy2m.models;

import org.json.JSONException;
import org.json.JSONObject;

import nok.easy2m.communityLayer.SerializableObject;

/**
 * Created by pc on 2/19/2018.
 */

public class User implements SerializableObject
{

    private boolean loggedIn;
    private String username;
    private boolean admin;
    private String name;
    private String phoneNumber;
    private String birthdate;

    public User()
    {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }


    @Override
    public void fromJSONObject(JSONObject jsonObject)
    {
        try {
            loggedIn = jsonObject.getBoolean("loggedIn");;
            username= jsonObject.getString("username");
            admin = jsonObject.getBoolean("admin");
            name =jsonObject.getString("name");
            phoneNumber =jsonObject.getString("phoneNumber");
            birthdate= jsonObject.getString("birthdate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

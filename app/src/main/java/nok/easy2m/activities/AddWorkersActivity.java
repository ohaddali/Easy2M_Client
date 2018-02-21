package nok.easy2m.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Toast;

import com.wafflecopter.multicontactpicker.ContactResult;
import com.wafflecopter.multicontactpicker.MultiContactPicker;

import java.util.ArrayList;
import java.util.List;

import nok.easy2m.*;
import nok.easy2m.R;

import nok.easy2m.communityLayer.SerializableObject;
import nok.easy2m.models.Role;

import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.communityLayer.HttpConnection;
import nok.easy2m.models.Services;


public class AddWorkersActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 991;

    private HttpConnection httpConnection;
    private String company;
    private List<ContactResult> currentSelectedContacts;
    private long companyId;
    private long roleId;
    private String roleName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(nok.easy2m.R.layout.activity_add_workers);

        company = getIntent().getStringExtra("companyName");
        companyId = getIntent().getLongExtra("companyId" , -1);
        roleId = getIntent().getLongExtra("roleId" , -1);
        roleName = getIntent().getStringExtra("roleName");

        httpConnection = HttpConnection.getInstance(this);
        currentSelectedContacts = new ArrayList<>();


        new MultiContactPicker.Builder(AddWorkersActivity.this) //Activity/fragment context
                .theme(R.style.MultiContactPicker_Azure)
                .hideScrollbar(false) //Optional - default: false
                .showTrack(true) //Optional - default: true
                .searchIconColor(Color.WHITE) //Option - default: White
                .handleColor(ContextCompat.getColor(AddWorkersActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleColor(ContextCompat.getColor(AddWorkersActivity.this, R.color.colorPrimary)) //Optional - default: Azure Blue
                .bubbleTextColor(Color.WHITE) //Optional - default: White
                .showPickerForResult(CONTACT_PICKER_REQUEST);

        Toast.makeText(getApplicationContext(), "Please select workers for role: " + roleName, Toast.LENGTH_SHORT).show();

    }

    private void requestSendSms()
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, Globals.SEND_SMS);
        }
        else
        {
            sendSms(currentSelectedContacts);
            done(roleId);
        }
    }

    private void done(long roleId)
    {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("roleId" , roleId);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        Globals.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(Globals.SendSmsAllow)
        {
            sendSms(currentSelectedContacts);
            done(roleId);
        }
        else
            done(-1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CONTACT_PICKER_REQUEST){
            if(resultCode == RESULT_OK) {
                List<ContactResult> selectedContacts = MultiContactPicker.obtainResult(data);
                currentSelectedContacts.clear();
                currentSelectedContacts.addAll(selectedContacts);
                requestSendSms();

            } else if(resultCode == RESULT_CANCELED){
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }

    private void sendSms(List<ContactResult> selectedContacts)
    {
        String methodName = "NotifyWorkerToJoinCompany";

        for(ContactResult contact : selectedContacts)
        {
            if(contact.getPhoneNumbers().size() == 0)
                continue;

            String phoneNumber = contact.getPhoneNumbers().get(0);
            CallBack<String> resCallBack = (res) ->
            {
                if(res != null)
                {
                    smsSender sender = new smsSender();
                    String message = getMessageFromResponse(res);
                    sender.send(phoneNumber , message);
                }

            };

            Pair<String,Object> param1 = new Pair<>("userPhone" , phoneNumber);
            Pair<String,Object> param2 = new Pair<>("companyId" , companyId);
            Pair<String,Object> param3 = new Pair<>("roleId" , roleId);
            httpConnection.send(Services.companiesService,methodName,resCallBack,String.class , null , param1 , param2 , param3);
        }

    }

    private String getMessageFromResponse(String url)
    {
        //String url = "http://easy2m.com?token=" + token;
        String message = "";

        message += "Hey,\n Company " + company + " invite you to join Easy2m application as " + roleName +"\n";
        message += "Please click on the link provided\n";
        message += url +"\n\n";
        message += "Thank you, \n Easy2m";

        return message;
    }

}

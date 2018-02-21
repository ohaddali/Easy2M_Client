package nok.easy2m.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;

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


public class AddWorkersActivity extends AppCompatActivity {

    private static final int CONTACT_PICKER_REQUEST = 991;

    Role[] roles;

    private HttpConnection httpConnection;
    private String company;
    private List<ContactResult> currentSelectedContacts;
    private long companyId;
    private long roleId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(nok.easy2m.R.layout.activity_add_workers);

        roles = SerializableObject.fromJSONObject(getIntent().getStringExtra("roles"),Role[].class);


        company = getIntent().getStringExtra("company");
        companyId = getIntent().getLongExtra("companyId" , -1);
        roleId = getIntent().getLongExtra("roleId" , -1);
        
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
    }

    private void requestReadContacts()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Globals.READ_CONTACTS);
        }
    }

    private void requestSendSms()
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Globals.SEND_SMS);
        }
        else
            sendSms(currentSelectedContacts);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        Globals.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(Globals.SendSmsAllow)
            sendSms(currentSelectedContacts);
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

                Intent intent = new Intent(this , companiesListActivity.class);
                startActivity(intent);

                finish();
            } else if(resultCode == RESULT_CANCELED){
                System.out.println("User closed the picker without selecting items.");
            }
        }
    }

    private void sendSms(List<ContactResult> selectedContacts)
    {
        String serviceName = "companiesService";
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
            httpConnection.send(serviceName,methodName,resCallBack,String.class , null , param1 , param2);
        }

    }

    private String getMessageFromResponse(String token)
    {
        String url = "http://easy2m.com?token=" + token;
        String message = "";

        message += "Hey,\n Company " + company + "invite you to join Easy2m application\n";
        message += "Please click on the link provided\n";
        message += url +"\n\n";
        message += "Thank you, \n Easy2m";

        return message;
    }

}

package nok.easy2m.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Pair;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import nok.easy2m.Globals;
import nok.easy2m.R;
import nok.easy2m.RoleAdapter;
import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.communityLayer.HttpConnection;
import nok.easy2m.communityLayer.SerializableObject;
import nok.easy2m.models.Role;
import nok.easy2m.models.Services;

public class AddRolesActivity extends ListActivity {


    private static final int ADD_WORKER_TO_ROLE_REQUEST = 1;
    long workerId;
    long companyId;
    boolean isAdmin;
    SharedPreferences pref;
    List<Role> addedRoles;
    private String companyName;
    private List<Role> roles;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_roles);

        pref = getSharedPreferences("label" , 0);
        workerId = pref.getLong("userId" , -1);
        isAdmin = pref.getBoolean("admin",true);
        companyId = getIntent().getLongExtra("companyId",-1);
        companyName = getIntent().getStringExtra("companyName");
        addedRoles = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Add Roles");
        setActionBar(toolbar);
        ListActivity activity = this;
        roles = new ArrayList<>();
        setListAdapter(new RoleAdapter(this,R.layout.rolelist,roles));
        String json = SerializableObject.toJSONArray(roles.toArray()).toString();
        Button doneBtn = toolbar.findViewById(R.id.doneAddRolesBtn);

        doneBtn.setOnClickListener(v ->
        {
            for(Role role : roles)
            {
                if(!role.getRoleName().equals(Globals.addRole))
                    sendRole(role.getRoleName(), companyId , roles.indexOf(role));
            }

        });
    }

    private void requestReadContacts()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CONTACTS}, Globals.READ_CONTACTS);
        }
        else
            sendRole();
    }

    private void sendRole()
    {
        Role role = addedRoles.get(0);

        Intent i = new Intent(this,AddWorkersActivity.class);
        i.putExtra("companyId" , companyId);
        i.putExtra("companyName" , companyName);
        i.putExtra("roleId" , role.getId());
        i.putExtra("roleName" , role.getRoleName());

        startActivityForResult(i , ADD_WORKER_TO_ROLE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        Globals.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(Globals.GetContactsAllow)
        {
            sendRole();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please allow read contacts", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendRole(String roleName,long companyId , int roleIndex)
    {
        HttpConnection httpConnection = HttpConnection.getInstance(this);
        Pair<String,Object> pair1 = new Pair<>("companyId",companyId);//@TOD
        Pair<String,Object> pair2 = new Pair<>("roleName",roleName);
        Role newRole = new Role();
        newRole.setRoleName(roleName);
        newRole.setCompanyId(companyId);
        CallBack<Long> resp = (id) ->
        {
            if(id!=-1)
            {
                newRole.setId(id);
                addedRoles.add(newRole);
                if(roleIndex == roles.size() - 1)
                    requestReadContacts();
            }
            else
            {
                runOnUiThread(()-> Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show());
            }
        };

        httpConnection.send(Services.roles,"addRole",resp,Long.class,null,pair1,pair2);
        //resp.execute(new Long(4));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case ADD_WORKER_TO_ROLE_REQUEST:
                if(resultCode == RESULT_OK) {
                    long roleId = data.getLongExtra("roleId" , -1);
                    int index = getRoleIndex(roleId);
                    if(index == -1)
                    {
                        Toast.makeText(getApplicationContext(), "Please confirm send sms", Toast.LENGTH_SHORT).show();
                        break;
                    }
                    if(index == addedRoles.size() - 1)
                    {
                        finish();
                    }
                    else
                    {
                        Role newRole = addedRoles.get(index + 1);

                        Intent i = new Intent(this,AddWorkersActivity.class);
                        i.putExtra("companyId" , companyId);
                        i.putExtra("companyName" , companyName);
                        i.putExtra("roleId" , newRole.getId());
                        i.putExtra("roleName" , newRole.getRoleName());

                        startActivityForResult(i , ADD_WORKER_TO_ROLE_REQUEST);
                    }
                }

                break;
        }
    }

    private int getRoleIndex(long roleId)
    {
        for(Role role : addedRoles)
        {
            if(role.getId() == roleId)
                return addedRoles.indexOf(role);
        }

        return  -1;
    }

}

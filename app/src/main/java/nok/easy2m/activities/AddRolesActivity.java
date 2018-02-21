package nok.easy2m.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.Toast;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import nok.easy2m.R;
import nok.easy2m.RoleAdapter;
import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.communityLayer.HttpConnection;
import nok.easy2m.communityLayer.SerializableObject;
import nok.easy2m.models.Role;
import nok.easy2m.models.Services;

public class AddRolesActivity extends ListActivity {


    long workerId;
    long companyId;
    boolean isAdmin;
    SharedPreferences pref;
    List<Role> addedRoles;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_roles);

        pref = getSharedPreferences("label" , 0);
        workerId = pref.getLong("userId" , -1);
        isAdmin = pref.getBoolean("admin",true);
        companyId = getIntent().getLongExtra("companyId",-1);
        addedRoles = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        toolbar.setTitle("Add Roles");
        setActionBar(toolbar);
        ListActivity activity = this;
        List<Role> roles = new ArrayList<>();
        setListAdapter(new RoleAdapter(this,R.layout.rolelist,roles));
        String json = SerializableObject.toJSONArray(roles.toArray()).toString();
        Button doneBtn = toolbar.findViewById(R.id.doneAddRolesBtn);

        doneBtn.setOnClickListener(v ->
        {
            for (Role role: roles) {
                if(!role.getRoleName().equals("addNewRole")) {
                    sendRole(role.getRoleName(), companyId);
                }
            }
            Intent i = new Intent(activity,AddWorkersActivity.class);
            i.putExtra("roles",json);
            startActivity(i);
        });
    }

    private void sendRole(String roleName,long companyId)
    {
        HttpConnection httpConnection = HttpConnection.getInstance(this);
        Pair<String,Object> pair1 = new Pair<>("companyId",companyId);//@TOD
        Pair<String,Object> pair2 = new Pair<>("roleName",roleName);
        Role newRole = new Role();
        newRole.setRoleName(roleName);
        newRole.setCompanyId(companyId);
        CallBack<Long> resp = (id) ->
        {
            if(id!=-1) {
                newRole.setId(id);
                addedRoles.add(newRole);
            }
            else
            {
                runOnUiThread(()-> Toast.makeText(getApplicationContext(), "Something Went Wrong", Toast.LENGTH_SHORT).show());
            }
        };

        httpConnection.send(Services.roles,"addRole",resp,Long.class,null,pair1,pair2);
        //resp.execute(new Long(4));

    }

}

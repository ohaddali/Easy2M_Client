package nok.easy2m;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.communityLayer.HttpConnection;
import nok.easy2m.models.Company;
import nok.easy2m.models.Role;
import nok.easy2m.models.Services;

/**
 * Created by pc on 2/21/2018.
 */

public class RoleAdapter extends ArrayAdapter<Role>
{
    private final Activity context;
    private List<Role> roles;
    private LayoutInflater inflater;


    public RoleAdapter(@NonNull Activity context, int resource, @NonNull List<Role> objects) {
        super(context, resource, objects);
        this.context = context;
        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        roles = objects;
        Role addRole = new Role();
        addRole.setRoleName(Globals.addRole);
        roles.add(0,addRole);

        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public Role getItem(int position)
    {
        return roles.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View rowView=inflater.inflate(R.layout.rolelist, parent,false);
        EditText roleName = rowView.findViewById(R.id.roleNameTxt);
        Button removeBtn = rowView.findViewById(R.id.deleteRoleBtn);
        Button checkBtn = rowView.findViewById(R.id.addRoleBtn);

        Role role = getItem(position);
        removeBtn.setOnClickListener(v ->
                remove(role));
        checkBtn.setOnClickListener(v ->{
            Role newRole =new Role();
            newRole.setRoleName(roleName.getText().toString());
            add(newRole);
        });


        roleName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    checkBtn.setVisibility(View.GONE);
                } else
                    checkBtn.setVisibility(View.VISIBLE);
            }
        });



        if(role.getRoleName()!=null && role.getRoleName().equals(Globals.addRole))
        {
            checkBtn.setVisibility(View.GONE);
            removeBtn.setClickable(false);
            roleName.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(roleName,InputMethodManager.SHOW_IMPLICIT);
        }
        else {
            roleName.setText(role.getRoleName());
            roleName.setFocusable(false);
            checkBtn.setVisibility(View.GONE);
        }
        return rowView;
    }
    private void removeRole(Role role) //NO DELETE
    {
        HttpConnection httpConnection = HttpConnection.getInstance(context);
        Pair<String,Object> pair1 = new Pair<>("roleId",role.getId());
        CallBack<Boolean> resp = (success) ->
        {
            if(success) {
                remove(role);
            }
            else
            {
                context.runOnUiThread(()-> Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show());
            }
        };

        httpConnection.send(Services.roles,"addRole",resp,Boolean.class,null,pair1);

    }

    private void addRole(String roleName,long companyId)
    {
        HttpConnection httpConnection = HttpConnection.getInstance(context);
        Pair<String,Object> pair1 = new Pair<>("companyId",companyId);//@TOD
        Pair<String,Object> pair2 = new Pair<>("roleName",roleName);
        Role newRole = new Role();
        newRole.setRoleName(roleName);
        newRole.setCompanyId(companyId);
        CallBack<Long> resp = (id) ->
        {
            if(id!=-1) {
                //newRole.setId(id);
                add(newRole);
            }
            else
            {
                context.runOnUiThread(()-> Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show());
            }
        };

        httpConnection.send(Services.roles,"addRole",resp,Long.class,null,pair1,pair2);

    }


    @Override
    public void add(@Nullable Role object)
    {
        roles.add(object);
        super.notifyDataSetChanged();
    }

    @Override
    public void remove(@Nullable Role object) {
        roles.remove(object);
        super.notifyDataSetChanged();
    }
}



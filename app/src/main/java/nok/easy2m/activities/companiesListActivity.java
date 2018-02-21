package nok.easy2m.activities;



import android.app.AlertDialog;
import android.app.ListActivity;

import android.content.Intent;
import android.content.SharedPreferences;


import android.os.Bundle;

import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;


import com.android.volley.VolleyError;


import java.util.ArrayList;
import java.util.List;

import nok.easy2m.CompanyAdapter;
import nok.easy2m.R;
import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.communityLayer.HttpConnection;
import nok.easy2m.models.Company;
import nok.easy2m.models.Services;
public class companiesListActivity extends ListActivity
{

    HttpConnection httpConnection;
    SharedPreferences pref;
    long workerId;
    boolean isAdmin;
    RelativeLayout progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies_list);
        pref = getSharedPreferences("label" , 0);
        workerId = pref.getLong("userId" , -1);
        isAdmin = pref.getBoolean("admin",true);
        httpConnection = HttpConnection.getInstance(this);
        progressBar = findViewById(R.id.loadingPanel);
        List<Company> companies = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        setActionBar(toolbar);
        ListActivity activity = this;
        CallBack<Company[]> respCallBack = (response) ->
        {
            for(Company company : response)
                companies.add(company);
            if(isAdmin) {
                Company c = new Company();
                c.setId(-1);
                c.setName("Add your Company");
                c.setOwnerID(-1);
                c.setDescription("Add your company to the system and start work with us");
                c.setLogoUrl("PLUS");
                companies.add(c);
            }
            activity.runOnUiThread(()-> ((CompanyAdapter)getListAdapter()).notifyDataSetChanged());
            activity.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
        };

        CallBack<VolleyError> errorCallBack =(error) ->
        {
            activity.runOnUiThread(() -> progressBar.setVisibility(View.GONE));
            activity.runOnUiThread(() -> Toast.makeText(activity, "Somthing went wrong, Try to login again", Toast.LENGTH_LONG).show());
        };

        progressBar.setVisibility(View.VISIBLE);
        getCompanies(respCallBack,errorCallBack);
        //@TODO:
        setListAdapter(new CompanyAdapter(this,R.layout.mylist,companies));
        getListView().setLongClickable(true);
        getListView().setOnItemLongClickListener((adapterView, view, position, l) ->
        {
            Company item = (Company)adapterView.getItemAtPosition(position);
            AlertDialog alertDialog = new AlertDialog.Builder(companiesListActivity.this).create();
            alertDialog.setTitle(item.getName());

            alertDialog.setMessage(item.getDescription());
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"OK",
                    (dialog, which) -> dialog.dismiss());
            alertDialog.show();
            return true;
        });

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        //TODO
        Company item = (Company)l.getAdapter().getItem(position);

        if(item.getId() == -1)
        {
            Intent i = new Intent(this,AddCompanyActivity.class);
            this.runOnUiThread(() -> startActivity(i));
        }
        super.onListItemClick(l, v, position, id);
    }

    private void getCompanies(CallBack<Company[]> respCallBack, CallBack<VolleyError> errorCallBack)
    {

        Pair<String, Object> pair1 = new Pair<>("workerId", workerId);
        httpConnection.send(Services.companiesService,"getWorkerCompanies"
                ,respCallBack, Company[].class,errorCallBack,pair1);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.back_btn)
        {
            Intent i = new Intent(this,MainActivity.class);
            SharedPreferences.Editor editor = pref.edit();
            editor.clear();
            startActivity(i);
            finish();
            return true;
        }
        return true;
    }


}

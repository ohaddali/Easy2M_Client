package nok.easy2m.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies_list);
        pref = getSharedPreferences("label" , 0);
        workerId = pref.getLong("userId" , -1);
        isAdmin = pref.getBoolean("admin",false);
        httpConnection = HttpConnection.getInstance(this);
        List<Company> companies = new ArrayList<>();
        CallBack<Company[]> respCallBack = (response) ->
        {
            for(Company company : response)
                companies.add(company);
        };
        getCompanies(respCallBack);
    }

    private void getCompanies(CallBack<Company[]> respCallBack)
    {
        Pair<String, Object> pair1 = new Pair<>("workerId", workerId);
        httpConnection.send(Services.companiesService,"getWorkerCompanies",respCallBack, Company[].class,null,pair1);
    }

}

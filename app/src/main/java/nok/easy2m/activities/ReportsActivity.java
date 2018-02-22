package nok.easy2m.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;


import android.util.Pair;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;

import android.widget.RelativeLayout;
import android.widget.Toast;


import com.android.volley.VolleyError;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nok.easy2m.AzureBlobsManager;
import nok.easy2m.Globals;
import nok.easy2m.R;
import nok.easy2m.ReportAdapter;
import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.communityLayer.HttpConnection;
import nok.easy2m.models.Report;
import nok.easy2m.models.Services;


public class ReportsActivity extends ListActivity  {

    HttpConnection httpConnection;
    SharedPreferences pref;
    long workerId;
    boolean isAdmin;
    RelativeLayout progressBar;
    DatePickerDialog dialog;
    SimpleDateFormat dateFormatter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);


        pref = getSharedPreferences("label" , 0);
        workerId = pref.getLong("userId" , -1);
        isAdmin = pref.getBoolean("admin",false);
        httpConnection = HttpConnection.getInstance(this);
        progressBar = findViewById(R.id.loadingPanel);

        dateFormatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);

        List<Report> reports = new ArrayList<>();
        CallBack<Report[]> response = objects ->
        {
            for (Report report: objects) {
                if(report !=null && report.getReportId()>0)
                    reports.add(report);
            }
            if(!isAdmin)
            {
                Report rep = new Report();
                rep.setReportId(-30);
                reports.add(rep);
                runOnUiThread(() -> ((ReportAdapter)getListAdapter()).notifyDataSetChanged());
            }
            progressBar.setVisibility(View.GONE);
        };
        if(!isAdmin)
            getAllWorkerReports(workerId,response);
        else 
        {
            getAllAdminReports();
        }

        setListAdapter(new ReportAdapter(this,R.layout.report_list,reports));
    }

    private void getAllWorkerReports(long workerId, CallBack<Report[]> response)
    {
        progressBar.setVisibility(View.VISIBLE);
        Pair<String,Object> pair1 = new Pair<>("workerId",workerId);

        httpConnection.send(Services.reports,"getWorkerReports",response,Report[].class,
                error -> runOnUiThread(() -> {
                            Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);}),
                pair1);

        Report[] stam = new Report[1];
        //response.execute(stam);
    }



    @TargetApi(Build.VERSION_CODES.N)
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        Report item = (Report)l.getAdapter().getItem(position);
        if(item.getReportId()==-30)
        {
            Calendar c = Calendar.getInstance();
            dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth,0,0,0);
                    newDate.add(Calendar.DAY_OF_WEEK , -(newDate.get(Calendar.DAY_OF_WEEK) - 1));
                    exportReport(dateFormatter.format(newDate.getTime()));
                }
            },c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        }
        else
        {
            FileOutputStream os = null;
            try {
                os = openFileOutput(item.getUrl(),MODE_APPEND);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Activity activity =this;
            CallBack<Boolean> callBack = success -> {
                if(success)
                {
                    File[] files = ReportsActivity.this.getFilesDir().listFiles();
                    int index = 0;
                    for (File file:
                         files) {
                        if(file.getName().equals(item.getUrl()))
                            break;
                        index++;
                    }

                    //TODO : OPEN FILE
                }
            };
            AzureBlobsManager.GetFile(item.getUrl(),os,Globals.reportsContainer,callBack);

        }
    }

    private void exportReport(String date)
    {
        Pair<String,Object> pair1 = new Pair<>("userId",workerId);
        Pair<String,Object> pair2 = new Pair<>("date",date);
        CallBack<Boolean> resp = x->
                runOnUiThread(()-> Toast.makeText(this, "The request for report in date " +date+" is on the way!", Toast.LENGTH_LONG).show());
        CallBack<VolleyError> err = error -> runOnUiThread(()-> Toast.makeText(this, "Error in request!", Toast.LENGTH_LONG).show());
        httpConnection.send(Services.reports,"exportWeeklyReportForWorker",resp,Boolean.class,err,pair1,pair2);
        //runOnUiThread(()-> Toast.makeText(this, "The request for report in date" +date+" is on the way!", Toast.LENGTH_LONG).show());

    }

    private void getAllAdminReports()
    {
    }
}

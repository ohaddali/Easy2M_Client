package nok.easy2m.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;


import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import nok.easy2m.AzureBlobsManager;
import nok.easy2m.Globals;
import nok.easy2m.NotificationManager;
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
    private Report chosenItem;

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
            requestWriteExternalStorage(item);
        }
    }

    private void requestWriteExternalStorage(Report item)
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            chosenItem = item;
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Globals.WRITE_EXTERNAL_STORAGE_CODE);
        }
        else
            downloadFile(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        Globals.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(Globals.writeAllow)
            downloadFile(chosenItem);
        else
            Toast.makeText(getApplicationContext(), "Please confirm write external storage", Toast.LENGTH_SHORT).show();
    }

    private void downloadFile(Report item)
    {
        File file = null;
        FileOutputStream fos = null;
        try
        {
            file = getFile(item.getUrl());
            fos = new FileOutputStream(file);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        Context context = getApplicationContext();
        Uri fileUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".my.package.name.provider", file);
        NotificationManager manager = new NotificationManager(getApplicationContext() , fileUri , item.getDate());
        CallBack<Boolean> callBack = (success) ->
        {
            if(success)
            {
                //send notification
                manager.send();
            }
        };


        AzureBlobsManager.GetFile(item.getUrl(),fos,Globals.reportsContainer,callBack);
    }

    private File getFile(String name) throws IOException
    {
        File home = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File file = new File(home , name);
        int index = 1;
        while(file.exists())
        {
            file = new File(home , name.split("\\.")[0]+" (" + index + ")."+name.split("\\.")[1]);
            index++;
        }

        file.createNewFile();
        return  file;
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

package nok.easy2m.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import nok.easy2m.R;

public class CompanyMenuActivity extends AppCompatActivity implements View.OnClickListener {


    Button clockBtn;
    Button reportsBtn;
    Button timetableBtn;
    TextView companyNameLbl;
    String companyName;
    private long companyId;
    private boolean isAdmin;
    private SharedPreferences pref;
    private LinearLayout linearLayout;
    Button addWorkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_menu);

        clockBtn = findViewById(R.id.menu_clockBtn);
        reportsBtn =findViewById(R.id.menu_reportsBtn);
        timetableBtn = findViewById(R.id.menu_timetableBtn);
        companyNameLbl = findViewById(R.id.menu_companynameLbl);
        pref = getSharedPreferences("label" , 0);
        isAdmin = pref.getBoolean("admin",false);

        companyName = getIntent().getStringExtra("companyName");
        companyId = getIntent().getLongExtra("companyId" , 0);
        companyNameLbl.setText(companyName);

        linearLayout = findViewById(R.id.user_gridlayout);
        if(isAdmin)
        {
            addWorkers = new Button(this);
            addWorkers.setText("Add Workers");
            addWorkers.setOnClickListener(this);
            linearLayout.removeView(clockBtn);
            linearLayout.addView(addWorkers);
        }

        clockBtn.setOnClickListener(this);
        reportsBtn.setOnClickListener(this);
        timetableBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        if( v == clockBtn)
        {
            i = new Intent(this,ClockActivity.class);
            i.putExtra("companyId" , companyId);
            startActivity(i);
        }
        else if(v == reportsBtn)
        {
            i= new Intent(this,ReportsActivity.class);
            startActivity(i);
        }
        else if(v == timetableBtn)
        {
            i = new Intent(this, TimeTableActivity.class);
            i.putExtra("companyId" , companyId);
            startActivity(i);
        }
        else if (v == addWorkers)
        {
            i = new Intent(this,AddRolesActivity.class);
            startActivity(i);
        }
    }
}

package nok.easy2m.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import nok.easy2m.R;

public class CompanyMenuActivity extends AppCompatActivity implements View.OnClickListener {


    Button clockBtn;
    Button reportsBtn;
    Button timetableBtn;
    TextView companyNameLbl;
    String companyName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_menu);

        clockBtn = findViewById(R.id.menu_clockBtn);
        reportsBtn =findViewById(R.id.menu_reportsBtn);
        timetableBtn = findViewById(R.id.menu_timetableBtn);
        companyNameLbl = findViewById(R.id.menu_companynameLbl);

        companyName = getIntent().getStringExtra("companyName");
        companyNameLbl.setText(companyName);

        clockBtn.setOnClickListener(this);
        reportsBtn.setOnClickListener(this);
        timetableBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        if( v == clockBtn)
        {
            //i = new Intent(this,ClockActivity.class)
            //startActivity(i);
        }
        else if(v == reportsBtn)
        {
            i= new Intent(this,ReportsActivity.class);
            startActivity(i);
        }
        else if(v == timetableBtn)
        {
            i = new Intent(this, TimeTableActivity.class);
            startActivity(i);
        }
    }
}

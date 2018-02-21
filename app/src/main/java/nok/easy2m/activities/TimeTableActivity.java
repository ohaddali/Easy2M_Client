package nok.easy2m.activities;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LayoutDirection;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nok.easy2m.R;
import nok.easy2m.Shift;

public class TimeTableActivity extends AppCompatActivity
{

    private TableLayout table;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        long comapnyId = getIntent().getLongExtra("companyId" , 0);
        List<Shift> shifts = new ArrayList<>(); //get from server

        Collections.sort(shifts);
        table = findViewById(R.id.tableLayout);

        addRowsToTable(shifts);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Shifts");

    }

    private void addRowsToTable(List<Shift> shifts)
    {
        String currentRole = "";
        int currentDayInWeek = 0;
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        int [] colors = {Color.GREEN , Color.WHITE};
        int currentColor = 0;
        for(Shift shift : shifts)
        {
            View view = inflater.inflate(R.layout.row_shift, null);
            TextView workerNameText = view.findViewById(R.id.workerName);
            workerNameText.setTextColor(Color.BLACK);

            TextView workerRoleText = view.findViewById(R.id.workerRole);
            workerRoleText.setTextColor(Color.BLACK);

            TextView timeShiftText = view.findViewById(R.id.timeShift);
            timeShiftText.setTextColor(Color.BLACK);

            workerNameText.setText(shift.getWorkerName());
            workerRoleText.setText(shift.getRole());
            timeShiftText.setText(shift.getStartTime() + "-" + shift.getEndTime());

            if(!currentRole.equals(shift.getRole()))
                currentColor = 1 - currentColor;

            view.setBackgroundColor(colors[currentColor]);

            if(currentDayInWeek != shift.getDayInTheWeek())
            {
                TextView day = new TextView(getApplicationContext());
                day.setText(getDayOfWeek(shift.getDayInTheWeek()));
                day.setTextColor(Color.RED);
                TableLayout.LayoutParams tableRowParams=
                        new TableLayout.LayoutParams
                                (TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT);
                tableRowParams.setMargins(2, 30, 0, 30);
                day.setLayoutParams(tableRowParams);
                day.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                table.addView(day);
            }
            view.setClickable(true);
            view.setFocusable(true);
            table.addView(view);

            currentDayInWeek = shift.getDayInTheWeek();
            currentRole = shift.getRole();
        }
    }

    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return day;
    }
}

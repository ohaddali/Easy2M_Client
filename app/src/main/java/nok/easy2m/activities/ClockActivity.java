package nok.easy2m.activities;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import nok.easy2m.R;

public class ClockActivity extends AppCompatActivity implements View.OnClickListener {

    long startTime;
    private SharedPreferences pref;
    private long currentTimerCompany;
    private long currentCompanyId;
    private TextView timerText;
    private Button timerButton;
    private Timer timer;
    private MyTimerTask myTask;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        pref = getSharedPreferences("label" , 0);
        startTime = pref.getLong("timer" , -1);
        currentTimerCompany = pref.getLong("companyTimer" , -1);

        currentCompanyId = getIntent().getLongExtra("companyId" , 0);

        timerText = findViewById(R.id.timerText);
        timerButton = findViewById(R.id.clockBtn);

        timerButton.setOnClickListener(this);

        timer = new Timer();
        myTask = new MyTimerTask();

        if(currentCompanyId == currentTimerCompany && startTime != -1)
        {
            timerButton.setText("EXIT");
            timer.schedule(myTask,0,1000);
        }

    }

    @Override
    public void onClick(View v)
    {
        if(v == timerButton)
        {
            if(timerButton.getText().toString().equalsIgnoreCase("enter"))
            {
                if(currentCompanyId != currentTimerCompany && currentTimerCompany != -1)
                {
                    Toast.makeText(getApplicationContext(), "You already enter to another company", Toast.LENGTH_SHORT).show();
                    return;
                }

                long time = System.currentTimeMillis();
                currentTimerCompany = currentCompanyId;
                startTime = time;

                SharedPreferences.Editor editor = pref.edit();
                editor.putLong("companyTimer" , currentCompanyId);
                editor.putLong("timer" , time);
                editor.commit();

                timer = new Timer();
                myTask = new MyTimerTask();
                timer.schedule(myTask,0,1000);

                timerButton.setText("EXIT");
            }
            else
            {
                SharedPreferences.Editor editor = pref.edit();
                editor.remove("companyTimer");
                editor.remove("timer");
                editor.commit();

                currentTimerCompany = -1;
                startTime = -1;

                timer.cancel();
                timer.purge();

                timerButton.setText("ENTER");
            }
        }
    }

    class MyTimerTask extends TimerTask
    {
        public void run()
        {
            long currentTime = System.currentTimeMillis();
            long period = currentTime - startTime;

            runOnUiThread(() -> timerText.setText(formatMillis(period)));
        }
    }

    public static String formatMillis(long timeInMillis) {
        String sign = "";
        if (timeInMillis < 0)
        {
            sign = "-";
            timeInMillis = Math.abs(timeInMillis);
        }

        long hours = timeInMillis / TimeUnit.HOURS.toMillis(1);
        long minutes = timeInMillis / TimeUnit.MINUTES.toMillis(1);
        long seconds = timeInMillis % TimeUnit.MINUTES.toMillis(1) / TimeUnit.SECONDS.toMillis(1);
        //long millis = timeInMillis % TimeUnit.SECONDS.toMillis(1);

        final StringBuilder formatted = new StringBuilder(20);
        formatted.append(sign);
        formatted.append(String.format("%02d", hours));
        formatted.append(String.format(":%02d", minutes));
        formatted.append(String.format(":%02d", seconds));

        return formatted.toString();
    }
}

package nok.easy2m.activities;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import nok.easy2m.R;
import nok.easy2m.communityLayer.HttpConnection;

public class companiesListActivity extends ListActivity
{

    HttpConnection httpConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_companies_list);

        httpConnection = HttpConnection.getInstance(this);
    }
}

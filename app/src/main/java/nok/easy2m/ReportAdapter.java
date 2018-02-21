package nok.easy2m;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import nok.easy2m.models.Report;

/**
 * Created by pc on 2/21/2018.
 */

public class ReportAdapter extends ArrayAdapter<Report> {

    private Activity context;
    private List<Report> reports;
    private LayoutInflater inflater;


    public ReportAdapter(@NonNull Activity context, int resource, @NonNull List<Report> objects) {
        super(context, resource, objects);
        this.context= context;
        this.reports = objects;
        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public Report getItem(int position) {
        return reports.get(position);
    }

    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View rowView = inflater.inflate(R.layout.report_list, parent,false);
        TextView reportDateTxt = rowView.findViewById(R.id.Report_Date);
        Report item = getItem(position);


        if(item.getReportId()== -30)
            reportDateTxt.setText("Press To Export New Report");
        else
            reportDateTxt.setText(item.getDate().toString());
        return rowView;
    }
}

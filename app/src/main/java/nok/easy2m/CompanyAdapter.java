package nok.easy2m;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.models.Company;

/**
 * Created by pc on 2/20/2018.
 */

public class CompanyAdapter extends ArrayAdapter<Company>
{
    private final Activity context;
    private List<Company> companies;
    private LayoutInflater inflater;

    public CompanyAdapter(@NonNull Activity context, int resource, @NonNull List<Company> objects) {
        super(context, resource, objects);
        this.context= context;
        this.companies = objects;
        inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Nullable
    @Override
    public Company getItem(int position) {
        return companies.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        View rowView = inflater.inflate(R.layout.mylist, parent,false);
        TextView title = rowView.findViewById(R.id.company_name);
        ImageView logo = rowView.findViewById(R.id.company_logo);
        Company item = getItem(position);
        title.setText(item.getName());
        if(item.getLogoUrl() != null)
        {
            if(item.getLogoUrl().equals("PLUS"))
            {
                logo.setImageResource(R.mipmap.add_icon_round);
            }
            else {
                ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                CallBack<Boolean> callBack = (Boolean success) -> {
                    if(success)
                    {
                        byte[] buffer = imageStream.toByteArray();

                        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                        context.runOnUiThread(() -> logo.setImageBitmap(bitmap));
                    }
                };

                AzureBlobsManager.GetFile(item.getLogoUrl(),imageStream,Globals.companiesImagesContainer,callBack);
            }
        }
        return rowView;
    }


}

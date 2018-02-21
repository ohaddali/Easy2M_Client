package nok.easy2m.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.opengl.Visibility;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.support.constraint.ConstraintLayout.LayoutParams;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.NoSuchElementException;

import nok.easy2m.AzureBlobsManager;
import nok.easy2m.Globals;
import nok.easy2m.R;
import nok.easy2m.communityLayer.CallBack;
import nok.easy2m.communityLayer.HttpConnection;
import nok.easy2m.communityLayer.SerializableObject;
import nok.easy2m.models.Company;
import nok.easy2m.models.Services;

public class AddCompanyActivity extends AppCompatActivity implements View.OnClickListener {


    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView companyImg;
    TextView companyName;
    TextView companyDesc;
    Button doneBtn;
    HttpConnection httpConnection;
    private SharedPreferences pref;
    long workerId;
    private boolean isAdmin;
    Bitmap comapnyBitmap;
    RelativeLayout progressBar;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_company);
        httpConnection = HttpConnection.getInstance(this);
        companyImg = findViewById(R.id.companyImg);
        companyName = findViewById(R.id.compnaynameTxt);
        companyDesc = findViewById(R.id.compantDescTxt);
        doneBtn = findViewById(R.id.addCompanyBtn);
        companyImg.setOnClickListener(this);
        doneBtn.setOnClickListener(this);
        comapnyBitmap = null;
        pref = getSharedPreferences("label" , 0);
        workerId = pref.getLong("userId" , -1);
        isAdmin = pref.getBoolean("admin",true);
        progressBar = findViewById(R.id.loadingPanel2);
        progressBar.setVisibility(View.GONE);
        activity = this;

        if(!isAdmin)
        {
            AlertDialog alertDialog = new AlertDialog.Builder(AddCompanyActivity.this).create();
            alertDialog.setTitle("You are not an admin");
            Activity activity = this;
            alertDialog.setMessage("How did you get here?!");
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE,"Back",
                    (dialog, which) -> {
                        dialog.dismiss();
                        activity.finish();
                    });
            alertDialog.show();
        }

    }


    @Override
    public void onClick(View v)
    {
        if(v.getId() == companyImg.getId())
        {
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

        }

        else if(v.getId() == doneBtn.getId())
        {

            CallBack<Company> response = (comp) ->
            {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                if(comp.getId()==-1)
                {
                    runOnUiThread(() -> Toast.makeText(activity, "Something went wrong", Toast.LENGTH_LONG).show());
                }
                else {
                    Intent intent = new Intent(activity, AddRolesActivity.class);
                    intent.putExtra("companyId", comp.getId());
                    startActivity(intent);
                    activity.finish();
                }
            };
            CallBack<VolleyError> errorCallBack = objects ->
            {
                runOnUiThread(() -> progressBar.setVisibility(View.GONE));
                runOnUiThread(() -> Toast.makeText(activity, "Error:Something went wrong", Toast.LENGTH_LONG).show());
            };

            addCompany(response,errorCallBack);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                comapnyBitmap = bitmap;
                companyImg.setImageBitmap(bitmap);
                scaleImage(companyImg);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private void addCompany(CallBack<Company> respCallback , CallBack<VolleyError> errorCallBack)
    {

        progressBar.setVisibility(View.VISIBLE);
        Company newCompany = new Company();
        newCompany.setOwnerID(workerId);
        newCompany.setName(companyName.getText().toString());
        newCompany.setDescription(companyDesc.getText().toString());
        if(comapnyBitmap!=null)
        {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            comapnyBitmap.compress(Bitmap.CompressFormat.PNG,0,bos);
            ByteArrayInputStream bs = new ByteArrayInputStream(bos.toByteArray());


            AzureBlobsManager.UploadImage(bs,bs.available(),newCompany.getName(), Globals.companiesImagesContainer,
                    URL -> {
                        newCompany.setLogoUrl(URL);
                        Pair<String,Object> pair1 = new Pair<>("newCompany", SerializableObject.toJSON(newCompany));
                        httpConnection.send(Services.companiesService,"addCompany"
                                ,respCallback,Company.class,errorCallBack,pair1);
                    });
        }
        else {
            newCompany.setLogoUrl("");
            Pair<String, Object> pair1 = new Pair<>("newCompany", SerializableObject.toJSON(newCompany));
            httpConnection.send(Services.companiesService, "addCompany"
                    , respCallback, Company.class, errorCallBack, pair1);
        }

    }

    private void scaleImage(ImageView view) throws NoSuchElementException {
        // Get bitmap from the the ImageView.
        Bitmap bitmap = null;

        try {
            Drawable drawing = view.getDrawable();
            bitmap = ((BitmapDrawable) drawing).getBitmap();
            bitmap = getCroppedBitmap(bitmap);
        } catch (NullPointerException e) {
            throw new NoSuchElementException("No drawable on given view");
        } catch (ClassCastException e) {
            // Check bitmap is Ion drawable
            //bitmap = Ion.with(view).getBitmap();
        }

        // Get current dimensions AND the desired bounding box
        int width = 0;

        try {
            width = bitmap.getWidth();
        } catch (NullPointerException e) {
            throw new NoSuchElementException("Can't find bitmap on given view/drawable");
        }

        int height = bitmap.getHeight();
        int bounding = dpToPx(150);

        // Determine how much to scale: the dimension requiring less scaling is
        // closer to the its side. This way the image always stays inside your
        // bounding box AND either x/y axis touches it.
        float xScale = ((float) bounding) / width;
        float yScale = ((float) bounding) / height;
        float scale = (xScale <= yScale) ? xScale : yScale;

        // Create a matrix for the scaling and add the scaling data
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        // Create a new bitmap and convert it to a format understood by the ImageView
        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth(); // re-use
        height = scaledBitmap.getHeight(); // re-use
        BitmapDrawable result = new BitmapDrawable(scaledBitmap);

        // Apply the scaled bitmap
        view.setImageDrawable(result);

        // Now change ImageView's dimensions to match the scaled image
        LayoutParams params = (LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);

    }

    private int dpToPx(int dp) {
        float density = getApplicationContext().getResources().getDisplayMetrics().density;
        return Math.round((float)dp * density);
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}

package nok.easy2m;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Random;

/**
 * Created by naordalal on 25/02/2018.
 */

public class NotificationManager
{
    private final Context context;
    private final Uri fileUri;
    private final String fileName;

    public NotificationManager(Context context , Uri fileUri , String fileName)
    {
        this.context = context;
        this.fileUri = fileUri;
        this.fileName = fileName;
    }

    public void send()
    {
        android.app.NotificationManager mNotifyManager = (android.app.NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = new Random().nextInt();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setData(this.fileUri);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        int requestCode = 0;//My request code
        PendingIntent pendingIntent = PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,"CHANNEL_ID");
        mBuilder.setContentText("Download complete " + this.fileName)
                // Removes the progress bar
                .setProgress(0,0,false)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        mNotifyManager.notify(id, mBuilder.build());
    }

}

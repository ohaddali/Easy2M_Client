package nok.easy2m;

import android.content.pm.PackageManager;

/**
 * Created by naordalal on 20/02/2018.
 */

public class Globals
{

    public static String companiesImagesContainer = "images";


    public static final int READ_CONTACTS = 1;
    public static final int SEND_SMS = 2;
    private static boolean GetContactsAllow = false;
    private static boolean SendSmsAllow = false;

    public static void onRequestPermissionsResult(int requestCode,
                                                  String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case READ_CONTACTS:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    Globals.GetContactsAllow = true;
                else
                    Globals.GetContactsAllow = false;

                return;
            }
            case SEND_SMS:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    Globals.SendSmsAllow = true;
                else
                    Globals.SendSmsAllow = false;

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

package nok.easy2m;

import android.content.pm.PackageManager;

/**
 * Created by naordalal on 20/02/2018.
 */

public class Globals
{
    public static String addRole = "addNewRole";

    public static String companiesImagesContainer = "images";
    public static String reportsContainer = "reports";

    public static String imagesBlobUrl ="https://nokstorage.blob.core.windows.net/images/";

    public static final int READ_CONTACTS_CODE = 1;
    public static final int SEND_SMS_CODE = 2;
    public static final int WRITE_EXTERNAL_STORAGE_CODE = 3;
    public static boolean GetContactsAllow = false;
    public static boolean SendSmsAllow = false;
    public static boolean writeAllow = false;

    public static void onRequestPermissionsResult(int requestCode,
                                                  String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case READ_CONTACTS_CODE:
            {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    Globals.GetContactsAllow = true;
                else
                    Globals.GetContactsAllow = false;

                return;
            }
            case SEND_SMS_CODE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    Globals.SendSmsAllow = true;
                else
                    Globals.SendSmsAllow = false;

                return;
            }
            case WRITE_EXTERNAL_STORAGE_CODE:
            {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                    Globals.writeAllow = true;
                else
                    Globals.writeAllow = false;

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

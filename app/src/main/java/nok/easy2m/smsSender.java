package nok.easy2m;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by naordalal on 20/02/2018.
 */

public class smsSender
{
    public void send(String phoneNumber , String sms)
    {

        Thread t = new Thread(() -> {
            try
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber , null , sms , null , null);
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        });

        t.start();
    }


    public List<Contact> getAllMyContacts(Activity activity)
    {
        List<Contact> contacts = new ArrayList<>();
        ContentResolver resolver = activity.getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI , null , null , null ,  ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC");

        while (cursor.moveToNext())
        {
            List<String> phoneNumbers = new ArrayList<>();
            List<String> emails = new ArrayList<>();

            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI , null
                    , ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?" , new String[]{ id } , null);

            while (phoneCursor.moveToNext())
            {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumbers.add(phoneNumber);
            }

            Cursor emailCursor = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI , null
                    , ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?" , new String[]{ id } , null);

            while (emailCursor.moveToNext())
            {
                String email = emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                emails.add(email);
            }

            Contact contact = new Contact(id , name , phoneNumbers , emails);
            contacts.add(contact);
        }

        return  contacts;
    }
}

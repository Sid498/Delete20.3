package com.example.sid.delete203;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    EditText etNumber;
    Button btnDelete;
    ContentResolver contentResolver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        contentResolver= getContentResolver();
        etNumber = (EditText) findViewById(R.id.etNumber);
        btnDelete = (Button) findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItem();
            }
        });
    }

    private void deleteItem()
    {
        //Checking for permission.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS}, 100);
        }

        //Checking that all Details .
        if(!etNumber.getText().toString().isEmpty())
        {
            //calling delete() method.
            delete(contentResolver,etNumber.getText().toString());
            Toast.makeText(getApplicationContext(),"Contact Deleted",Toast.LENGTH_SHORT).show();
            //launching contact application when user delete number and click on delete button
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivity(intent);
        }
        else
        {
            //Displaying Toast.
            Toast.makeText(getApplicationContext(),"Please Fill Correct Phone Nuumber",Toast.LENGTH_SHORT).show();
        }
    }

    //update method.
    public static void delete(ContentResolver contentResolver,String numberToDelete)
    {
        //ArrayList of ContentProviderOperation class.
        ArrayList<ContentProviderOperation> operationArrayList = new ArrayList<>();

        String contactID = String.valueOf(getContactID(contentResolver,numberToDelete));



        //Creating Arguements array.
        String[] phoneArgs = new String[]{contactID};

        //deleting Contact.
        operationArrayList.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(ContactsContract.RawContacts._ID + "=?",phoneArgs)
                .build());

        try
        {

            contentResolver.applyBatch(ContactsContract.AUTHORITY,operationArrayList);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (OperationApplicationException e)
        {
            e.printStackTrace();
        }

    }

    //Method to get ID of Contact.
    private static long getContactID(ContentResolver contentResolver,String number)
    {

        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = { ContactsContract.PhoneLookup._ID };   //Creating projection.
        Cursor cursor=null;   //creating reference of Cursor.

        try
        {

            cursor = contentResolver.query(contactUri, projection, null, null,null);

            if (cursor != null && cursor.moveToFirst())
            {

                int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                return cursor.getLong(personID);
            }

            return -1;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (cursor != null) {
                cursor.close();
            }
        }
        return -1;
    }
}

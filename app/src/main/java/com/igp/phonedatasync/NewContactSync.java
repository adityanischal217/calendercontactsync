package com.igp.phonedatasync;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.igp.phonedatasync.model.ContactsInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewContactSync extends AppCompatActivity {
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    MyCustomAdapter dataAdapter = null;
    ListView listView;
    List<ContactsInfo> contactsInfoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact_sync);
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        listView = (ListView) findViewById(R.id.lstContacts);
        try {
            requestContactPermission();
        } catch (ParseException e) {
            Log.v("hiii", e.getMessage());
            e.printStackTrace();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class AsyncTaskRunner extends AsyncTask<String, String, String> {
        private String resp;
        // ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                try {
                    getContacts();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            dataAdapter = new MyCustomAdapter(NewContactSync.this, R.layout.contact_info, contactsInfoList);
            listView.setAdapter(dataAdapter);
            findViewById(R.id.progress).setVisibility(View.GONE);
        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {
            // finalResult.setText(text[0]);

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint({"Range", "SimpleDateFormat"})
    private void getContacts() throws ParseException {
        ContentResolver contentResolver = getContentResolver();
        String contactId = null;
        String displayName = null;
        contactsInfoList = new ArrayList<>();
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {
                    ContactsInfo contactsInfo = new ContactsInfo();
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                    contactsInfo.setContactId(contactId);
                    contactsInfo.setDisplayName(displayName);
                    Cursor emailCur = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                            new String[]{contactId}, null);
                    while (emailCur.moveToNext()) {
                        String email = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                        if (email != null) {
                            contactsInfo.setEmail(email);
                        }
                        // Here you will get list of email

                    }
                    emailCur.close();
                    //phone cursor
                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);

                    if (phoneCursor.moveToNext()) {
                        String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactsInfo.setPhoneNumber(phoneNumber);
                    }

                    phoneCursor.close();
                    // birthday cursor
                    @SuppressLint("Recycle") Cursor bdc = getContentResolver().
                            query(android.provider.ContactsContract.Data.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Event.DATA},
                                    android.provider.ContactsContract.Data.CONTACT_ID + " = " + contactId +
                                            " AND " + ContactsContract.Data.MIMETYPE + " = '" +
                                            ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "' AND " +
                                            ContactsContract.CommonDataKinds.Event.TYPE + " = " +
                                            ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY,
                                    null, android.provider.ContactsContract.Data.DISPLAY_NAME);

                    while (bdc.moveToNext()) {
                        String birthday = bdc.getString(bdc.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
                        @SuppressLint("SimpleDateFormat") DateFormat inputFormat = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            inputFormat = new SimpleDateFormat("YYYYMMDD");
                        }
                        @SuppressLint("SimpleDateFormat") DateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");

                        Date date = null;
                        try {
                            if (inputFormat != null) {
                                date = inputFormat.parse(birthday);
                            }
                            if (date != null) {
                                String startDateStrNewFormat = outputFormat.format(date);
                                contactsInfo.setBirthDate(startDateStrNewFormat);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    bdc.close();
                    //email
                    contactsInfoList.add(contactsInfo);
                }

            }
        }
        cursor.close();


    }


    public void requestContactPermission() throws ParseException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // getContacts();
                    AsyncTaskRunner runner = new AsyncTaskRunner();
                    runner.execute();
                }
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                //    getContacts();
                AsyncTaskRunner runner = new AsyncTaskRunner();
                runner.execute();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //  getContacts();
                        AsyncTaskRunner runner = new AsyncTaskRunner();
                        runner.execute();
                    }
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}
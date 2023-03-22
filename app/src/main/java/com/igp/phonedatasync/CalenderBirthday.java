package com.igp.phonedatasync;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class CalenderBirthday extends AppCompatActivity {
    private static final int PERMISSION_GRANTED = 1;
    public static final int RequestPermissionCode = 1;
    static Cursor cursor;
    ListView listView;
    ArrayList<String> StoreContacts;
    ArrayAdapter<String> arrayAdapter;
    private static final String DEBUG_TAG = "MyActivity";
    public static final String[] INSTANCE_PROJECTION = new String[]{
            CalendarContract.Instances.EVENT_ID,      // 0
            CalendarContract.Instances.BEGIN,         // 11
            CalendarContract.Instances.TITLE          // 2
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_TITLE_INDEX = 2;


    ArrayList<String> nameOfEvent = new ArrayList<String>();
    ArrayList<String> startDates = new ArrayList<String>();
    ArrayList<String> endDates = new ArrayList<String>();
    ArrayList<String> descriptions = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.listview1);
        findViewById(R.id.progress).setVisibility(View.VISIBLE);
        findViewById(R.id.listview1).setVisibility(View.GONE);
        StoreContacts = new ArrayList<>();
        EnableRuntimePermission();

    }

    @SuppressLint("Range")
    private void readAllEventsCalender() {
        ContentResolver cr = getContentResolver();
        Uri caluri = CalendarContract.Events.CONTENT_URI;
        Uri atteuri = CalendarContract.Attendees.CONTENT_URI;
        Cursor cur1, cur2;
        String all = null;
        String event_Title = null;

        try {
            cur1 = cr.query(caluri
                    , new String[]{CalendarContract.Events.CALENDAR_ID, CalendarContract.Events._ID, CalendarContract.Events.TITLE, CalendarContract.Events.DESCRIPTION, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.EVENT_LOCATION}
                    , null, null, null);
            if (cur1 != null) {
                while (cur1.moveToNext()) {
                    try {
                        event_Title = cur1.getString(cur1.getColumnIndex(CalendarContract.Events.TITLE));
                        String event_Desc = cur1.getString(cur1.getColumnIndexOrThrow(CalendarContract.Events.DESCRIPTION));
                        @SuppressLint("Range") Date event_Start = new Date(cur1.getLong(cur1.getColumnIndex(CalendarContract.Events.DTSTART)));
                        @SuppressLint("Range") Date event_end = new Date(cur1.getLong(cur1.getColumnIndex(CalendarContract.Events.DTEND)));
                        @SuppressLint("Range") String event_loc = cur1.getString(cur1.getColumnIndex(CalendarContract.Events.EVENT_LOCATION));
                        String all_attendee = null;
                        String all_Emails = null;
                        if (event_Title.contains("birthday") || event_Title.contains("Birthday") || event_Title.contains("anni") || event_Title.contains("Anni")) {
                            //  StoreContacts.add(event_Title);
                            StoreContacts.add("Event title: " + event_Title + "\n"
                                    + "Event Description: " + event_Desc + "\n"
                                    + "Event Start: " + event_Start + "\n" + "Events End: "
                                    + event_end);
                        }

                        @SuppressLint("Range") String cal_ID = cur1.getString(cur1.getColumnIndex(CalendarContract.Events.CALENDAR_ID));
                        @SuppressLint("Range") String event_ID = cur1.getString(cur1.getColumnIndex(CalendarContract.Events._ID));
                        cur2 = cr.query(atteuri, new String[]{CalendarContract.Attendees.ATTENDEE_NAME, CalendarContract.Attendees.ATTENDEE_EMAIL}
                                , CalendarContract.Attendees.EVENT_ID + "=" + event_ID, null, null);
                        if (cur2 != null) {
                            while (cur2.moveToNext()) {
                                @SuppressLint("Range") String attendee_name = cur2.getString(cur2.getColumnIndex(CalendarContract.Attendees.ATTENDEE_NAME));
                                @SuppressLint("Range") String attendee_Email = cur2.getString(cur2.getColumnIndex(CalendarContract.Attendees.ATTENDEE_EMAIL));
                                all_attendee += "\n" + attendee_name;
                                all_Emails += "\n" + attendee_Email;
                            }
                            cur2.close();
                        }
                        all += "Event title: " + event_Title + "\n"
                                + "Event Description: " + event_Desc + "\n"
                                + "Event Start: " + event_Start + "\n" + "Events End: "
                                + event_end + "\n" + "Event Location: " + event_loc
                                + "\n" + "Attendees: " + "\n" + all_attendee + "\n"
                                + "Emails: " + "\n" + all_Emails + "\n";
                        // all += "Event title: " + event_Title + "\n";
                        // GetContactsIntoArrayList();


                    } catch (IllegalArgumentException e) {
                        // e.printStackTrace();
                        Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                }
                cur1.close();
            } else {
                Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
            }


           /* System.out.println("My log--------" + all);
            Log.v("events_track", all);*/
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }
        if (StoreContacts.size() > 0) {
            arrayAdapter = new ArrayAdapter<>(
                    CalenderBirthday.this,
                    R.layout.contact_items_listview,
                    R.id.textview, StoreContacts
            );

            listView.setAdapter(arrayAdapter);
        } else {
            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
        }
        findViewById(R.id.progress).setVisibility(View.GONE);
        findViewById(R.id.listview1).setVisibility(View.VISIBLE);
    }


    public void readCalendarEvent() {
        Cursor cursor = getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[]{"calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation"}, null,
                        null, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        nameOfEvent.clear();
        startDates.clear();
        endDates.clear();
        descriptions.clear();
        for (int i = 0; i < CNames.length; i++) {

            nameOfEvent.add(cursor.getString(1));
            startDates.add(getDate(Long.parseLong(cursor.getString(3))));
            endDates.add(getDate(Long.parseLong(cursor.getString(4))));
            descriptions.add(cursor.getString(2));
            CNames[i] = cursor.getString(1);
            cursor.moveToNext();

        }
        Log.v("track", String.valueOf(nameOfEvent));

    }

    private void checkPermission(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
            readCalendarEvent();
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }

    public String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "dd/MM/yyyy hh:mm:ss a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public void EnableRuntimePermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_CALENDAR)) {
            readAllEventsCalender();
            //  Toast.makeText(this, "CONTACTS permission allows us to Access CONTACTS app", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.READ_CALENDAR}, RequestPermissionCode);

        }
    }

    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        super.onRequestPermissionsResult(RC, per, PResult);
        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    readAllEventsCalender();
                    // Toast.makeText(MainActivity.this, "Permission Granted, Now your application can access CONTACTS.", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(CalenderBirthday.this, "Permission Canceled, Now your application cannot access CONTACTS.", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

}




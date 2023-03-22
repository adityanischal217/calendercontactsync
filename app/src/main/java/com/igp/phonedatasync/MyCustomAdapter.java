package com.igp.phonedatasync;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.igp.phonedatasync.model.ContactsInfo;

import java.util.List;

public class MyCustomAdapter extends ArrayAdapter {

    private final List contactsInfoList;
    private final Context context;

    public MyCustomAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
        this.contactsInfoList = objects;
        this.context = context;
    }

    private class ViewHolder {
        TextView displayName;
        TextView phoneNumber, email, birthDay, anniversaryDay;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.contact_info, null);

            holder = new ViewHolder();
            holder.displayName = (TextView) convertView.findViewById(R.id.displayName);
            holder.phoneNumber = (TextView) convertView.findViewById(R.id.phoneNumber);
            holder.email = (TextView) convertView.findViewById(R.id.email);
            holder.birthDay = (TextView) convertView.findViewById(R.id.birthday);
            //  holder.anniversaryDay = (TextView) convertView.findViewById(R.id.anniversay);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ContactsInfo contactsInfo = (ContactsInfo) contactsInfoList.get(position);
        holder.displayName.setText("Name - " + contactsInfo.getDisplayName());
        holder.phoneNumber.setText("Phone - " + contactsInfo.getPhoneNumber());
        if (contactsInfo.getEmail() == null || contactsInfo.getEmail().equalsIgnoreCase("")) {
            holder.email.setVisibility(View.GONE);
        } else {
            holder.email.setVisibility(View.VISIBLE);
            holder.email.setText("Email -" + contactsInfo.getEmail());
        }
        if (contactsInfo.getBirthDate() == null || contactsInfo.getBirthDate().equalsIgnoreCase("")) {
            holder.birthDay.setVisibility(View.GONE);
        } else {
            holder.birthDay.setVisibility(View.VISIBLE);
            holder.birthDay.setText("Birthday - " + contactsInfo.getBirthDate());
        }


        //   holder.anniversaryDay.setText(contactsInfo.getAnniversaryDate());

        return convertView;
    }
}
package com.example.e_commerceapp2.ui.home;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.e_commerceapp2.AlarmReceiver;
import com.example.e_commerceapp2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

import static android.app.PendingIntent.FLAG_CANCEL_CURRENT;
import static android.content.Context.ALARM_SERVICE;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private FloatingActionButton addEvent;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private EditText addEventMessage, addEventDate, addEventTime;
    private Button datePicker, timePicker, addEventCancel, addEventSave;
    private int setYear, setMonth, setDay, setHour, setMinute;
    private int notificationId = 1;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        addEvent = (FloatingActionButton) root.findViewById(R.id.addEvent);
        addEvent.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                addEventDialog();
            }
        });
        return root;
    }

    //create pop-up
    public void addEventDialog(){
        dialogBuilder = new AlertDialog.Builder(getContext());
        final View eventPopupView = getLayoutInflater().inflate(R.layout.popup, null);
        addEventMessage = (EditText) eventPopupView.findViewById(R.id.addEventMessage);
        datePicker = (Button) eventPopupView.findViewById(R.id.addDateBtn);
        timePicker = (Button) eventPopupView.findViewById(R.id.addTimeBtn);
        addEventDate = (EditText) eventPopupView.findViewById(R.id.addEventDate);
        addEventTime = (EditText) eventPopupView.findViewById(R.id.addEventTime);
        addEventCancel = (Button) eventPopupView.findViewById(R.id.cancelBtn);
        addEventSave = (Button) eventPopupView.findViewById(R.id.saveBtn);

        dialogBuilder.setView(eventPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        datePicker.setOnClickListener(this::onClick);
        timePicker.setOnClickListener(this::onClick);
        addEventCancel.setOnClickListener(this::onClick);
        addEventSave.setOnClickListener(this::onClick);

    }

    //on-click for dialog view
    public void onClick(View view){
        //Set notificationId & text
        Intent intent = new Intent (getContext(), AlarmReceiver.class);
        intent.putExtra("notificationId", notificationId);
        intent.putExtra("message", addEventMessage.getText().toString());

        // getBroadcast(context, requestCode, intent, flags)
        PendingIntent alarmIntent = PendingIntent.getBroadcast(getContext(), 0, intent, FLAG_CANCEL_CURRENT);

        AlarmManager alarm = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

        // Date picker
        if (view == datePicker) {
            //set custom date to DatePickerDialog
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                    setYear = year;
                    setMonth = monthOfYear;
                    setDay = dayOfMonth;
                    addEventDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                }
            }
                    , calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        }
        // Time picker
        if (view == timePicker) {
            //set custom time to TimePickerDialog
            final Calendar calendar = Calendar.getInstance();
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    setHour = hourOfDay;
                    setMinute = minute;
                    addEventTime.setText(hourOfDay + ":" + minute);

                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        }
        //Save event
        if (view == addEventSave) {
            Log.d("TIME","Notification set at: "+ setMinute);
            if (addEventMessage.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Please Enter event message", Toast.LENGTH_SHORT).show();
            } else {
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.YEAR, setYear);
                startTime.set(Calendar.MONTH, setMonth);
                startTime.set(Calendar.DAY_OF_MONTH, setDay);
                startTime.set(Calendar.HOUR_OF_DAY, setHour);
                startTime.set(Calendar.MINUTE, setMinute);
                startTime.set(Calendar.SECOND, 0);
                long alarmStartTime = startTime.getTimeInMillis();

                //set alarm
                alarm.set(AlarmManager.RTC_WAKEUP, alarmStartTime, alarmIntent);
                Toast.makeText(getContext(), "Notification set", Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        }
        //Cancel event
        if (view == addEventCancel) {
            dialog.dismiss();
        }
    }
}
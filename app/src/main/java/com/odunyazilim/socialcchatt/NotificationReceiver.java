package com.odunyazilim.socialcchatt;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String messag = intent.getStringExtra("toastMessage");

        Toast.makeText(context, messag, Toast.LENGTH_SHORT).show();

    }
}

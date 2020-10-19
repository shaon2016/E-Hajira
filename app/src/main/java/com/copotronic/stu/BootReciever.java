package com.copotronic.stu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.copotronic.stu.activities.SplashActivity;

import java.util.Objects;

public class BootReciever extends BroadcastReceiver {

@Override
public void onReceive(Context context, Intent intent) {

    if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
        Intent i = new Intent(context, SplashActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
 }}
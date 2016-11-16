package com.mahoucoder.misakagate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by jamesji on 14/11/2016.
 */

public class CheckSubscriptionReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, SubscriptionService.class);
        context.startService(i);
    }
}

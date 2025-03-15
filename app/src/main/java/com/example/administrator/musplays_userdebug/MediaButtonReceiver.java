package com.example.administrator.musplays_userdebug;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationManagerCompat;

public class MediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null && Player.exp != null) {
            switch (action) {
                case "ACTION_PLAY":
                    Player.exp.play();
                    break;
                case "ACTION_PAUSE":
                    Player.exp.pause();
                    break;
                case "ACTION_NEXT":
                    // Call playNextSong through a service or static method
                    break;
                case "ACTION_PREVIOUS":
                    // Call playPreviousSong through a service or static method
                    break;
            }
        }
    }
}

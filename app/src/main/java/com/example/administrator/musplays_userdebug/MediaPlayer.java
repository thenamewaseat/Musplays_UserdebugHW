package com.example.administrator.musplays_userdebug;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.session.CommandButton;
import androidx.media3.session.MediaSession;
import androidx.media3.session.MediaSessionService;

/*public class PlaybackService extends MediaSessionService {
    private static final SessionCommand CUSTOM_COMMAND_FAVORITES =
            new SessionCommand("ACTION_FAVORITES", Bundle.EMPTY);
    @Nullable
    private MediaSession mediaSession;

    public void onCreate() {
        super.onCreate();
        CommandButton favoriteButton =
                new CommandButton.Builder()
                        .setDisplayName("Save to favorites")
                        .setIconResId(R.drawable.favorite_icon)
                        .setSessionCommand(CUSTOM_COMMAND_FAVORITES)
                        .build();
        Player player = new ExoPlayer.Builder(this).build();
        // Build the session with a custom layout.
        mediaSession =
                new MediaSession.Builder(this, player)
                        .setCallback(new MyCallback())
                        .setCustomLayout(ImmutableList.of(favoriteButton))
                        .build();
    }

    private static class MyCallback implements MediaSession.Callback {
        @Override
        public ConnectionResult onConnect(
                MediaSession session, MediaSession.ControllerInfo controller) {
            // Set available player and session commands.
            return new AcceptedResultBuilder(session)
                    .setAvailablePlayerCommands(
                            ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                                    .remove(COMMAND_SEEK_TO_NEXT)
                                    .remove(COMMAND_SEEK_TO_NEXT_MEDIA_ITEM)
                                    .remove(COMMAND_SEEK_TO_PREVIOUS)
                                    .remove(COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM)
                                    .build())
                    .setAvailableSessionCommands(
                            ConnectionResult.DEFAULT_SESSION_COMMANDS.buildUpon()
                                    .add(CUSTOM_COMMAND_FAVORITES)
                                    .build())
                    .build();
        }

        public ListenableFuture onCustomCommand(
                MediaSession session,
                MediaSession.ControllerInfo controller,
                SessionCommand customCommand,
                Bundle args) {
            if (customCommand.customAction.equals(CUSTOM_COMMAND_FAVORITES.customAction)) {
                // Do custom logic here
                saveToFavorites(session.getPlayer().getCurrentMediaItem());
                return Futures.immediateFuture(new SessionResult(SessionResult.RESULT_SUCCESS));
            }
            return MediaSession.Callback.super.onCustomCommand(
                    session, controller, customCommand, args);
        }
    }
}*/

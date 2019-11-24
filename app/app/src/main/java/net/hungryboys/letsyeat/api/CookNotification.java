package net.hungryboys.letsyeat.api;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import net.hungryboys.letsyeat.R;
import net.hungryboys.letsyeat.data.RecipeID;
import net.hungryboys.letsyeat.login.LoginRepository;
import net.hungryboys.letsyeat.recipe.RecipeActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Helper class for showing and canceling cook
 * notifications.
 * <p>
 * This class makes heavy use of the {@link NotificationCompat.Builder} helper
 * class to create notifications in a backward-compatible way.
 */
public class CookNotification  extends FirebaseMessagingService{

    private static final String TAG_FIREBASE = "CookNotification";

    private static final String KEY_RECIPE_NAME = "name";
    private static final String KEY_RECIPE_ID = "id";

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG_FIREBASE, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getData().size() > 0) {
            String name = remoteMessage.getData().get(KEY_RECIPE_NAME);
            RecipeID id = new RecipeID(remoteMessage.getData().get(KEY_RECIPE_ID));

            sendNotification(name, id);
        }


    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.d(TAG_FIREBASE, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);

    }
    // [END on_new_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        LoginRepository loginRepository = LoginRepository.getInstance(getApplicationContext());

        if (loginRepository.isLoggedIn()) {
            Call<String> call = APICaller.getApiCall().updateFirebaseToken(token, loginRepository.getUserEmail());
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG_FIREBASE, "Token sent successfuly");
                    } else {
                        Log.e(TAG_FIREBASE, "Error when sending token: " + response.message());
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(TAG_FIREBASE, "Error when sending token", t);
                }
            });

        }
    }

    /**
     * Show notification for given recipe name. When clicked, it launches that recipe's activity
     * @param recipeName name of the recipe
     * @param recipeID id of the recipe
     */
    private void sendNotification(String recipeName, RecipeID recipeID) {
        Intent intent = new Intent(this, RecipeActivity.class);
        intent.putExtra(RecipeActivity.EXTRA_RECIPE_ID, recipeID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_groceries_black_24dp)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(recipeName)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager == null) {
            Log.e(TAG_FIREBASE, "No notification manager found");
            return;
        }

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(recipeID.getId().hashCode(), notificationBuilder.build());
    }

}

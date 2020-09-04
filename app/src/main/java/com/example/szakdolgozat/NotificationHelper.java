package com.example.szakdolgozat;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NotificationHelper {
    private static String CHANNEL_ID = "Szakdolgozat";
    private static String URL = "https://fcm.googleapis.com/fcm/send";

    public static void createNotificationChannel(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            String CHANNEL_ID = "Szakdolgozat";
            CharSequence name = "Szakdolgozat";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            notificationManager.createNotificationChannel(mChannel);
        }
    }



    public static void displayNotification(Context context, String title, String body)
    {
        if(!(context instanceof PrivateChatViewActivity))
        {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                    .setSmallIcon(R.drawable.cart)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(context);
            mNotificationManager.notify(1,mBuilder.build());
        }

    }

    public static void sendNotification(Context context, String token, String title, String body)
    {
        final RequestQueue mRequestQue = Volley.newRequestQueue(context);

        JSONObject mainObj = new JSONObject();
        try
        {
            mainObj.put("to", token);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", title);
            notificationObj.put("body",  body);
            mainObj.put("notification",notificationObj);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL, mainObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> header = new HashMap<>();
                    header.put("Content-Type","application/json");
                    header.put("Authorization", "key=AAAAQbPpA1A:APA91bHCgR72W4tUD7evh72kQad7sHDbPNJAaaweiTLHbvwlPeWt2VhOvqg502Sgi5_43_QIdR2WyFnYYuD4zyXvpu-e93IHwxjsVzNsHUxtFYBgKJYrBH8dDwPQK3FfFqZnfjfsnUUu");

                    return header;
                }
            };
            mRequestQue.add(request);
            displayNotification(context, title, body);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

}

package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PrivateChatViewActivity extends AppCompatActivity {

    private Button sendMessageButton;
    private EditText input_message;
    private TextView chat_conversation, roomname;
    private PrivateChatDetails privateChatDetails;
    private DatabaseReference rootRef;
    private String chat_message,sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat_view);
        privateChatDetails = (PrivateChatDetails)getIntent().getSerializableExtra("privateChatDetails");

        sendMessageButton = (Button) findViewById(R.id.send_message_button);
        input_message = (EditText) findViewById(R.id.message_edittext);
        chat_conversation = (TextView) findViewById(R.id.viewmessages_textview);
        roomname = (TextView) findViewById(R.id.roomname_textview);
        roomname.setText(privateChatDetails.toString());


        rootRef = FirebaseDatabase.getInstance().getReference().child("PrivateChat").child(privateChatDetails.getRoomID()).child("Messages");

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,Object> map = new HashMap<String, Object>();
                String temp_key = rootRef.push().getKey();
                rootRef.updateChildren(map);

                DatabaseReference message_root = rootRef.child(temp_key);
                Map<String,Object> map2 = new HashMap<String,Object>();
                map2.put("sender", CurrentUsers.currentOnlineUser.getEmail());
                map2.put("message", input_message.getText().toString());
                message_root.updateChildren(map2);



                input_message.getText().clear();
            }
        });
        rootRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                append_chat(snapshot);
                if (!sender.equals(CurrentUsers.currentOnlineUser.getEmail()))
                {
                    sendNotification(sender,chat_message);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                append_chat(snapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void append_chat(DataSnapshot snapshot)
    {
        Iterator i = snapshot.getChildren().iterator();

        while (i.hasNext())
        {
            chat_message = (String) ((DataSnapshot)i.next()).getValue();
            sender = (String) (((DataSnapshot) i.next()).getValue());

            if(sender.equals(CurrentUsers.currentOnlineUser.getEmail()))
            {
                chat_conversation.append(Html.fromHtml("<font color='blue'>".concat(sender+ ": ").concat("</font>").concat(chat_message).concat("<br>")));
            }
            else {
                chat_conversation.append(Html.fromHtml("<font color='green'>".concat(sender+": ").concat("</font>").concat(chat_message).concat("<br>")));
            }

        }
    }
    private void sendNotification(final String sender, final String message)
    {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        String email;
        if(privateChatDetails.getpartner().equals(CurrentUsers.currentOnlineUser.getEmail()))
        {
            email = privateChatDetails.getcreator();
        }
        else{
            email = privateChatDetails.getpartner();
        }
        rootRef.child("Users").orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String token = ds.child("token").getValue(String.class);
                    NotificationHelper.sendNotification(PrivateChatViewActivity.this, token, sender, message);
                    Log.i("asd", token);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
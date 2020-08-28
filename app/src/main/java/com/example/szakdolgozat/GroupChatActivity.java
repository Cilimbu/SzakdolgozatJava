package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroupOverlay;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GroupChatActivity extends AppCompatActivity {

    private Button addRoomButton;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listofRooms = new ArrayList<String>();
    private DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
    private String inputText="";
    private String inputText2="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        addRoomButton = (Button) findViewById(R.id.add_room_button);
        listView = (ListView) findViewById(R.id.chatroom_listview);

        arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_items, listofRooms);
        listView.setAdapter(arrayAdapter);
        RefreshList();
        addRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(GroupChatActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                final AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this);
                builder.setTitle("Adja meg a szoba nevét!");
                builder.setView(input);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        inputText = input.getText().toString();
                        GivePasswordForRoom(inputText);
                    }
                });
                builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                ChatRoomDetails roomDetails = (ChatRoomDetails) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(GroupChatActivity.this, ChatViewActivity.class);
                intent.putExtra("roomDetails", roomDetails);
                startActivity(intent);
            }
        });
    }
    public void Upload(String inputText, String inputText2)
    {
        String roomID = RootRef.push().getKey();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("creator", CurrentUsers.currentOnlineUser.getEmail());
        map.put("roomname", inputText);
        map.put("roomID", roomID);
        map.put("roomPass", inputText2);
        RootRef.child("Chatrooms").child(roomID).updateChildren(map);
    }
    public void RefreshList() {
        DatabaseReference reference;
        reference=FirebaseDatabase.getInstance().getReference().child("Chatrooms");
        final ArrayList<ChatRoomDetails> arrayList = new ArrayList<ChatRoomDetails>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter<ChatRoomDetails>(this, R.layout.list_items, R.id.textt, arrayList);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    ChatRoomDetails temp = new ChatRoomDetails();
                    String roomname = ds.child("roomname").getValue(String.class);
                    temp.setRoomname(roomname);
                    String ID = ds.child("roomID").getValue(String.class);
                    temp.setID(ID);
                    String creator = ds.child("creator").getValue(String.class);
                    temp.setCreator(creator);
                    String roompass = ds.child("roomPass").getValue(String.class);
                    temp.setRoomPass(roompass);
                    arrayList.add(temp);
                }
                listView.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void GivePasswordForRoom(final String inputText)
    {
        final EditText input2 = new EditText(GroupChatActivity.this);
        input2.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
        final AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this);
        builder.setTitle("Állítsa be a szoba jelszavát!");
        builder.setView(input2);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                inputText2 = input2.getText().toString();
                Upload(inputText, inputText2);
            }
        });
        builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}
package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PrivateChatActivity extends AppCompatActivity {

    private Button addButton;
    private ListView listView;
    private DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
    final ArrayList<PrivateChatDetails> arrayList = new ArrayList<PrivateChatDetails>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);
        listView = (ListView) findViewById(R.id.private_chatroom_listview);
        addButton = (Button) findViewById(R.id.add_newprivate_button);
        RefreshList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final PrivateChatDetails privateChatDetails = (PrivateChatDetails) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(PrivateChatActivity.this, PrivateChatViewActivity.class);
                intent.putExtra("privateChatDetails", privateChatDetails);
                startActivity(intent);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivateChatActivity.this);

                final EditText input = new EditText(PrivateChatActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setTitle("Adja meg az email címét annak, akivel chatelni akar");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String inputText;
                        inputText = input.getText().toString();
                        if(inputText.equals(CurrentUsers.currentOnlineUser.getEmail()))
                        {
                            Toast.makeText(PrivateChatActivity.this, "Magaddal nem indíthatsz chatet!", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            ChatExists(inputText);
                        }
                    }
                });
                builder.show();
            }
        });
    }


    public void Upload(String partner)
    {
        String roomID = RootRef.push().getKey();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("creator", CurrentUsers.currentOnlineUser.getEmail());
        map.put("partner", partner);
        map.put("roomID", roomID);
        RootRef.child("PrivateChat").child(roomID).updateChildren(map);
    }


    public void RefreshList() {
        DatabaseReference reference;
        reference= FirebaseDatabase.getInstance().getReference().child("PrivateChat");
        final ArrayAdapter arrayAdapter = new ArrayAdapter<PrivateChatDetails>(this, R.layout.list_items, R.id.textt, arrayList);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    PrivateChatDetails temp = new PrivateChatDetails();
                    String creator = ds.child("creator").getValue(String.class);
                    temp.setcreator(creator);
                    String partner = ds.child("partner").getValue(String.class);
                    temp.setpartner(partner);
                    String roomID = ds.child("roomID").getValue(String.class);
                    temp.setRoomID(roomID);
                    arrayList.add(temp);
                }
                listView.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PrivateChatActivity.this, "Valami hiba lépett fel, próbálja meg később", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void ChatExists(String partner)
    {
        boolean Exists = false;
        for(PrivateChatDetails temp : arrayList)
        {
            if((temp.getcreator().equals(partner) || temp.getcreator().equals(CurrentUsers.currentOnlineUser.getEmail())) &&
            temp.getpartner().equals(partner) || temp.getpartner().equals((CurrentUsers.currentOnlineUser.getEmail())))
            {
                Toast.makeText(this, "Már létezik ez a chat", Toast.LENGTH_SHORT).show();
                Exists = true;
                break;
            }
        }
        if(!Exists)
        {
            Upload(partner);
        }
    }
}
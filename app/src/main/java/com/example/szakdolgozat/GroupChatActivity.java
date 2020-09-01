package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.renderscript.Sampler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroupOverlay;
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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.nio.file.attribute.GroupPrincipal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GroupChatActivity extends AppCompatActivity {

    private Button addRoomButton, delRoomButton;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> listofRooms = new ArrayList<String>();
    private DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
    private String inputText="";
    private String inputText2="";
    private ArrayList<ChatRoomDetails> ListObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        delRoomButton = (Button) findViewById(R.id.delete_room_button);
        addRoomButton = (Button) findViewById(R.id.add_room_button);
        listView = (ListView) findViewById(R.id.chatroom_listview);

        arrayAdapter = new ArrayAdapter<String>(this,R.layout.list_items, listofRooms);
        ListObj = new ArrayList<ChatRoomDetails>();

        RefreshList();

        delRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> namesList = new ArrayList<String>();
                String currentUser = CurrentUsers.currentOnlineUser.getEmail();

                for(int i=0 ; i< ListObj.size();i++)
                {
                    String name = ListObj.get(i).getCreator();

                    if(name.equals(currentUser))
                    {
                        namesList.add(name);
                    }
                }
                String[] names = new String[namesList.size()];
                names = namesList.toArray(names);
                final ArrayList<Integer> selectedItems = new ArrayList<Integer>();
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this);
                builder.setTitle("Válassza ki mely elemeket szeretné törölni").setMultiChoiceItems(names, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        if (b) {
                            selectedItems.add(i);
                        } else if (selectedItems.contains(i)) {
                            selectedItems.remove(Integer.valueOf(i));
                        }
                    }
                });
                builder.setPositiveButton("Törlés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        for (Integer p : selectedItems) {
                            Delete(ListObj.get(p).getID());
                        }
                        Toast.makeText(GroupChatActivity.this, "Sikeres törlés", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(GroupChatActivity.this, "Törlés megszakítva", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

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
                final ChatRoomDetails roomDetails = (ChatRoomDetails) adapterView.getItemAtPosition(i);
                AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this);
                builder.setTitle("Adja meg a jelszót!");
                final EditText input = new EditText(GroupChatActivity.this);
                input.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String inputText3;
                        inputText3 = input.getText().toString();
                        if(roomDetails.getRoomPass().equals(inputText3))
                        {
                            Intent intent = new Intent(GroupChatActivity.this, ChatViewActivity.class);
                            intent.putExtra("roomDetails", roomDetails);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(GroupChatActivity.this, "Helytelen jelszó", Toast.LENGTH_SHORT).show();
                            dialogInterface.dismiss();
                        }
                    }
                });
                builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(GroupChatActivity.this, "Jelszó megadás megszakítva", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
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
                ListObj.clear();
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
                ListObj = arrayList;
                listView.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(GroupChatActivity.this, "Valami hiba lépett fel, próbálja meg később", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void Delete(String ID)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("PrivateChat").child(ID).removeValue();
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
package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.example.szakdolgozat.Model.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MySharedListsActivity extends AppCompatActivity {

    public ArrayList<UserListDetails> ListObj;
    private String inputText;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_shared_lists);
        listView = (ListView) findViewById(R.id.mysharedlists_list_view);
        ListObj = new ArrayList<UserListDetails>();
        RefreshList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l)
            {

                AlertDialog.Builder builder = new AlertDialog.Builder(MySharedListsActivity.this);
                builder.setTitle("Adja meg a lista nevét!");
                final EditText input = new EditText(MySharedListsActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int j) {
                        inputText = input.getText().toString();
                        if(!(inputText.equals(CurrentUsers.currentOnlineUser.getEmail())))
                        {
                            inputText=inputText.replace(".",",");
                            UserExists(inputText, i);
                        }
                        else {
                            Toast.makeText(MySharedListsActivity.this, "Nem tudod magaddal megosztani a saját listádat", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.cancel();
                    }
                });
                builder.show();

            }
        });
    }

    public void RefreshList()
    {
        DatabaseReference reference;
        reference= FirebaseDatabase.getInstance().getReference().child("Lists");
        final ArrayList<UserListDetails> arrayList = new ArrayList<UserListDetails>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter<UserListDetails>(this,R.layout.list_items, R.id.textt, arrayList);
        reference.orderByChild("email").equalTo(CurrentUsers.currentOnlineUser.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                ListObj.clear();
                for (DataSnapshot ds : snapshot.getChildren())
                {
                    UserListDetails temp = new UserListDetails();
                    String name = ds.child("name").getValue(String.class);
                    temp.setName(name);
                    String ID = ds.child("ID").getValue(String.class);
                    temp.setID(ID);
                    String email = ds.child("email").getValue(String.class);
                    temp.setEmail(email);
                    String listitems = ds.child("listitems").getValue(String.class);
                    temp.setListitems(listitems);
                    String date = ds.child("date").getValue(String.class);
                    temp.setDate(date);
                    String shared = ds.child("shared").getValue(String.class);
                    temp.setShared(shared);
                    if (shared.equals("1"))
                    {
                        arrayList.add(temp);
                    }
                }
                ListObj = arrayList;
                listView.setAdapter(arrayAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MySharedListsActivity.this, "Sajnálom, valami hiba lépett fel, próbálja újra!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void UserExists(final String inputText, final Integer position)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataSnapshot user = snapshot.child("Users").child(inputText);
                if(user.exists())
                {
                    ConnectionExists(inputText, ListObj.get(position).getID());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MySharedListsActivity.this, "Ez a felhasználó nem létezik", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void Upload(String userID, String listID) {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        String connectionID = RootRef.push().getKey();
        HashMap<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("userID", userID);
        userdataMap.put("listID", listID);
        RootRef.child("UserListConnect").child(connectionID).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MySharedListsActivity.this, "Sikeres lista létrehozás!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MySharedListsActivity.this, "Hiba lépett fel a lista létrehozása közben, próbálja meg később", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    public void ConnectionExists(final String userID, final String listID)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.child("UserListConnect").orderByChild("userID").equalTo(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean Exists = false;
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    if(ds.child("listID").getValue().equals(listID))
                    {
                        Exists = true;
                        Toast.makeText(MySharedListsActivity.this, "Ezzel a felhasználóval már megosztottad a listát!", Toast.LENGTH_SHORT).show();
                        break;
                    }
                }

                if (!Exists)
                {
                    Upload(userID, listID);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MySharedListsActivity.this, "Hiba történt, próbálja meg később!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.renderscript.Sampler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MyListActivity extends AppCompatActivity {

    public ArrayList<UserListDetails> ListObj;
    private ListView listView;
    private Button addList, removeList;
    private String inputText ="";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        ListObj = new ArrayList<UserListDetails>();
        RefreshList();
        listView = (ListView) findViewById(R.id.list_view);
        addList = (Button) findViewById(R.id.new_list_button);
        removeList = (Button) findViewById(R.id.delete_list_button);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                UserListDetails ListDetails = (UserListDetails) adapterView.getItemAtPosition(i);
                Share(ListDetails.getID(), i);
                return true;
            }
        });

        removeList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] names = new String[ListObj.size()];
                for(int i=0 ; i< ListObj.size();i++)
                {
                    names[i] = ListObj.get(i).getName();
                }
                final ArrayList<Integer> selectedItems = new ArrayList<Integer>();
                AlertDialog.Builder builder = new AlertDialog.Builder(MyListActivity.this);
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
                        Toast.makeText(MyListActivity.this, "Sikeres törlés", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MyListActivity.this, "Törlés megszakítva", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                UserListDetails ListDetails = (UserListDetails) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(MyListActivity.this,ListViewActivity.class);
                intent.putExtra("ListDetails",ListDetails);
                startActivity(intent);
            }
        });

        addList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MyListActivity.this);
                builder.setTitle("Adja meg a lista nevét!");
                final EditText input = new EditText(MyListActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        inputText = input.getText().toString();
                        Upload(inputText);
                        RefreshList();
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


    }
    public void RefreshList()
    {
        DatabaseReference reference;
        reference=FirebaseDatabase.getInstance().getReference().child("Lists");
        final ArrayList<UserListDetails> arrayList = new ArrayList<UserListDetails>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter<UserListDetails>(this,R.layout.list_items, R.id.textt, arrayList)
        {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                UserListDetails currentList = ListObj.get(position);
                ((TextView) view.findViewById(R.id.textt)).setTextColor((currentList.getShared().equals("0") ? Color.RED : Color.GREEN));

                return view;
            }
        };
        reference.orderByChild("email").equalTo(CurrentUsers.currentOnlineUser.getEmail()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                ListObj.clear();
                for(DataSnapshot ds : snapshot.getChildren())
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
                    arrayList.add(temp);

                }
                ListObj = arrayList;
                listView.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MyListActivity.this, "Sajnálom, valami hiba lépett fel, próbálja újra!", Toast.LENGTH_SHORT).show();
            }
        });

        //arrayList.add();
    }
    public void Upload(String name)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String ListID = RootRef.push().getKey();
        String ListItem = "";
        String shared = "0";
        HashMap<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("email", CurrentUsers.currentOnlineUser.getEmail());
        userdataMap.put("ID", ListID);
        userdataMap.put("name", name);
        userdataMap.put("listitems", ListItem);
        userdataMap.put("shared", shared);
        userdataMap.put("date", formatter.format(date));
        RootRef.child("Lists").child(ListID).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MyListActivity.this, "Sikeres lista létrehozás!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MyListActivity.this, "Hiba lépett fel a lista létrehozása közben, próbálja meg később", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
    public void Delete(String ID)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Lists").child(ID).removeValue();
    }
    public void Share(String ID, Integer index)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        final HashMap<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("shared", (ListObj.get(index).getShared().equals("0") ? "1": "0"));
        RootRef.child("Lists").child(ID).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if(userdataMap.get("shared").equals("0"))
                    {
                        Toast.makeText(MyListActivity.this, "Sikeres megosztás!", Toast.LENGTH_SHORT).show();
                    }
                    else
                        {
                            Toast.makeText(MyListActivity.this, "Már nem osztod meg a listát.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(MyListActivity.this, "Hiba lépett fel a lista létrehozása közben, próbálja meg később", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }
}
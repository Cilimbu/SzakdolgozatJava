package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.szakdolgozat.CurrentOnline.CurrentUsers;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Ref;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ListViewActivity extends AppCompatActivity {

    ListView listView;
    UserListDetails ListDetails;
    Button addNewListItem, delListItems;
    private String inputText;
    TextView textView1,textView2;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        addNewListItem = (Button)findViewById(R.id.add_new_list_item_button);
        listView = (ListView) findViewById(R.id.list_view_listview);
        ListDetails = (UserListDetails)getIntent().getSerializableExtra("ListDetails");
        textView1 = (TextView) findViewById(R.id.nametextView);
        textView2 = (TextView) findViewById(R.id.datetextView);
        delListItems = (Button) findViewById(R.id.del_list_item_button);
        RefreshList();
        textView1.setText(ListDetails.getName());
        textView2.setText(ListDetails.getDate());

        addNewListItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(ListViewActivity.this);
                builder.setTitle("Adja meg mit szeretne a listához adni!");
                final EditText input = new EditText(ListViewActivity.this);
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

        delListItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] names = ListDetails.getListitems().split(",");

                final ArrayList<Integer> selectedItems = new ArrayList<Integer>();
                AlertDialog.Builder builder = new AlertDialog.Builder(ListViewActivity.this);
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
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Delete(selectedItems);
                        Toast.makeText(ListViewActivity.this, "Sikeres törlés", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(ListViewActivity.this, "Törlés megszakítva", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

    }
    public void RefreshList() {
        DatabaseReference reference;
        reference=FirebaseDatabase.getInstance().getReference().child("Lists");
        final ArrayList<String> arrayList = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter<String>(this, R.layout.list_items, R.id.textt, arrayList);
        reference.orderByChild("ID").equalTo(ListDetails.getID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    String listitems = ds.child("listitems").getValue(String.class);
                    if(!(listitems.length() == 0))
                    {
                        for (String item : listitems.split(",")) {
                            arrayList.add(item);
                        }
                    }
                    listView.setAdapter(arrayAdapter);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void Upload(String listitem)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("email", CurrentUsers.currentOnlineUser.getEmail());
        userdataMap.put("ID", ListDetails.getID());
        userdataMap.put("name", ListDetails.getName());
        String newlistitems = ListDetails.getListitems();
        if(!(newlistitems.length()==0))
        {
            newlistitems = newlistitems.concat(",");
        }
        newlistitems = newlistitems.concat(listitem);
        userdataMap.put("listitems", newlistitems);
        final String addeditem = newlistitems;
        RootRef.child("Lists").child(ListDetails.getID()).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    ListDetails.setListitems(addeditem);
                    Toast.makeText(ListViewActivity.this, "Sikeres listaelem létrehozás!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(ListViewActivity.this, "Hiba lépett fel a lista létrehozása közben, próbálja meg később", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void Delete(ArrayList<Integer> listItems)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();
        ArrayList<String> items = new ArrayList<String>();
        String[] split = ListDetails.getListitems().split(",");

        for(int i=0; i<split.length; i++)
        {
            if(!listItems.contains(i))
            {
                items.add(split[i]);
            }
        }
        String joineditems = TextUtils.join(",", items);
        ListDetails.setListitems(joineditems);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("listitems", joineditems);
        RootRef.child("Lists").child(ListDetails.getID()).updateChildren(map);

        RefreshList();
    }
}

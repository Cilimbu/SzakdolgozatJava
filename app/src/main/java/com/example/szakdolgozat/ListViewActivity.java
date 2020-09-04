package com.example.szakdolgozat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import org.w3c.dom.Text;

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
    TextView textView1,textView2, doneCheck;
    final ArrayList<ListItemDetails> arrayList = new ArrayList<>();


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        addNewListItem = (Button)findViewById(R.id.add_new_list_item_button);
        listView = (ListView) findViewById(R.id.list_view_listview);
        ListDetails = (UserListDetails)getIntent().getSerializableExtra("ListDetails");
        textView1 = (TextView) findViewById(R.id.nametextView);
        textView2 = (TextView) findViewById(R.id.datetextView);
        delListItems = (Button) findViewById(R.id.del_list_item_button);
        final ArrayAdapter arrayAdapter = new ArrayAdapter<ListItemDetails>(this, R.layout.list_items_checkbox, R.id.textt, arrayList);
        RefreshList(arrayAdapter);
        textView1.setText(ListDetails.getName());
        textView2.setText(ListDetails.getDate());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListItemDetails listItemDetails = (ListItemDetails) arrayAdapter.getItem(i);
                final Integer newi = i;

                toggleChecked(newi);
                //RefreshList(arrayAdapter);

            }
        });



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
                        RefreshList(arrayAdapter);
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

                String[] names = new String[arrayList.size()];
                for(int i = 0; i<arrayList.size();i++)
                {
                    names[i] = arrayList.get(i).getName();
                }
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
                        RefreshList(arrayAdapter);
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
    public void RefreshList(final ArrayAdapter<ListItemDetails> arrayAdapter) {
        DatabaseReference reference;
        reference=FirebaseDatabase.getInstance().getReference().child("Lists");
        reference.child(ListDetails.getID()).child("listitems").orderByChild("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for(DataSnapshot ds : snapshot.getChildren())
                {
                    ListItemDetails temp = new ListItemDetails();
                    String name = ds.child("name").getValue(String.class);
                    temp.setName(name);
                    String ID = ds.child("ID").getValue(String.class);
                    temp.setID(ID);
                    String checked = ds.child("checked").getValue(String.class);
                    temp.setChecked(checked);
                    arrayList.add(temp);
                }
                listView.setAdapter(arrayAdapter);
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
        String ListItemID = RootRef.push().getKey();
        String checked = "";
        HashMap<String, Object> userdataMap = new HashMap<>();
        userdataMap.put("ID", ListItemID);
        userdataMap.put("name", listitem);
        userdataMap.put("checked", checked);
        RootRef.child("Lists").child(ListDetails.getID()).child("listitems").child(ListItemID).updateChildren(userdataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
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

        for(Integer i : listItems)
        {
            RootRef.child("Lists").child(ListDetails.getID()).child("listitems").child(arrayList.get(i).getID()).removeValue();
        }

    }
    public void toggleChecked(final Integer i)
    {
        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.child("Lists").child(ListDetails.getID()).child("listitems").child(arrayList.get(i).getID()).child("checked").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String checked = snapshot.getValue(String.class);
                Map<String, Object> map = new HashMap<>();
                map.put("checked",(checked.equals("") ? "Kész" : ""));

                RootRef.child("Lists").child(ListDetails.getID()).child("listitems").child(arrayList.get(i).getID()).updateChildren(map);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

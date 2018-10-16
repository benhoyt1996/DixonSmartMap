package com.example.benjamin.finalmap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class GetDataActivity extends AppCompatActivity {

    String description;
    String name;

    ArrayList<Tree> treeList = new ArrayList<>();
    ArrayList<Tree> finalTreeList = new ArrayList<>();

    Tree tree1 = new Tree();
    Tree tempTree = new Tree();
    Tree tempAddTree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_data);

        getTreeList(new MyCallback() {
            public void onCallback(String value) {

                String[] treeData = value.split("splithere");

                if (treeData[0].equals("name"))
                {
                    tempTree.name = treeData[1];
                }
                else if (treeData[0].equals("latinName"))
                {
                    tempTree.latinName = treeData[1];
                }
                else if (treeData[0].equals("coords"))
                {
                    tempTree.Coordinates = treeData[1];
                }
                else if (treeData[0].equals("desc"))
                {
                    tempTree.description = treeData[1];
                }
                else if (treeData[0].equals("pics"))
                {
                    tempTree.photos = treeData[1];
                    addTree(tempTree);
                }
                if (tempTree.name != null && tempTree.description != null
                        && tempTree.latinName != null
                        && tempTree.Coordinates != null && tempTree.photos != null) {
                }

            }
        });
    }

    public void getTreeList(final MyCallback myCallback) {

        final List<Tree> createdList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference("Trees").addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                if (dataSnapshot.exists()) {
                    HashMap<String, Object> hashmap = new HashMap<>();
                    Iterator<DataSnapshot> treeIterator = dataSnapshot.getChildren().iterator();
                    while (treeIterator.hasNext()) {

                        DataSnapshot treeSnapShot = treeIterator.next();

                        if (treeSnapShot.getKey().equals("name"))
                        {
                            tree1.setName(treeSnapShot.getValue().toString());
                            myCallback.onCallback("namesplithere" + treeSnapShot.getValue().toString());
                        }
                        else if (treeSnapShot.getKey().equals("latinName"))
                        {
                            tree1.setLatinName(treeSnapShot.getValue().toString());
                            myCallback.onCallback("latinNamesplithere" + treeSnapShot.getValue().toString());
                        }
                        else if (treeSnapShot.getKey().equals("description"))
                        {
                            tree1.setDescription(treeSnapShot.getValue().toString());
                            myCallback.onCallback("descsplithere" + treeSnapShot.getValue().toString());
                        }
                        else if (treeSnapShot.getKey().equals("Coordinates"))
                        {
                            String[] splitCoords = treeSnapShot.getValue().toString().replace("[", "").replace("]", "").split(",");
                            StringBuilder sb = new StringBuilder();
                            sb.append(splitCoords[1]).append(",").append(splitCoords[2]);
                            myCallback.onCallback("coordssplithere" + sb.toString());
                            tree1.setCoordinates(sb.toString());
                        }
                        else if (treeSnapShot.getKey().equals("photos"))
                        {
                            String[] splitPhoto = treeSnapShot.getValue().toString().replace("[", "").replace("]", "").split(",");
                            myCallback.onCallback("picssplithere" + splitPhoto[1]);
                            tree1.setPhotos(splitPhoto[1]);
                            tempAddTree = new Tree(tree1.Coordinates, tree1.description, tree1.latinName, tree1.name, tree1.photos);
                            createdList.add(tempAddTree);
                        }
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    public void addTree(Tree t) {

        tempAddTree = new Tree(t.Coordinates, t.description, t.latinName, t.name, t.photos);
        this.treeList.add(new Tree(t.Coordinates, t.description, t.latinName, t.name, t.photos));

        if (treeList.size() >= 10) {
            this.finalTreeList = this.treeList;
            Intent mapsIntent = new Intent(GetDataActivity.this, MapsActivity.class);
            mapsIntent.putExtra("data", new DataWrapper(finalTreeList));
            startActivity(mapsIntent);
        }
    }

    public interface MyCallback {

        void onCallback(String value);

    }

}
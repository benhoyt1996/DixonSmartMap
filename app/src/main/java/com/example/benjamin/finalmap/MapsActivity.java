package com.example.benjamin.finalmap;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import java.util.Iterator;
import android.app.Fragment;
import android.view.View;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.data.geojson.GeoJsonLayer;
import com.google.maps.android.data.kml.KmlLayer;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.json.JSONException;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private static final LatLng NORTHEAST = new LatLng(35.1074333, -89.91459722222223);
    private static final LatLng SOUTHWEST = new LatLng(35.1044389, -89.92042777777779);
    public static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION =1;

    private GoogleMap mMap;



    String[] list;

    MaterialSearchView searchView;

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    DatabaseReference mDatabase;
    DatabaseReference treeEndpoint;

    DataSnapshot treeDatabase;

    String TAG = MapsActivity.class.getSimpleName();

    String Coordinates;
    String description;
    String latinName;
    String name;
    String photos;

    List<Tree> treeList = new ArrayList<>();
    List<Tree> finalTreeList = new ArrayList<>();
    List<Tree> testTreeList = new ArrayList<>();

    ArrayList<Tree> importedList = new ArrayList<>();

    Tree tree1 = new Tree();
    Tree tempTree = new Tree();
    Tree tempAddTree;
    Integer counter = 0;

    Tree foundTree = new Tree();

    //ArrayList<Tree> myList = (ArrayList<Tree>) getIntent().getSerializableExtra("mylist");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        DataWrapper dw = (DataWrapper) getIntent().getSerializableExtra("data");
        importedList = dw.getTrees();


        //Log.d(TAG, this.finalTreeList.get(this.finalTreeList.size()).name);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        for(Tree t: importedList)
//        {
//            Log.d(TAG, "TREE " + t.name + "\n");
//            Log.d(TAG, "TREE " + t.latinName + "\n");
//            Log.d(TAG, "TREE " + t.description + "\n");
//            Log.d(TAG, "TREE " + t.Coordinates + "\n");
//            Log.d(TAG, "TREE " + t.photos + "\n");
//
//        }

        //loadTreeList(this.finalTreeList);
        List<String> treeNameList = new ArrayList<String>();
        for(Tree t: importedList) {
            //Log.d(TAG, importedList.get(importedList.size()).name);
            treeNameList.add(t.name);
        }

        String[] treeNameArr = new String[treeNameList.size()];
        treeNameArr = treeNameList.toArray(treeNameArr);

        for(String s : treeNameArr)
            System.out.println(s);


//        list = new String[]{"Japanese Maple", "Three-flowered Maple", "Black Oak",
//                "Southern Black-haw", "Musclewood", "Weeping Katsuratree", "Dove Tree",
//                "Chinese Witchhazel", "Possumhaw", "Southern Magnolia", "Southern Red Oak", "American Elm",
//                "Slippery Elm"};



        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setSuggestions(treeNameArr);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                for(Tree t: importedList)
                {

                    Log.d("query: " , query);
                    Log.d("treename: ", t.name);
                    if(query.equals(t.name))
                    {
                        String[] splitCoords = t.Coordinates.split(",");
                        Double lat = Double.parseDouble(splitCoords[0].replaceAll("\\s+",""));
                        Double lon = Double.parseDouble(splitCoords[1].replaceAll("\\s+",""));

                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lon), 18));
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lon)).title("Marker on" + t.name));
                        Toast.makeText(MapsActivity.this, "Marker Created at" + t.Coordinates, Toast.LENGTH_LONG).show();

                    }
                }




                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Make realtime changes as search is edited

                return false;
            }

        });




//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder
//                .setTitle("Hello, Welcome to the Dixon Gardens Android App")
//                .setMessage("Click on the Search Button and begin typing to bring up tree search suggestions")
//               // .setIcon(android.R.drawable.ic_dialog_alert)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener()
//                {
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        //do some thing here which you need
//                    }
//                });
////        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
////        {
////            public void onClick(DialogInterface dialog, int which)
////            {
////                dialog.dismiss();
////            }
////        });
//        AlertDialog alert = builder.create();
//        alert.show();





    }

//    private void getTreeData() {
//
//        mDatabase = FirebaseDatabase.getInstance().getReference();
//
//
//
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        getLocationPermission();


        LatLng dixGardens = new LatLng(35.1059, -89.9178);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dixGardens));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dixGardens, 18));

        mMap.setOnMarkerClickListener(this);



        try {
            GeoJsonLayer layer = new GeoJsonLayer(mMap, R.raw.dixonoverlay,
                    getApplicationContext());
            layer.addLayerToMap();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }



        LatLngBounds DIXBOUNDS  = new LatLngBounds(
                new LatLng(35.104975, -89.9197722222222),
                new LatLng(35.1071861, -89.91549444444445));
        LatLng DIXDRAWNMAP= new LatLng(35.1071861, -89.91549444444445);



    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public boolean onMarkerClick(final Marker marker) {

        // Retrieve the data from the marker.
       // Integer clickCount = (Integer) marker.getTag();
        Intent mapIntent = new Intent(MapsActivity.this, TreeActivity.class);

        Tree markerTree;
        findTree(marker.getTitle().replace("Marker on ",""));
        markerTree = foundTree;
        Log.d("marker tree: ", markerTree.toString());


        mapIntent.putExtra("TREENAME", markerTree.name);
        mapIntent.putExtra("TREE_LATIN_NAME", markerTree.latinName);
        mapIntent.putExtra("TREE_DESC", markerTree.description.replace("\t", "line.seperator"));
        mapIntent.putExtra("TREE_IMAGE_URL", markerTree.photos.replace(" ", ""));


        startActivity(mapIntent);




        // Check if a click count was set, then display the click count.
//        if (clickCount != null) {
//            clickCount = clickCount + 1;
//            marker.setTag(clickCount);
//            Toast.makeText(this,
//                    marker.getTitle() +
//                            " has been clicked " + clickCount + " times.",
//                    Toast.LENGTH_SHORT).show();
//
//        }

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur (which is for the camera to move such that the
        // marker is centered and for the marker's info window to open, if it has one).
        return false;
    }



    public void findTree(String treeName) {


        for(Tree t : importedList)
        {
            if(treeName.replace("Marker on","").equals(t.name))
            {
                foundTree = t;
            }
        }

    }


    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


//    public interface MyCallback {
//
//        void onCallback(String value);
//
//    }

}

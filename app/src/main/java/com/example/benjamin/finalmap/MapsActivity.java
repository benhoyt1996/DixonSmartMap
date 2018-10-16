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

    MaterialSearchView searchView;

    String TAG = MapsActivity.class.getSimpleName();

    String description;
    String name;

    ArrayList<Tree> importedList = new ArrayList<>();

    Tree foundTree = new Tree();

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

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Generate list of tree names from treeList used to populate search suggestions
        List<String> treeNameList = new ArrayList<String>();
        for(Tree t: importedList) {
            treeNameList.add(t.name);
        }
        String[] treeNameArr = new String[treeNameList.size()];
        treeNameArr = treeNameList.toArray(treeNameArr);

        //Add material search view to map
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setSuggestions(treeNameArr);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                for(Tree t: importedList)
                {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }


    //Changes the map when the map is ready to use
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        getLocationPermission();

        //Move camera to Dixon Gardens
        LatLng dixGardens = new LatLng(35.1059, -89.9178);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(dixGardens));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(dixGardens, 18));

        mMap.setOnMarkerClickListener(this);


        //Add KML overlay to map
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

        Intent mapIntent = new Intent(MapsActivity.this, TreeActivity.class);

        //Search for tree in list of trees
        Tree markerTree;
        findTree(marker.getTitle().replace("Marker on ",""));
        markerTree = foundTree;


        mapIntent.putExtra("TREENAME", markerTree.name);
        mapIntent.putExtra("TREE_LATIN_NAME", markerTree.latinName);
        mapIntent.putExtra("TREE_DESC", markerTree.description.replace("\t", ""));
        mapIntent.putExtra("TREE_IMAGE_URL", markerTree.photos.replace(" ", ""));


        startActivity(mapIntent);

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

}

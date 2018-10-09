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

    Tree tree1 = new Tree();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        getTreeList(new MyCallback() {
            @Override
            public void onCallback(String value) {
                //Log.d("TAG", value);
                Tree tempTree = new Tree();

                String[] treeData = value.split("splithere");
               // if(treeData[1].contains("\t")) { treeData[1] = treeData[1].replace("\t", ""); }
                //Log.d(TAG, treeData[0]);

                if(treeData[0] == "a") {
                    tempTree.name = treeData[2];
                    Log.d(TAG, "name: " + tempTree.name);
                }
                else if(treeData[0] == "b") {
                    tempTree.latinName = treeData[1];
                    Log.d(TAG, "latin Name: " + tempTree.latinName);
                }
                else if(treeData[0] == "c") {
                    tempTree.latinName = treeData[1];
                    Log.d(TAG, "coordinates: " + tempTree.Coordinates);
                }
                else if(treeData[0] == "d") {
                    tempTree.latinName = treeData[1];
                    Log.d(TAG, "description: " + tempTree.description);
                }
                else if(treeData[0] == "e") {
                    tempTree.latinName = treeData[1];
                    Log.d(TAG, "photos" + tempTree.photos);
                }

            }
        });


        //Log.d(TAG, treeList.get(0).name);
        for(Tree t: treeList)
        {
            Log.d(TAG, "TREE " + t.name + "\n");
            Log.d(TAG, "TREE " + t.latinName + "\n");
            Log.d(TAG, "TREE " + t.description + "\n");
            Log.d(TAG, "TREE " + t.Coordinates + "\n");
            Log.d(TAG, "TREE " + t.photos + "\n");

        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




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




        list = new String[]{"Japanese Maple", "Three-flowered Maple", "Black Oak",
                "Southern Black-haw", "Musclewood", "Weeping Katsuratree", "Dove Tree",
                "Chinese Witchhazel", "Possumhaw", "Southern Magnolia", "Southern Red Oak", "American Elm",
                "Slippery Elm"};



        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setSuggestions(list);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {



                //FILTERING OF LIST GOES HERE
                if(query.equals("Japanese Maple"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10498, -89.91756), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10498,-89.91756", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10498, -89.91756)).title("Marker on Japanese Maple"));
                }
                else if(query.equals("Three-flowered Maple"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10613,-89.91624), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10613,-89.91624", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10613,-89.91624)).title("Marker on Three-flowered Maple"));

                }
                else if(query.equals("Black Oak"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10546,-89.9172), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10546,-89.9172", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10546,-89.9172)).title("Marker on Black Oak"));
                }
                else if(query.equals("Southern Black-haw"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10587,-89.91814), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10587,-89.91814", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10587,-89.91814)).title("Marker on Southern Black-haw"));
                }
                else if(query.equals("Musclewood"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10576,-89.91838), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10576,-89.91838", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10576,-89.91838)).title("Marker on Southern Musclewood"));
                }
                else if(query.equals("Weeping Katsuratree"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10556,-89.91724), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10556,-89.91724", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10556,-89.91724)).title("Marker on Weeping Katsuratree"));
                }
                else if(query.equals("Dove Tree"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10555,-89.91823), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10555,-89.91823", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10555,-89.91823)).title("Marker on Dove Tree"));
                }
                else if(query.equals("Chinese Witchhazel"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10597,-89.91886), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10597,-89.91886", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10597,-89.91886)).title("Marker on Chinese Witchhazel"));
                }
                else if(query.equals("Possumhaw"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10583,-89.91617), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10583,-89.91617", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10583,-89.91617)).title("Marker on Possumhaw"));
                }
                else if(query.equals("Southern Magnolia"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10593,-89.91843), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10593,-89.91843", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10593,-89.91843)).title("Marker on Southern Magnolia"));
                }
                else if(query.equals("Southern Red Oak"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10498,-89.91756), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10498,-89.91756", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10498,-89.91756)).title("Marker on Southern Red Oak"));
                }
                else if(query.equals("American Elm"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10613,-89.91624), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10613,-89.91624", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10613,-89.91624)).title("Marker on American Elm"));
                }
                else if(query.equals("Slippery Elm"))
                {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.10585,-89.91821), 18));
                    Toast.makeText(MapsActivity.this, "Marker Created at 35.10585,-89.91821", Toast.LENGTH_LONG).show();
                    mMap.addMarker(new MarkerOptions().position(new LatLng(35.10585,-89.91821)).title("Marker on Slippery Elm"));
                }
                else
                {
                    Toast.makeText(MapsActivity.this, "Tree not found!", Toast.LENGTH_LONG).show();

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

        MenuItem item = menu.findItem(R.id.search);
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
        markerTree = findTree(marker.getTitle().replace("Marker on ",""));


        mapIntent.putExtra("TREENAME", markerTree.name);
        mapIntent.putExtra("TREE_LATIN_NAME", markerTree.latinName);
        mapIntent.putExtra("TREE_DESC", markerTree.description);
        mapIntent.putExtra("TREE_IMAGE_URL", markerTree.photos);



//        String treeName = marker.getTitle();
//        treeName = treeName.replace("Marker on ","");
//        String latinName;
//        String desc;
//        String imageUrl;

//        if(treeName.equals("Japanese Maple"))
//        {
//            latinName = "Acer negundo";
//            desc = "Acer palmatum is a deciduous shrub or small tree reaching heights of 6 to 10 m (20 to 33 ft), rarely 16 metres (52 ft), often growing as an understory plant in shady woodlands. It may have multiple trunks joining close to the ground. In habit, it is often shaped like a hemisphere (especially when younger) or takes on a dome-like form, especially when mature. The leaves are 4–12 cm long and wide, palmately lobed with five, seven, or nine acutely pointed lobes. The flowers are produced in small cymes, the individual flowers with five red or purple sepals and five whitish petals. The fruit is a pair of winged samaras, each samara 2–3 cm long with a 6–8 mm seed. The seeds of Acer palmatum and similar species require stratification in order to germinate.[8][9] " + "\n" + "Even in nature, Acer palmatum displays considerable genetic variation, with seedlings from the same parent tree typically showing differences in such traits as leaf size, shape, and color. Overall form of the tree can vary from upright to weeping.[8] " + "\n" + "" + "\n" + "Three subspecies are recognised:[8][9] " + "\n" + "Acer palmatum subsp. palmatum. Leaves small, 4–7 cm wide, with five or seven lobes and double-serrate margins; seed wings 10–15 mm (3⁄8–5⁄8 in). Lower altitudes throughout central and southern Japan (not Hokkaido). " + "\n" + "Acer palmatum subsp. amoenum (Carrière) H.Hara. Leaves larger, 6–12 cm wide, with seven or nine lobes and single-serrate margins; seed wings 20–25 mm (3⁄4–1 in). Higher altitudes throughout Japan and South Korea. " + "\n" + "Acer palmatum subsp. matsumurae Koidz. Leaves larger, 6–12 cm wide, with seven (rarely five or nine) lobes and double-serrate margins; seed wings 15–25 mm. Higher altitudes throughout Japan.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/purple-ghost-japanese-maple-1.jpg?alt=media&token=f0babb5c-0964-47e8-a1eb-c949e0971ce5";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//
//
//        }
//        else if(treeName.equals("Three-flowered Maple"))
//        {
//            latinName = "Acer triflorum";
//            desc = "Acer triflorum, the three-flowered maple, is a species of maple native to hills of northeastern China (Heilongjiang, Jilin, Liaoning) and Korea. " + "\n" + "It is a deciduous tree that reaches a height of about 25 metres (82 ft) but is usually smaller. It is a trifoliate maple related to such other species as Manchurian Maple (Acer mandshuricum) and Paperbark Maple (Acer griseum). It has yellowish-brown exfoliating bark that peels in woody scales rather than papery pieces like Acer griseum. " + "\n" + "The leaves have a 2.5–6 centimetres (0.98–2.36 in) petiole and three leaflets; the leaflets are 4–9 centimetres (1.6–3.5 in) long and 2–3.5 centimetres (0.79–1.38 in) broad, with serrated margins, the central leaflet the same size as or slightly larger than the two side leaflets. The flowers are yellow, produced in small corymbs of three small flowers each, hence the name. The samaras are 3.5–4.5 centimetres (1.4–1.8 in) long and 1.3–2 cm broad, hairy, the nutlet with a woody shell. " + "\n" + "Even more than its relatives, three-flower maple has spectacular fall colour that may include brilliant orange, scarlet, purple and gold. It is one of the few trees to develop good fall colour in shade.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/ThreefloweredMaple.JPG?alt=media&token=066e8091-1a1d-4ed6-9cfe-9379fdea161f";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//
//        }
//        else if(treeName.equals("Black Oak"))
//        {
//            latinName = "Quercus velutina";
//            desc = "Black oak is a member of the red oak group with lobed leaves. This is a Kentucky native. In early spring, velvety red leaves emerge from the velvety winter buds. The bicolored, mature leaves contrast nicely with unique black, furrowed bark. Fall foliage is orange or red. " + "\n" + "Culture: The black oak requires full sun and thrives in well-drained, slightly acidic soil. It tolerates poor, dry soil, but will not tolerate shade. Like many of the oak species, black oak can develop yellow leaves, or chlorosis, when grown in high pH soil. Black oak tends to suffer from decay and may become structurally compromised. Potential problems for oaks in general include obscure scale, two-lined chestnut borer, bacterial leaf scorch, oak horn gall and gypsy moth. In addition, as little as 1 inch of fill soil can kill an oak. Black oak has a long, prominent tap root that makes it difficult to transplant.  " + "\n" + "Botanical Information " + "\n" + "Native habitat: Central and eastern North America in poor, dry soils. " + "\n" + "Growth habit: Black oak has a variable, irregular form and may look unruly. " + "\n" + "Tree size: This tree usually grows to a height of 50 to 60 feet. " + "\n" + "Flower and fruit: Female flowers are inconspicuous; male catkins are pendulous. The ½- to 3/4-inch acorn is coated with rust-colored down and the top half is enclosed in a cap. " + "\n" + "Leaf: Foliage is glossy green in summer. Leaves have seven to nine lobes with sharp tips. Fall color ranges from dull red to orange-brown. " + "\n" + "Hardiness: Winter hardy to USDA Zone 3. " + "\n" + "Additional information: The black oak is a stately oak that was introduced to commerce as early as 1800. It can reach a height of more than 100 feet. Co-national champion trees are in Michigan (131 feet) and Connecticut (84 feet). The very prominent tap root of black oak ensures this species' survival under poor growing conditions. However, the black oak is not as common in the nursery trade because it can be difficult to transplant. Like other tree species, oaks can suffer where construction means a change in the grade around the root system of the tree. The black oak's common name refers to its nearly black bark. This oak's inner bark, however, is yellow or deep orange and is used to make a yellow dye called quercitron. " + "\n" + "Although the native black oak is most frequently found in dry, poor areas, it prefers rich soil. Its seedlings, however, will not survive in shade so the black oak is often relegated to poor soils in native areas. The specific epithet, velutina, is derived from the Latin word for fleece, wool or down, vellus, which refers to this species' velvety winter buds and young foliage.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/BlackOak.jpg?alt=media&token=ba8958b2-43ad-474d-b707-733cbeef340c";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("Southern Black-haw"))
//        {
//            latinName = "Viburnum rufidulum";
//            desc = "Leathery deciduous leaves are simple and grow in opposite blades ranging from 0.5-3 inches in length and 1-1.5 inches in width.Petioles are \"rusty hairy\" with grooves and sometimes wings. Leaf margins are serrate. Autumn leaf colors are bronze to red. " + "\n" + "Twigs range in color from \"reddish brown to gray\"; young twigs are hairy, and get smoother with age. Bark is similar that of the flowering dogwood, ranging in color from \"reddish brown to almost black\" and forming \"blocky plates on larger trunks\". " + "\n" + "Viburnum rufidulum blooms in April to May with creamy white flowers that are bisexual, or perfect and similar to those of other Viburnum species, but with clusters as large as six inches wide. " + "\n" + "The fruits are purple or dark blue, glaucous, globose or ellipsoid drupes that mature in mid to late summer.The fruit has been said to taste like raisins and attract birds.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/SouthernBlackhaw.JPG?alt=media&token=83f28798-391e-467e-9790-a70d49e7dc3f";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("Southern Musclewood"))
//        {
//            latinName = "Carpinus caroliniana";
//            desc = "American hornbeam is a small tree reaching heights of 10–15 meters (35–50 ft), rarely 20 meters (65 ft), and often has a fluted and crooked trunk. The bark is smooth and greenish-grey, becoming shallowly fissured in all old trees. The leaves are alternate, 3–12 centimeters long, with prominent veins giving a distinctive corrugated texture, and a serrated margin. The male and female catkins appear in spring at the same time as the leaves. The fruit is a small 7–8-millimeter  long nut, partially surrounded by three- to seven-pointed leafy involucre 2–3 centimeters long; it matures in autumn. The seeds often do not germinate till the spring of the second year after maturating. " + "\n" + "" + "\n" + "Bark: On old trees near the base, furrowed. Young trees and branches smooth, dark bluish gray, sometimes furrowed, light and dark gray. Branchlets at first pale green, changing to reddish brown, ultimately dull gray. " + "\n" + "Wood: Light brown, sapwood nearly white; heavy, hard, close-grained, very strong. Used for levers, handles of tools. Specific gravity, 0.7286; weight 45.41 pounds (20.60 kg). " + "\n" + "Winter buds: Ovate, acute, chestnut brown, 1⁄8 inch (3 mm) long. Inner scales enlarge when spring growth begins. No terminal bud is formed. " + "\n" + "Leaves: Alternate, two to four inches long, ovate-oblong, rounded, wedge-shaped, or rarely subcordate and often unequal at base, sharply and doubly serrate, acute or acuminate. They come out of the bud pale bronze green and hairy; when full grown they are dull deep green above, paler beneath; feather-veined, midrib and veins very prominent on under side. In autumn bright red, deep scarlet and orange. Petioles short, slender, hairy. Stipules caducous. " + "\n" + "Flowers: April. Monœcious, without petals, the staminate spike naked in pendulous catkins (aments). The staminate ament buds are axillary and form in the autumn. During the winter they resemble leaf-buds, only twice as large. They begin to lengthen very early in the spring, and when full grown are about 1.5 inches (4 cm) long. The staminate flower is composed of three to twenty stamens crowded on a hairy torus, adnate to the base of a broadly ovate, acute boot-shaped scale, green below the middle, bright red at apex. The pistillate aments are one-half to three-fourths of an inch long with ovate, acute, hairy, green scales and bright scarlet styles " + "\n" + "Fruit: Clusters of involucres, hanging from the ends of leafy branches. Each involucre slightly encloses a small oval nut. The involucres are short stalked, usually three-lobed, though one lobe is often wanting; halberd-shaped, coarsely serrated on one margin, or entire.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/SouthernBlackhaw.JPG?alt=media&token=83f28798-391e-467e-9790-a70d49e7dc3f";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("Weeping Katsuratree"))
//        {
//            latinName = "Cercidiphyllum japonicum";
//            desc = "This deciduous small tree initially has a pyramidal form, and later rounded.  Cercis -like, opposite, heart-shaped blue-green leaves are borne on stiff, slender, pendulous branches that fan out from the crown and sweep the ground. Caramel-scented foliage emerges bronze or purple-red, turns blue-green, then fades to gold or apricot in autumn. Tiny red flowers emerge in late March or early April before the leaves.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/WeepingKatsuratree.jpg?alt=media&token=1d96fb75-6e93-4daf-830e-71034d6e9263";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("Dove Tree"))
//        {
//            latinName = "Davidia invlucrata";
//            desc = "It is a moderately fast-growing tree, growing to 20–25 m (66–82 ft) in height, with alternate cordate leaves resembling those of a linden in appearance, except that they are symmetrical, and lacking the lop-sided base typical of linden leaves; the leaves are mostly 10–20 cm long and 7–15 cm wide and are ovate to heart-shaped. " + "\n" + "Davidia involucrata is best known for its flowers. The Latin specific epithet involucrata means \"with a ring of bracts surrounding several flowers\".[5] These form a tight cluster about 1–2 cm across, reddish in colour, each flower head with a pair of large (12–25 cm), pure white bracts at the base performing the function of petals. These hang in long rows beneath the level branches. The flowers are at their best in late May. On a breezy day, the bracts flutter in the wind like white doves or pinched handkerchiefs, hence the English names for this tree. " + "\n" + "The fruit is a very hard nut about 3 cm long surrounded by a green husk about 4 cm long by 3 cm wide, hanging on a 10 cm stalk. The nut contains 3–6 seeds.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/DoveTree.JPG?alt=media&token=d6bc7b89-a050-4b16-8839-fb8b6c4c9de5";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("Chinese Witchhazel"))
//        {
//            latinName = "Hamamelis mollis";
//            desc = "Hamamelis mollis, also known as Chinese witch hazel,is a species of flowering plant in the witch hazel family Hamamelidaceae, native to central and eastern China, in Anhui, Guangxi, Hubei, Hunan, Jiangxi, Sichuan, and Zhejiang. " + "\n" + "It is a deciduous large shrub or small tree growing to 8 m (26 ft) tall. The leaves are oval, 8–15 cm (3–6 in) long and 6–10 cm (2–4 in) broad, oblique at the base, acute or rounded at the apex, with a wavy-toothed or shallowly lobed margin, and a short petiole 6–10 mm long; they are dark green and thinly hairy above, and grey beneath with dense grey hairs. " + "\n" + "The Latin term mollis means \"soft\", and refers to the felted leaves, which turn yellow in autumn. The flowers are yellow, often with a red base, with four ribbon-shaped petals 15 mm long and four short stamens, and grow in clusters; flowering is in late winter to early spring on the bare branches. The fruit is a hard woody capsule 12 mm long, which splits explosively at the apex at maturity one year after pollination, ejecting the two shiny black seeds from the parent plant. " + "\n" + "H. mollis is widely grown as an ornamental plant, valued for the strongly-scented flowers appearing in winter when little else is growing. Numerous cultivars have been selected, for variation in flower colour and size, and in shrub size and habit. It is also one of the two parents of the popular garden hybrid H. × intermedia (the other parent is H. japonica).";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/ChineseWitchhazel.jpeg?alt=media&token=e7b32689-1c8d-45f0-8965-e15123f8d61d";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("Possumhaw"))
//        {
//            latinName = "llex decidua";
//            desc = "Ilex verticillata is a shrub growing to 1–5 metres (3.3–16.4 ft) tall. It is one of a number of hollies which are deciduous, losing their leaves in the fall. In wet sites, it will spread to form a dense thicket, while in dry soil it remains a tight shrub. The leaves are glossy green, 3.5–9 cm long, 1.5–3.5 cm broad, with a serrated margin and an acute apex. The flowers are small, 5 mm diameter, with five to eight white petals. " + "\n" + "fThe fruit is a globose red drupe 6–8 mm diameter, which often persists on the branches long into the winter, giving the plant its English name. Like most hollies, it is dioecious, with separate male and emale plants; the proximity of at least one male plant is required to pollenize the females in order to bear fruit.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/Possumhaw.jpg?alt=media&token=0ac39b86-75b6-406a-beb8-3bfec9f9fb65";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("Southern Magnolia"))
//        {
//            latinName = "Magnolia grandiflora";
//            desc = "Magnolia grandiflora is a medium to large evergreen tree which may grow 120 ft (37 m) tall.It typically has a single stem (or trunk) and a pyramidal shape. The leaves are simple and broadly ovate, 12–20 cm broad,with smooth margins. They are dark green, stiff and leathery, and often scurfy underneath with yellow-brown pubescence. " + "\n" + "The large, showy, lemon citronella-scented flowers are white, up to 30 cm across and fragrant, with six to 12 petals with a waxy texture, emerging from the tips of twigs on mature trees in late spring. " + "\n" + "Flowering is followed by the rose-coloured fruit, ovoid polyfollicle, 7.5–10 cm long, and 3–5 cm wide. Exceptionally large trees have been reported in the far southern United States. The national champion is a specimen in Smith County, Mississippi, that stands an incredible 37 m (121 ft). Another record includes a 35-m-high specimen from the Chickasawhay District, De Soto National Forest, in Mississippi, which measured 17.75 ft in circumference at breast height, from 1961, and a 30-m-tall tree from Baton Rouge, which reached 18 ft in circumference at breast height.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/SouthernMagnolia.jpg?alt=media&token=2e892542-cbde-4f3b-b0b4-25a6e01aed7f";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("Southern Red Oak"))
//        {
//            latinName = "Quercus falcata";
//            desc = "Quercus falcata is a medium to large-sized deciduous tree reaching 25–30 meters (82–98 ft) tall, rarely 35–38 meters (115–125 ft) (forest grown specimens on highly productive sites), with a trunk up to 1.5 meters (59 inches)or 5 feet) in diameter, the crown with a broad, round-topped head. " + "\n" + "The leaves are 10–30 cm (4–12 in) long and 6–16 cm (2.5–6.5 in) wide, with 3 to 5 sharply pointed, often curved, bristle-tipped lobes, the central lobe long and narrow; the small number of long, narrow lobes is diagnostic, readily distinguishing Southern Red Oak from other red oaks. The base of the leaf is distinctly rounded into an inverted bell shape and often lopsided. They are dark green and shiny above, and rusty and hairy below, particularly along the midrib and veins. " + "\n" + "The seed is a short acorn 9–16 mm long, bright orange-brown, enclosed for one-third to half of its length in a flat cup. The acorn matures at the end of its second season. The bark is dark brownish gray with narrow, shallow ridges.[5] Southern red oak has been reported to form occasional hybrids with several other red oaks in the region.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/Southern%20Red%20Oak.jpg?alt=media&token=c8937cde-5e99-44d1-8e24-17b9ed735d81";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("American Elm"))
//        {
//            latinName = "Ulmus americana";
//            desc = "Less commonly known as the white elm or water elm, American Elm is a species native to eastern North America, naturally occurring from Nova Scotia west to Alberta and Montana, and south to Florida and central Texas. The American elm is an extremely hardy tree that can withstand winter temperatures as low as -44 °F. Trees in areas unaffected by Dutch elm disease can live for several hundred years. For over 80 years, U. americana had been identified as a tetraploid, i.e. having double the usual number of chromosomes, making it unique within the genus. However, a study published in 2011 by the Agricultural Research Service of the USDA revealed that about 20% of wild American elms are diploid and may even constitute another species. Moreover, several triploid trees known only in cultivation, such as 'Jefferson', are possessed of a high degree of resistance to Dutch elm disease, which ravaged American elms in the 20th century. This suggests that the diploid parent trees, which have markedly smaller cells than the tetraploid, may too be highly resistant to the disease." + "\n" + "The leaves are alternate, 7–20 cm long, with double-serrate margins and an oblique base. The perfect flowers are small, purple-brown and, being wind-pollinated, apetalous. The flowers are also protogynous, the female parts maturing before the male, thus reducing, but not eliminating, self-fertilization, and emerge in early spring before the leaves. The fruit is a flat samara 2 cm long by 1.5 cm broad, with a circular papery wing surrounding the single 4–5 mm seed. As in the closely related European White Elm Ulmus laevis, the flowers and seeds are borne on 1–3 cm long stems. American Elm is wholly insensitive to daylight length (photoperiod), and will continue to grow well into autumn until injured by frost.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/American%20Elm.jpg?alt=media&token=23eb6d4c-0445-4246-8f7e-e595179919e7";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else if(treeName.equals("Slippery Elm"))
//        {
//            latinName = "Ulmus rubra";
//            desc = "Ulmus rubra is a medium-sized deciduous tree with a spreading head of branches, commonly growing to 12–19 m (39–62 ft), very occasionally < 30 m (98 ft) in height. Its heartwood is reddish-brown, giving the tree its alternative common name 'red elm'. The species is chiefly distinguished from American elm by its downy twigs, chestnut brown or reddish hairy buds, and slimy red inner bark. The broad oblong to obovate leaves are 10–20 cm (4–8 in) long, rough above but velvety below, with coarse double-serrate margins, acuminate apices and oblique bases; the petioles are 6–12mm long. " + "\n" + "The leaves are often red tinged on emergence, turning dark green by summer, and then a dull yellow in the fall; The perfect, apetalous, wind-pollinated flowers are produced before the leaves in early spring, usually in tight, short-stalked, clusters of 10–20. The reddish-brown fruit is an oval winged samara, orbicular to obovate, slightly notched at the top, 12–18 mm (1/2–3/4 in) long, the single, central seed coated with red-brown hairs, naked elsewhere.";
//            imageUrl = "https://firebasestorage.googleapis.com/v0/b/dixon-gardens.appspot.com/o/slipperyElm.jpg?alt=media&token=5ecde57b-e5fd-4e31-ab1e-da22ecbc8030";
//            mapIntent.putExtra("TREE_LATIN_NAME", latinName);
//            mapIntent.putExtra("TREE_DESC", desc);
//            mapIntent.putExtra("TREE_IMAGE_URL", imageUrl);
//        }
//        else
//        {
//            Toast.makeText(MapsActivity.this, "Tree not found!", Toast.LENGTH_LONG).show();
//
//        }

        startActivity(mapIntent);
       // startActivity(new Intent(MapsActivity.this, TreeActivity.class));


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

    public void getTreeList(final MyCallback myCallback) {


        FirebaseDatabase.getInstance().getReference("Trees").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


                if (dataSnapshot.exists())
                {
                    HashMap<String, Object> hashmap = new HashMap<>();
                    Iterator<DataSnapshot> treeIterator = dataSnapshot.getChildren().iterator();
                    while (treeIterator.hasNext()) {

                        DataSnapshot treeSnapShot = treeIterator.next();

                        if (treeSnapShot.getKey().equals("name"))
                        {
                            //Log.d(TAG,treeSnapShot.getValue().toString());
                            tree1.setName(treeSnapShot.getValue().toString());
                            myCallback.onCallback("asplithere" + treeSnapShot.getValue().toString());
                        }

                        else if (treeSnapShot.getKey().equals("latinName"))
                        {
                            //Log.d(TAG,treeSnapShot.getValue().toString());
                            tree1.setLatinName(treeSnapShot.getValue().toString());
                            myCallback.onCallback("bsplithere" + treeSnapShot.getValue().toString());
                            //Log.d(TAG, tree1.latinName);

                        }
                        else if (treeSnapShot.getKey().equals("description"))
                        {
                            tree1.setDescription(treeSnapShot.getValue().toString());
                            myCallback.onCallback("dsplithere" + treeSnapShot.getValue().toString());
                            //Log.d(TAG, tree1.description);
                        }
                        else if(treeSnapShot.getKey().equals("Coordinates"))
                        {
                            String[] splitCoords = treeSnapShot.getValue().toString().replace("[","").replace("]","").split(",");
                            StringBuilder sb = new StringBuilder();
                            sb.append(splitCoords[1]).append(",").append(splitCoords[2]);

                            myCallback.onCallback("csplithere" + sb.toString());
                            tree1.setCoordinates(sb.toString());

                            //Log.d(TAG, tree1.Coordinates);
                        }
                        else if(treeSnapShot.getKey().equals("photos"))
                        {
                            String[] splitPhoto = treeSnapShot.getValue().toString().replace("[","").replace("]","").split(",");

                            myCallback.onCallback("esplithere" + splitPhoto[1]);
                            tree1.setPhotos(splitPhoto[1]);
                            addTree(tree1);
//                            treeList.add(new Tree(tree1.Coordinates, tree1.description, tree1.latinName,
//                                    tree1.name, tree1.photos));

                            //Log.d(TAG, treeList.get(0).name);
                        }



//                        if(tree1.name != null && tree1.description != null
//                                && tree1.latinName != null
//                                && tree1.Coordinates != null && tree1.photos != null)
//                        {
//                            Log.d(TAG, tree1.name + tree1.latinName + tree1.photos + tree1.Coordinates +
//                                        tree1.description);
//
//
//                        }

                        //treeList.add(tree1);
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
        this.treeList.add(t);
        //Log.d(TAG, "TreeList Length: " + treeList.size());
        //Log.d(TAG, treeList.get(0).name);
    }


    public Tree findTree(String treeName) {

        Tree placeholderTree = new Tree();

        for(Tree t : treeList)
        {
            if(t.name == treeName)
            {
                return t;
            }
        }

        placeholderTree.name = "Tree not found";
        return placeholderTree;
    }

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }


    public interface MyCallback {

        void onCallback(String value);

    }

}

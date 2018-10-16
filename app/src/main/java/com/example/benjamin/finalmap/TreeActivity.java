package com.example.benjamin.finalmap;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.method.ScrollingMovementMethod;

import com.squareup.picasso.Picasso;

public class
TreeActivity extends AppCompatActivity {

    String treeName;
    String latinName;
    String treeDesc;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.ThemeOverlay_AppCompat_Dark);
        setContentView(R.layout.activity_tree);

        treeName = getIntent().getStringExtra("TREENAME");
        treeName = treeName.replace("Marker on ","");
        latinName = getIntent().getStringExtra("TREE_LATIN_NAME");
        treeDesc = getIntent().getStringExtra("TREE_DESC");
        imageUrl = getIntent().getStringExtra("TREE_IMAGE_URL");

        TextView engTextView = findViewById(R.id.engTextView);
        engTextView.setText(treeName);

        TextView latinTextView = findViewById(R.id.latinTextView);
        latinTextView.setText(latinName);
        latinTextView.setTypeface(latinTextView.getTypeface(), Typeface.ITALIC);

        TextView descTextView = findViewById(R.id.descTextView);
        descTextView.setText(treeDesc);
        descTextView.setMovementMethod(new ScrollingMovementMethod());

        ImageView imageView = findViewById(R.id.treeImageView);
        Picasso.get().load(imageUrl).into(imageView);

    }
    @Override
    public void onBackPressed() {
        // your code.
        startActivity(new Intent(TreeActivity.this, GetDataActivity.class));

    }

}

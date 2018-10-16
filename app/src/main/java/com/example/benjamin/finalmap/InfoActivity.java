package com.example.benjamin.finalmap;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        TextView tv1 = (TextView) findViewById(R.id.textView);
        tv1.setText("\n" + "Address" + "\n" +
                "4339 Park Ave, Memphis TN 38117" + "\n" + "\n" + "Admission Hours" + "\n" + "Tuesday - Friday: 10 am - 5 pm" + "\n" + "Third Thursdays: 10 am - 8 pm" +
                "\n" +"Saturdays: 10 am - 5 pm" + "\n" + "Sunday: 1 - 5 pm" + "\n" + "Mondays: Closed"  + "\n" +  "\n" +
                "\n" +"Business Office Hours" + "\n" +  "Monday - Friday: 9 am - 5 pm"  + "\n" +  "\n" + "Special Admission Hours"  + "\n" +
                "Tuesday: 10 am - 5 pm" + "\n" + "Pay-What-You-Wish" + "\n" + "Saturday: 10 am - noon" + "\n" + "Free, museum and gardens"
                + "\n" + "\n" + "\n" + "Admission Fee" + "\n" + "Adults: $7" + "\n" + "Seniors (Ages 65+): $5" + "\n" +
                "Students (Ages 18+ with valid ID): $5" + "\n" + "Children (Ages 7-17): $3" + "\n" + "Children (Ages 6-): Free" +
                "\n" + "Educators (w valid ID): Free" + "\n" + "\n" + "\n" + "Groups with advance registration" + "\n" +
                "get group discounted rate. " + "\n" +  "Please call (901) 761-5250" + "\n" + "for adjusted pricing." + "\n" + "\n" + "\n");
        tv1.setMovementMethod(new ScrollingMovementMethod());
    }
}

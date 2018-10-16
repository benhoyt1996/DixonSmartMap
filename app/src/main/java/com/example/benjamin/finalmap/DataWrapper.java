package com.example.benjamin.finalmap;

import java.io.Serializable;
import java.util.ArrayList;

public class DataWrapper implements Serializable {

    private ArrayList<Tree> trees;


    public DataWrapper(ArrayList<Tree> data) {
        this.trees = data;
    }

    public ArrayList<Tree> getTrees() {
        return this.trees;
    }

}
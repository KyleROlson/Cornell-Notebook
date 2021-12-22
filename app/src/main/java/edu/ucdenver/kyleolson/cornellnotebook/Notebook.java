package edu.ucdenver.kyleolson.cornellnotebook;

import com.google.firebase.database.Exclude;

import java.util.ArrayList;
import java.util.HashMap;

public class Notebook {
    //@Exclude
    public String name;
    public String pathToBackground;
    public HashMap<String, String> notes;
    public String key;
}

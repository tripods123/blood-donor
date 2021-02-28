package com.example.blooddonorapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.blooddonorapp.R.id.hosp_s;
import static com.example.blooddonorapp.R.id.standard;

public class MakeNewRequest extends Fragment {
    Spinner sp_state,sp_city,sp_hosp;
    Context context;
    ArrayList<String> state_array,city_array,hospital_array;
    ArrayAdapter<String> state_adapter;
    ArrayAdapter<String> city_adapter;
    ArrayAdapter<String> hosp_adapter;
    ArrayList<HashMap<String, String>> datalist,citydatalist,hospitaldatalist;
    final httphandler states=new httphandler();
    private String TAG = MainActivity.class.getSimpleName();
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        View view=inflater.inflate(R.layout.activity_req,container,false);
        Toolbar toolbar = view.findViewById(R.id.toolbar_MakeNewReq);
        context = getActivity();
        datalist = new ArrayList<HashMap<String, String>>();
        citydatalist = new ArrayList<HashMap<String, String>>();
        hospitaldatalist = new ArrayList<HashMap<String, String>>();
        sp_state = view.findViewById(R.id.state_s);
        sp_city = view.findViewById(R.id.city_s);
        sp_hosp = view.findViewById(hosp_s);
        state_array = new ArrayList<>();
        city_array = new ArrayList<>();
        hospital_array = new ArrayList<>();
        return view;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String jsonStr = states.getstates("https://blood-donor.herokuapp.com/objects/getstates.php");
        if (jsonStr != null) {
            try {
                JSONObject jsonObj = new JSONObject(jsonStr);
                // Getting JSON Array node
                JSONArray data = jsonObj.getJSONArray("");
                // looping through All Contacts
                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);
                    String name=c.getString("name");
                    HashMap<String, String> states_data= new HashMap<>();
                    state_array.add(name);
                    // adding each child node to HashMap key => value
                    states_data.put("name", name);
                    states_data.put("stateid", ""+i);
                    // adding contact to contact list
                    datalist.add(states_data);
                }} catch (final JSONException e) {
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                Toast.makeText(context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Log.e(TAG, "Couldn't get json from server.");
            Toast.makeText(context, "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
        }
        state_adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, state_array);
        sp_state.setAdapter(state_adapter);
        sp_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                HashMap<String, String> temp= new HashMap<String, String>();
                temp= datalist.get(i);
                String sid=temp.get("stateid");
                String stateid="stateid";
                String jsonInputString = "{"+stateid+": "+sid+"}";
                String citystr=states.getcity("https://blood-donor.herokuapp.com/objects/getcity.php",jsonInputString);
                if (citystr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(citystr);
                        // Getting JSON Array node
                        JSONArray data = jsonObj.getJSONArray("");
                        // looping through All Contacts
                        for (int j = 0; j < data.length(); j++) {
                            JSONObject c = data.getJSONObject(j);
                            String cityid=c.getString("cityid");
                            String cityname=c.getString("name");
                            HashMap<String, String> city_data= new HashMap<>();
                            city_array.add(cityname);
                            // adding each child node to HashMap key => value
                            city_data.put("name", cityname);
                            city_data.put("cityid", ""+cityid);
                            city_data.put("stateid", ""+sid);
                            // adding contact to contact list
                            datalist.add(city_data);
                        }} catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        Toast.makeText(context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    Toast.makeText(context, "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                }
                sp_city.setAdapter(city_adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> temp= new HashMap<String, String>();
                temp= citydatalist.get(position);
                String sid=temp.get("stateid");
                String stateid="stateid";
                String cid=temp.get("cityid");
                String cityid="cityid";
                String jsonInputString = "{"+stateid+": "+sid+",\n"+cityid+": "+cid+"}";
                String citystr=states.gethospitals("https://blood-donor.herokuapp.com/objects/gethospitals.php",jsonInputString);
                if (citystr != null) {
                    try {
                        JSONObject jsonObj = new JSONObject(citystr);
                        // Getting JSON Array node
                        JSONArray data = jsonObj.getJSONArray("");
                        // looping through All Contacts
                        for (int j = 0; j < data.length(); j++) {
                            JSONObject c = data.getJSONObject(j);
                            String hospitalid=c.getString("hospitalid");
                            String hospitalname=c.getString("name");
                            HashMap<String, String> hospital_data= new HashMap<>();
                            hospital_array.add(hospitalname);
                            // adding each child node to HashMap key => value
                            hospital_data.put("name", hospitalname);
                            hospital_data.put("cityid", ""+hospitalid);
                            hospital_data.put("stateid", ""+sid);
                            hospital_data.put("city", ""+cid);
                            // adding contact to contact list
                            hospitaldatalist.add(hospital_data);
                        }} catch (final JSONException e) {
                        Log.e(TAG, "Json parsing error: " + e.getMessage());
                        Toast.makeText(context, "Json parsing error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.e(TAG, "Couldn't get json from server.");
                    Toast.makeText(context, "Couldn't get json from server. Check LogCat for possible errors!", Toast.LENGTH_LONG).show();
                }
                sp_hosp.setAdapter(hosp_adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}

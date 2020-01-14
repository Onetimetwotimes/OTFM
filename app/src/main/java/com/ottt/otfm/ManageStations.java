package com.ottt.otfm;

import android.app.Dialog;
import android.database.DataSetObserver;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class ManageStations extends AppCompatActivity {
    private Dialog stationDialog;
    private Button confirmStation_btn;
    EditText stationTitle_txtBx, stationURL_txtBx;
    ListView stations_list;
    ArrayList<TextView> adapterList = new ArrayList<>();

    private static Stations stations;

    public static void setStations(Stations s) {
        stations = s;
    }

    private View.OnClickListener confirmNewStation = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                stations.addStation(stationTitle_txtBx.getText().toString(), stationURL_txtBx.getText().toString());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                switch (e.getMessage()) {
                    case "Key cannot be null or empty!":
                        BlinkHandler.blink(stationTitle_txtBx);
                        return;
                    case "Key already exists!":
                        Toast.makeText(getApplicationContext(), "There is already a station with that title!", Toast.LENGTH_LONG).show();
                        return;
                    case "URL cannot be null or empty!":
                        BlinkHandler.blink(stationURL_txtBx);
                        return;
                }
            }
            stationDialog.hide();
            populateStations();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_stations);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialiseViews();

        populateStations();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newStation();
            }
        });
    }

    private void populateStations() {
        stations_list.setAdapter(new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,stations.getKeySet()));
    }

    private void initialiseViews() {
        stations_list = findViewById(R.id.stations_list);
        populateStations();
        registerForContextMenu(stations_list);

        stationDialog = new Dialog(this);
        stationDialog.setContentView(R.layout.dialog_station);
        confirmStation_btn = stationDialog.findViewById(R.id.confirmStation_btn);
        stationTitle_txtBx = stationDialog.findViewById(R.id.stationTitle_txtBx);
        stationURL_txtBx = stationDialog.findViewById(R.id.stationURL_txtBx);
    }

    private void newStation() {
        stationTitle_txtBx.setText("");
        stationURL_txtBx.setText("");
        confirmStation_btn.setOnClickListener(confirmNewStation);
        stationDialog.show();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.menu_stations, menu);

    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        System.out.println(stations.getKeySet().get((int) menuInfo.id));

        switch (item.getTitle().toString()) {
            case "Edit":
                editSation(menuInfo);
                break;
            case "Remove":
                removeStation(menuInfo);
        }

        return super.onContextItemSelected(item);
    }

    private void editSation(AdapterView.AdapterContextMenuInfo menuInfo) {
        final String key = stations.getKeySet().get((int)menuInfo.id);
        stationTitle_txtBx.setText(key);
        stationURL_txtBx.setText(stations.getStationURL(key));

        confirmStation_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!stationTitle_txtBx.getText().toString().equals(key)) {
                    try {
                        stations.removeStation(key);
                        stations.addStation(stationTitle_txtBx.getText().toString(),stationURL_txtBx.getText().toString());
                    }catch (Exception e) {
                        switch (e.getMessage()) {
                            case "Key cannot be null or empty!":
                                BlinkHandler.blink(stationTitle_txtBx);
                                return;
                            case "Key already exists!":
                                Toast.makeText(getApplicationContext(),"There is already a station with that title!", Toast.LENGTH_LONG).show();
                                return;
                            case "URL cannot be null or empty!":
                                BlinkHandler.blink(stationURL_txtBx);
                                return;
                        }
                    }
                    populateStations();
                }
                else {
                    try {
                        stations.setStation(key,stationURL_txtBx.getText().toString());
                    }catch (IllegalArgumentException e) {
                        BlinkHandler.blink(stationURL_txtBx);
                        return;
                    }


                }
                stationDialog.hide();
                populateStations();
            }
        });

        stationDialog.show();
    }

    private void removeStation(AdapterView.AdapterContextMenuInfo menuInfo) {
        stations.removeStation(stations.getKeySet().get((int) menuInfo.id));
        populateStations();
    }
}

package com.ottt.otfm;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;

public class MainActivity extends AppCompatActivity {
    private Stations stations;
    private RadioPlayer radioPlayer;
    private Spinner station_spin;
    private TextView mediaInfo_txt;
    private ImageButton pausePlay_btn;


    private MetadataOutput metadataOutput = new MetadataOutput() {
        @Override
        public void onMetadata(Metadata metadata) {
            mediaInfo_txt.setText(metadata.toString().split("\"")[1].split("\"")[0]);
        }
    };



    //region:Initialisation
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initialiseRadio();
        initialiseViews();

    }

    private void initialiseRadio() {
        stations = new Stations(this);
        if (stations.getLatest().equals("no_dest")) {
            stations.addStation("Nightwave Plaza", "http://radio.plaza.one/mp3");
            stations.addStation("Deep Space One", "http://ice4.somafm.com/deepspaceone-128-mp3");
            stations.setLatest("Nightwave Plaza");
        }
        try {
            radioPlayer = new RadioPlayer(this,stations.getStationURL(stations.getLatest()));
        }catch (IllegalArgumentException e) {
            System.err.println("Invalid .latest!");
            radioPlayer = new RadioPlayer(this,"no_dest");
        }
        radioPlayer.setMetadataOutput(metadataOutput);
    }
    private void initialiseViews() {
        station_spin = findViewById(R.id.station_spin);
        station_spin.setOnItemSelectedListener(stationSelectedListener);

        mediaInfo_txt = findViewById(R.id.mediaInfo_txt);
        mediaInfo_txt.setSelected(true);

        pausePlay_btn = findViewById(R.id.pausePlay_btn);
    }
    //endregion

    //region Spinner
    private void populateSpinner() {
        ArrayAdapter<Object> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item,
                stations.getKeySet().toArray());

        station_spin.setAdapter(adapter);
        station_spin.setSelection(stations.getKeySet().indexOf(stations.getLatest()));
    }
    private AdapterView.OnItemSelectedListener stationSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (!stations.getLatest().equals(stations.getKeySet().get(position))) {
                String key = parent.getItemAtPosition(position).toString();
                stations.setLatest(key);
                radioPlayer.setMediaSource(stations.getStationURL(key));
                System.out.println("Changed");
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    //endregion


    //region: Button Events
    public void onPausePlay(View v) {
        pausePlay_btn.setImageResource(radioPlayer.pausePlay() ? R.drawable.exo_controls_pause:R.drawable.exo_controls_play);
    }
    public void onNext(View v) {
        station_spin.setSelection(station_spin.getSelectedItemPosition() == station_spin.getCount() - 1? 0 : station_spin.getSelectedItemPosition() + 1);
    }
    public void onPrev(View v) {
        station_spin.setSelection(station_spin.getSelectedItemPosition() == 0 ? station_spin.getCount() -1 : station_spin.getSelectedItemPosition() - 1);
    }
    //endregion

    //region: Options Events
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        String id = item.getTitle().toString();

        //options switch
        switch (id) {
            case "Manage Stations":
                manageStations();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void manageStations() {
        Intent manageStations = new Intent(this,ManageStations.class);
        ManageStations.setStations(stations);
        startActivity(manageStations);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    //endregion


    @Override
    public void onResume() {
        super.onResume();
        populateSpinner();
    }
}

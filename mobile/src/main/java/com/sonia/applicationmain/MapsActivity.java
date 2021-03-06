package com.sonia.applicationmain;


import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;

import android.location.LocationManager;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import android.os.Parcelable;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private String geoUri;
    private NfcAdapter mNfcAdapter;
    private PendingIntent mPendingIntent;
    private IntentFilter[] mFilters;
    private String[][] mTechLists;
    private Tag detectedTag;
    private LocationManager locationManager;



    //TODO create a listener (background listner) which detecs NFC tag and write the coordinates to it

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);

        mFilters = new IntentFilter[]{
                tagDetected,
        };

        mTechLists = new String[][]{new String[]{Ndef.class.getName()},
                new String[]{NdefFormatable.class.getName()}};

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
            locationManager.setTestProviderEnabled(locationManager.GPS_PROVIDER, true);
        }

    }

    private NdefRecord createRecord(String text) throws UnsupportedEncodingException {
        String lang       = "";
        byte[] textBytes  = text.getBytes();
        byte[] langBytes  = lang.getBytes("US-ASCII");
        int    langLength = langBytes.length;
        int    textLength = textBytes.length;
        byte[] payload    = new byte[1 + langLength + textLength];

        payload[0] = (byte) langLength;

        System.arraycopy(langBytes, 0, payload, 1,              langLength);
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength);

        NdefRecord recordNFC = new NdefRecord(NdefRecord.TNF_WELL_KNOWN,  NdefRecord.RTD_TEXT,  new byte[0], payload);

        return recordNFC;
    }


    private void write(String text, Tag tag) throws IOException, FormatException {

        NdefRecord[] records = { createRecord(text) };
        NdefMessage  message = new NdefMessage(records);
        // Get an instance of Ndef for the tag.
        Ndef ndef = Ndef.get(tag);
        // Enable I/O
        ndef.connect();
        // Write the message
        ndef.writeNdefMessage(message);
        Log.i("Tag",text);
        // Close the connection
        ndef.close();
    }



    @Override
    protected void onResume() {
        super.onResume();
        mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mNfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                //Log.w("INFO ", location.toString());
                if (location != null) {
                    Log.w("INFO ", location.getLongitude() + "   " + location.getLatitude());
                    geoUri = "geo:"+ location.getLatitude()+","+location.getLongitude();
                    //readFromTag(getIntent());
                }
                else{
                    Log.w("INFO ", "no location found");
                }

            }
        });

    }

    @Override
    protected void onNewIntent(Intent intent) {

        Log.i("Foreground dispatch", "Discovered tag with intent: " + intent);
        detectedTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        //String externalType = "nfclab.com:geoService";
        //NdefRecord geoUriRecord = NdefRecord.createUri(geoUri);
        //NdefRecord extRecord = new NdefRecord(NdefRecord.TNF_EXTERNAL_TYPE, externalType.getBytes(), new byte[0], geoUri.getBytes());
       // NdefMessage newMessage = new NdefMessage(new NdefRecord[] { geoUriRecord});
        try {
            write(geoUri,detectedTag);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }

        readFromTag(intent);

    }

    public void readFromTag(Intent intent){

        Ndef ndef = Ndef.get(detectedTag);
        try{
            ndef.connect();
            Parcelable[] messages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (messages != null) {
                NdefMessage[] ndefMessages = new NdefMessage[messages.length];
                for (int i = 0; i < messages.length; i++) {
                    ndefMessages[i] = (NdefMessage) messages[i];
                }
                NdefRecord record = ndefMessages[0].getRecords()[0];

                byte[] payload = record.getPayload();
                String text = new String(payload);
                Log.i("NFCReading ",text);
                addMarkerOnMap(text);
                ndef.close();
            }
        }
        catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Cannot Read From Tag.", Toast.LENGTH_LONG).show();
        }
    }

    private void addMarkerOnMap(String text) {

        String[] splits = text.split(":");
        String[] latlng = splits[1].split(",");
        double lat = Double.parseDouble(latlng[0]);
        double lng = Double.parseDouble(latlng[1]);
        Log.i("Latlng",lat + ","+lng);
        mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(lat, lng))
                        .title("HERE IS MY CAR").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat, lng), 20.0f));

    }






}
package ca.nicholascarr.northhacks;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import io.apptik.widget.MultiSlider;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private HeatmapTileProvider mProvider;
    private ArrayList<Point> points;
    private double minAmount;
    private double maxAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        points = new ArrayList<>();
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MultiSlider multiSlider5 = (MultiSlider) findViewById(R.id.range_slider5);

        multiSlider5.setOnThumbValueChangeListener(new MultiSlider.OnThumbValueChangeListener() {
            @Override
            public void onValueChanged(MultiSlider multiSlider,
                                       MultiSlider.Thumb thumb,
                                       int thumbIndex,
                                       int value)
            {
                if (thumbIndex == 0) {
                    minAmount = value * 10;
                } else {
                    maxAmount = value * 10;
                }
                ArrayList<LatLng> coordinates = new ArrayList<>();
                coordinates.add(points.get(0).getLatLng());
                for (Point point: points) {
                    if (point.getAmount() > minAmount && point.getAmount() < maxAmount) {
                        coordinates.add(point.getLatLng());
                    }
                }
                addHeatMap(coordinates);
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Toronto, Ontario.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Toronto and move the camera
        LatLng toronto = new LatLng(43.5, -79.5);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(toronto, 10));
        try {
            addHeatMap(readItems(R.raw.point_info, 0, 1000));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addHeatMap(List<LatLng> list) {
        // Get the data: latitude/longitude positions of police stations.

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private ArrayList<LatLng> readItems(int resource, double min, double max) throws JSONException {
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        ArrayList<LatLng> coordinates = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double amount = object.getDouble("amount");
            double lat = object.getDouble("latitude");
            double lng = object.getDouble("longitude");
            String merchant = object.getString("merchant");
            Point point = new Point(lat, lng, amount, merchant);
            points.add(point);
            if (amount > min && amount < max) {
                coordinates.add(point.getLatLng());
            }
        }
        return coordinates;
    }
}

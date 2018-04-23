package pkali.transportationapp.backend;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pkali.transportationapp.R;

/**
 * Created by shivasuri on 3/24/18.
 */

public class MyExpandableListAdapter extends BaseExpandableListAdapter implements OnMapReadyCallback {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listHashMap;

    private List<NamedLocation> namedLocations;
    private List<GoogleMap> maps;
    private List<MapView> mapViews;
    //private List<Boolean> clicked;

    GoogleMap map;
    MapView mapView;

    private int currIndex;
    private int latestGroup;
    private TextView title;

    private List<View> layouts;

    //this is a POJO class with bunch of getters and setters
    public MyExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
    }

    public MyExpandableListAdapter(Context context, List<String> listDataHeader, HashMap<String, List<String>> listHashMap, List<NamedLocation> locations) {
        this.context = context;
        this.listDataHeader = listDataHeader;
        this.listHashMap = listHashMap;
        this.namedLocations = locations;
        this.maps = new ArrayList<>();
        this.layouts = new ArrayList<>();
        this.mapViews = new ArrayList<>();
        //this.clicked = new ArrayList<>();
        for (int i = 0; i < listDataHeader.size(); i++) {
            maps.add(null);
            layouts.add(null);
            mapViews.add(null);
            latestGroup = -1;
//            clicked.add(false);
//
//            View view = R.id.;
//            layouts.add(i, view);
//            MapView mapView = (MapView) view.findViewById(R.id.map);
//            mapViews.add(i, mapView);
//            // MapsInitializer.initialize(context);
//            //setMapLocation(maps.get(groupPosition), mapView);
//
//            if (mapViews.get(i) != null) {
//                Log.d("MAPVIEW", "NONNULL MAP");
//                // Initialize the MapView
//                mapViews.get(i).onCreate(null);
//                // Set the map ready callback to receive the GoogleMap object
//                mapViews.get(i).getMapAsync(this);
//                while (maps.get(i) == null);
//            }
        }
    }

    @Override
    public int getGroupCount() {
        return listDataHeader.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listDataHeader.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listHashMap.get(listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        Log.v("HEADER:", headerTitle);

        if (view == null) {
            Log.v("HEADER:", "null View");
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ride_name, null);
        }
        TextView ride_name = (TextView) view.findViewById(R.id.ride_name);
        Log.v("HEADER:", ride_name.toString());
        // ride_name.setTypeface(null, Typeface.BOLD);

        ride_name.setText(headerTitle);
        Log.v("HEADER:", "finishesGroupView");
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.ride_item, null);
        }

        TextView ride_item = (TextView) view.findViewById(R.id.ride_item);
        ride_item.setText(childText);
        ride_item.setTypeface(null, Typeface.ITALIC);


        currIndex = groupPosition;
        Log.v("currIndex:", Integer.toString(groupPosition));

        if (maps.get(groupPosition) == null) {
            layouts.add(groupPosition, view);
            MapView mapView = (MapView) view.findViewById(R.id.map);
            mapViews.add(groupPosition, mapView);
            // MapsInitializer.initialize(context);
            // setMapLocation(maps.get(groupPosition), mapView);

            if (mapView != null) {
                Log.d("MAPVIEW", "NONNULL MAP");
                // Initialize the MapView
                mapView.onCreate(null);
                //clicked.add(currIndex, true);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);

                //setMapLocation(groupPosition);
                //bindView(groupPosition);
            } else {
                Log.d("MAPVIEW", "NULL MAP");
            }
        }

/*
        if (maps.get(groupPosition) == null) {
            layouts.add(groupPosition, view);
            MapView mapView = (MapView) view.findViewById(R.id.map);
            mapViews.add(groupPosition, mapView);
            // MapsInitializer.initialize(context);
            // setMapLocation(maps.get(groupPosition), mapView);
            if (mapViews.get(groupPosition) != null) {
                Log.d("MAPVIEW", "NONNULL MAP");
                // Initialize the MapView
                mapViews.get(groupPosition).onCreate(null);
                //clicked.add(currIndex, true);
                // Set the map ready callback to receive the GoogleMap object
                mapViews.get(groupPosition).getMapAsync(this);
                //setMapLocation(groupPosition);
                //bindView(groupPosition);
            } else {
                Log.d("MAPVIEW", "NULL MAP");
            }
        }
*/
        // setMapLocation(groupPosition);
        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.v("OnMapReady, CurrIndex", "" + currIndex);
        MapsInitializer.initialize(context);

        this.map = map;
        // maps.add(currIndex, map);
        setMapLocation(currIndex);
//        for (int i = 0; i < clicked.size(); i++) {
//            if (clicked.get(i)) {
//                maps.add(currIndex, map);
//                setMapLocation(currIndex);
//            }
//        }

    }

    /**
     * Displays a {} on a
     * {@link GoogleMap}.
     * Adds a marker and centers the camera on the NamedLocation with the normal map type.
     */
    private void setMapLocation(int pos) {
        if (map == null) {
            Log.d("setMapLocation()", "map is null");
            return;
        }
        /*
        if (maps.get(currIndex) == null) {
            Log.d("setMapLocation()", "map is null");
            return;
        }*/

        NamedLocation data = namedLocations.get(currIndex); //(NamedLocation) mapViews.get(pos).getTag();
        if (data == null) {
            Log.d("setMapLocation()", "Location tag is null");
            return;
        }

        // Add a marker for this item and set the camera
        MarkerOptions srcMarker = new MarkerOptions();
        srcMarker.title(namedLocations.get(currIndex).src);
        srcMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        map.addMarker(srcMarker.position(data.locationSrc));

        MarkerOptions dstMarker = new MarkerOptions();
        dstMarker.title(namedLocations.get(currIndex).dst);
        dstMarker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        map.addMarker(dstMarker.position(data.locationDst));

        Log.v("POSITION::", "" + currIndex);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(srcMarker.getPosition());
        builder.include(dstMarker.getPosition());
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        map.moveCamera(cu);
        map.animateCamera(CameraUpdateFactory.zoomOut());

        // Set the map type  to normal.
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void bindView(int pos) {
        NamedLocation item = namedLocations.get(pos);
        // Store a reference of the ViewHolder object in the layout.
        //layouts.get(pos).setTag(this);
        // Store a reference to the item in the mapView's tag. We use it to get the
        // coordinate of a location, when setting the map location.
        mapViews.get(pos).setTag(item);
        //setMapLocation(pos);
        //title.setText(item.name);
    }
}
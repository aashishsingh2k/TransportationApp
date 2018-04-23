package pkali.transportationapp.backend;

/**
 * Created by shivasuri on 4/8/18.
 */

import com.google.android.gms.maps.model.LatLng;

/**
 * Location represented by a position ({@link LatLng} and a
 * name ({@link String}).
 */
public class NamedLocation {

    public final String src;
    public final String dst;
    public final LatLng locationSrc;
    public final LatLng locationDst;

    NamedLocation(String src, LatLng locationSrc, String dst, LatLng locationDst) {
        this.src = src;
        this.locationSrc = locationSrc;
        this.dst = "";
        this.locationDst = locationDst;
    }
}
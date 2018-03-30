package pkali.transportationapp;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.Fare;
import com.google.maps.model.TravelMode;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.DateTime;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import org.joda.time.ReadableDateTime;

/**
 * Created by prasanna on 3/30/18.
 */

public class PublicTransport {

    private static GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey("AIzaSyBs5XJvoULqoFHJbC9vVpvN7ADfssU-kqo")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    static DirectionsResult getTransitResult(String origin, String destination){

        ReadableDateTime now = new org.joda.time.DateTime();
        try {
            DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.DRIVING).origin(origin)
                    .destination(destination).departureTime(now)
                    .await();
            return result;
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    static Marker addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0]
                        .startLocation.lng)).title(results.routes[0].legs[0].startAddress));
        Marker m = mMap.addMarker(
                new MarkerOptions().position(new LatLng(results.routes[0].legs[0]
                        .endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .snippet(getEndLocationTitle(results)));

        return m;
    }

    static String getEndLocationTitle(DirectionsResult results){

        try{
            BigDecimal fare = results.routes[0].fare.value;
            return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" +
                    results.routes[0].legs[0].distance.humanReadable+ " Fare : " +
                    fare;
        } catch (Exception e) {
            return "Time :" + results.routes[0].legs[0].duration.humanReadable + " Distance :" +
                    results.routes[0].legs[0].distance.humanReadable;
        }
    }
}

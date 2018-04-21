package pkali.transportationapp.Price;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.api.client.http.GenericUrl;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.Fare;
import com.google.maps.model.TravelMode;
import com.google.android.gms.maps.model.LatLng;
import com.google.api.client.util.DateTime;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.joda.time.ReadableDateTime;

import pkali.transportationapp.Price.PlaceAutoComplete;
import pkali.transportationapp.Price.PlacesAutocompleteList;

/**
 * Created by prasanna on 3/30/18.
 */

public class PublicTransport {
    // Create our transport.
    private static final HttpTransport transport = new ApacheHttpTransport();

    private static final JacksonFactory jacksonFactory = new JacksonFactory();

    // Fill in the API key you want to use.
    private static final String API_KEY = "INSERT YOUR API KEY HERE";

    // The different Places API endpoints.
    private static final String PLACES_SEARCH_URL =  "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";

    private static final boolean PRINT_AS_STRING = false;

    // Moscone Center, Howard Street, San Francisco, CA, United States
    static double srcLat;
    static double srcLong;


    private static GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3)
                .setApiKey("AIzaSyBs5XJvoULqoFHJbC9vVpvN7ADfssU-kqo")
                .setConnectTimeout(1, TimeUnit.SECONDS)
                .setReadTimeout(1, TimeUnit.SECONDS)
                .setWriteTimeout(1, TimeUnit.SECONDS);
    }

    public static DirectionsResult getTransitResult(String origin, String destination){

        ReadableDateTime now = new org.joda.time.DateTime();
        try {
            DirectionsResult result = DirectionsApi.newRequest(getGeoContext())
                    .mode(TravelMode.TRANSIT).origin(origin)
                    .destination(destination).departureTime(now)
                    .await();
            srcLong = result.routes[0].legs[0]
                    .startLocation.lng;
            srcLat = result.routes[0].legs[0]
                    .startLocation.lat;
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

    public static Marker addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(results.routes[0].legs[0].startLocation.lat,results.routes[0].legs[0]
                        .startLocation.lng)).title(results.routes[0].legs[0].startAddress));
        Marker m = mMap.addMarker(
                new MarkerOptions().position(new LatLng(results.routes[0].legs[0]
                        .endLocation.lat,results.routes[0].legs[0].endLocation.lng))
                        .title(results.routes[0].legs[0].startAddress)
                        .snippet(getEndLocationTitle(results)));

        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));

        return m;
    }

    static String getBusStation(){
        HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
        HttpRequest request = null;
        try {
            request = httpRequestFactory.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
        } catch (IOException e) {
            e.printStackTrace();
        }

        request.getUrl().put("key", "AIzaSyBs5XJvoULqoFHJbC9vVpvN7ADfssU-kqo");
        request.getUrl().put("location", srcLat + "," + srcLong);
        request.getUrl().put("radius", 500);
        request.getUrl().put("rankby", true);
        request.getUrl().put("types", "bus_station");

        try {
            String s = "";
            PlacesAutocompleteList places = request.execute().parseAs(PlacesAutocompleteList.class);
            for (PlaceAutoComplete place : places.predictions) {
                System.out.println(place);
                s = s + place + " ";
            }
            return s;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getEndLocationTitle(DirectionsResult results){
        BasicConfigurator.configure();
        return "Trip Summary: "  + " Start Address: " +
                results.routes[0].legs[0].startAddress + " End Address: " +
                results.routes[0].legs[0].endAddress + " Time: " +
                results.routes[0].legs[0].duration.humanReadable + " Distance :" +
                results.routes[0].legs[0].distance.humanReadable + "" + results.routes[0].summary;
        //+ " Nearby Bus station: " + getBusStation();

//        try{
//            BigDecimal fare = results.routes[0].fare.value;
//            return  "Time :"+ results.routes[0].legs[0].duration.humanReadable + " Distance :" +
//                    results.routes[0].legs[0].distance.humanReadable+ " Fare : " +
//                    fare;
//        } catch (Exception e) {
//            return "Time :" + results.routes[0].legs[0].duration.humanReadable + " Distance :" +
//                    results.routes[0].legs[0].distance.humanReadable;
//        }
    }

    public static HttpRequestFactory createRequestFactory(final HttpTransport transport) {

        return transport.createRequestFactory(new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
                JsonObjectParser parser = new JsonObjectParser(jacksonFactory);
                request.setParser(parser);
            }
        });
    }
}

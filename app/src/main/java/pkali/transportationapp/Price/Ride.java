package pkali.transportationapp.Price;

/**
 * Created by pkali on 4/8/2018.
 */

public class Ride {
    //uber or lyft
    private String company;
    //Ride type (Lyft Line, Uber POOL, UBER X, etc.)
    private String name;
    //product id (identifies different ride types in dif areas)
    private String productId;
    //estimated distance in miles
    private float distance;
    //estimated duration of trip from start to finish locations in seconds
    private int duration;
    //max estimate of cost in cents
    private int highPriceEst;
    //min estimate of cost in cents
    private int lowPriceEst;
    //estimate of eta of ride in seconds
    private int eta;

    public Ride(String company, String name, String productId, float distance, int duration, int highPriceEst, int lowPriceEst, int eta) {
        this.company = company;
        this.name = name;
        this.productId = productId;
        this.distance = distance;
        this.duration = duration;
        this.highPriceEst = highPriceEst;
        this.lowPriceEst = lowPriceEst;
        this.eta = eta;
    }

    public String getCompany() {
        return company;
    }

    public String getProductId() {
        return productId;
    }

    //returns ride type name
    public String getName() {
        return name;
    }

    //returns estimated distance of trip in miles
    public float getDistance() {
        return distance;
    }

    //returns estimated duration of trip in seconds
    public int getDuration() {
        return duration;
    }

    //returns average estimated price in dollars
    public float getPrice() {
        return ((highPriceEst + lowPriceEst) / 2.0f ) / 100.0f;
    }

    //returns high price estimate in dollars
    public float getHighPriceEst() {
        return highPriceEst / 100.0f;
    }

    //returns low price estimate in dollars
    public float getLowPriceEst() {
        return lowPriceEst / 100.0f;
    }

    //returns driver eta in seconds
    public int getEta() {
        return eta;
    }
}

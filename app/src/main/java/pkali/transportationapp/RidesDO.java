package pkali.transportationapp;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "transportationappv-mobilehub-2063038980-Rides")

public class RidesDO {
    private String _userId;
    private String _time;
    private Double _destLat;
    private Double _destLon;
    private Double _srcLat;
    private Double _srcLon;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBIndexHashKey(attributeName = "userId", globalSecondaryIndexName = "user")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "time")
    @DynamoDBIndexRangeKey(attributeName = "time", globalSecondaryIndexName = "user")
    public String getTime() {
        return _time;
    }

    public void setTime(final String _time) {
        this._time = _time;
    }
    @DynamoDBAttribute(attributeName = "dest_lat")
    public Double getDestLat() {
        return _destLat;
    }

    public void setDestLat(final Double _destLat) {
        this._destLat = _destLat;
    }
    @DynamoDBAttribute(attributeName = "dest_lon")
    public Double getDestLon() {
        return _destLon;
    }

    public void setDestLon(final Double _destLon) {
        this._destLon = _destLon;
    }
    @DynamoDBAttribute(attributeName = "src_lat")
    public Double getSrcLat() {
        return _srcLat;
    }

    public void setSrcLat(final Double _srcLat) {
        this._srcLat = _srcLat;
    }
    @DynamoDBAttribute(attributeName = "src_lon")
    public Double getSrcLon() {
        return _srcLon;
    }

    public void setSrcLon(final Double _srcLon) {
        this._srcLon = _srcLon;
    }

}

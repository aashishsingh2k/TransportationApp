package pkali.transportationapp.backend;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "transportationappv-mobilehub-2063038980-RideTable")

public class RideTableDO {
    private String _userId;
    private Long _timestamp;
    private String _date;
    private String _dstAddr;
    private Double _dstLat;
    private Double _dstLon;
    private String _srcAddr;
    private Double _srcLat;
    private Double _srcLon;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "timestamp")
    @DynamoDBAttribute(attributeName = "timestamp")
    public Long getTimestamp() {
        return _timestamp;
    }

    public void setTimestamp(final Long _timestamp) {
        this._timestamp = _timestamp;
    }
    @DynamoDBAttribute(attributeName = "date")
    public String getDate() {
        return _date;
    }

    public void setDate(final String _date) {
        this._date = _date;
    }
    @DynamoDBAttribute(attributeName = "dst_addr")
    public String getDstAddr() {
        return _dstAddr;
    }

    public void setDstAddr(final String _dstAddr) {
        this._dstAddr = _dstAddr;
    }
    @DynamoDBAttribute(attributeName = "dst_lat")
    public Double getDstLat() {
        return _dstLat;
    }

    public void setDstLat(final Double _dstLat) {
        this._dstLat = _dstLat;
    }
    @DynamoDBAttribute(attributeName = "dst_lon")
    public Double getDstLon() {
        return _dstLon;
    }

    public void setDstLon(final Double _dstLon) {
        this._dstLon = _dstLon;
    }
    @DynamoDBAttribute(attributeName = "src_addr")
    public String getSrcAddr() {
        return _srcAddr;
    }

    public void setSrcAddr(final String _srcAddr) {
        this._srcAddr = _srcAddr;
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
package com.blackbox.model;

public class LocationDataModel {

    public String viewed_satellite;
    public String used_satellite;
    public String PDOP;
    public String HDOP;
    public String VDOP;
    public String altitude_MSL;
    public double Lat;
    public double Lon;
    public String  accuracy_M;
    public String  accuracy_V;
    public String  data_Date;
    public String  Time;
    public String  GPS_Fix_Status;
    public String  Satelite_Used;

    public String getSatelite_Used() {
        return Satelite_Used;
    }

    public void setSatelite_Used(String satelite_Used) {
        Satelite_Used = satelite_Used;
    }

    public String getAccuracy_V() {
        return accuracy_V;
    }

    public void setAccuracy_V(String accuracy_V) {
        this.accuracy_V = accuracy_V;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String speed;

    public String getViewed_satellite() {
        return viewed_satellite;
    }

    public void setViewed_satellite(String viewed_satellite) {
        this.viewed_satellite = viewed_satellite;
    }

    public String getUsed_satellite() {
        return used_satellite;
    }

    public void setUsed_satellite(String used_satellite) {
        this.used_satellite = used_satellite;
    }

    public String getPDOP() {
        return PDOP;
    }

    public void setPDOP(String PDOP) {
        this.PDOP = PDOP;
    }

    public String getHDOP() {
        return HDOP;
    }

    public void setHDOP(String HDOP) {
        this.HDOP = HDOP;
    }

    public String getVDOP() {
        return VDOP;
    }

    public void setVDOP(String VDOP) {
        this.VDOP = VDOP;
    }

    public String getAltitude_MSL() {
        return altitude_MSL;
    }

    public void setAltitude_MSL(String altitude_MSL) {
        this.altitude_MSL = altitude_MSL;
    }

    public double getLat() {
        return Lat;
    }

    public void setLat(double lat) {
        Lat = lat;
    }

    public double getLon() {
        return Lon;
    }

    public void setLon(double lon) {
        Lon = lon;
    }

    public String getAccuracy_M() {
        return accuracy_M;
    }

    public void setAccuracy_M(String accuracy_M) {
        this.accuracy_M = accuracy_M;
    }

    public String getData_Date() {
        return data_Date;
    }

    public void setData_Date(String data_Date) {
        this.data_Date = data_Date;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getGPS_Fix_Status() {
        return GPS_Fix_Status;
    }

    public void setGPS_Fix_Status(String GPS_Fix_Status) {
        this.GPS_Fix_Status = GPS_Fix_Status;
    }
}



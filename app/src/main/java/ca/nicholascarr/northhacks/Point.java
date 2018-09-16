package ca.nicholascarr.northhacks;

import com.google.android.gms.maps.model.LatLng;

public class Point {
    private LatLng latLng;
    private double amount;
    private String merchant;

    Point(double lat, double lng, double amount, String merchant) {
        this.latLng = new LatLng(lat, lng);
        this.amount = amount;
        this.merchant = merchant;
    }

    public double getLat() {
        return latLng.latitude;
    }

    public double getLng() {
        return latLng.longitude;
    }

    public double getAmount() {
        return amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public LatLng getLatLng () {
        return this.latLng;
    }
}

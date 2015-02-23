package ua.intersog.homework.hotncold;

import com.google.android.gms.maps.model.LatLng;

public class MyPoint {

    public double x, y, z, radius;

    MyPoint(double x, double y, double z, double radius) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
    }

    MyPoint(LatLng c) {
        double lat = c.latitude * Math.PI / 180.0;
        double lon = c.longitude * Math.PI / 180.0;
        double cosLon = Math.cos(lon);
        double sinLon = Math.sin(lon);
        double cosLat = Math.cos(lat);
        double sinLat = Math.sin(lat);
        radius = EarthRadiusInMeters(lat);
        x = cosLon * cosLat * radius;
        y = sinLon * cosLat * radius;
        z = sinLat * radius;
    }

    public static double getAzimuth(LatLng startPoint, LatLng endPoint) {
        MyPoint bp = new MyPoint(startPoint);
        MyPoint br = MyPoint.rotateGlobe(startPoint, endPoint, bp.radius);
        double theta = Math.atan2(br.z, br.y) * 180.0 / Math.PI;
        double azimuth = 90.0 - theta;
        if (azimuth < 0.0) {
            azimuth += 360.0;
        }
        if (azimuth > 360.0) {
            azimuth -= 360.0;
        }
        return azimuth;
    }

    private double EarthRadiusInMeters(double latitudeRadians) {
        final double a = 6378137.0;  // equatorial radius in meters
        final double b = 6356752.3;  // polar radius in meters
        double cos = Math.cos(latitudeRadians);
        double sin = Math.sin(latitudeRadians);
        double t1 = a * a * cos;
        double t2 = b * b * sin;
        double t3 = a * cos;
        double t4 = b * sin;
        return Math.sqrt((t1 * t1 + t2 * t2) / (t3 * t3 + t4 * t4));
    }

    private static MyPoint rotateGlobe(LatLng a, LatLng b, double bradius) {
        LatLng br = new LatLng(b.latitude, b.longitude - a.longitude);
        MyPoint brp = new MyPoint(br);
        brp.x *= (bradius / brp.radius);
        brp.y *= (bradius / brp.radius);
        brp.z *= (bradius / brp.radius);
        brp.radius = bradius;

        double alat = -a.latitude * Math.PI / 180.0;
        double acos = Math.cos(alat);
        double asin = Math.sin(alat);

        double bx = (brp.x * acos) - (brp.z * asin);
        double by = brp.y;
        double bz = (brp.x * asin) + (brp.z * acos);
        return new MyPoint(bx, by, bz, bradius);
    }

    public static double getDistance(LatLng a, LatLng b) {

//      latitude and longitude in radians
        double radianLatA = a.latitude * Math.PI / 180.0;
        double radianLatB = b.latitude * Math.PI / 180.0;
        double radianLonA = a.longitude * Math.PI / 180.0;
        double radianLonB = b.longitude * Math.PI / 180.0;

//      cos and sin for latitudes and for difference between longitude
        double cosLatA = Math.cos(radianLatA);
        double cosLatB = Math.cos(radianLatB);
        double sinLatA = Math.sin(radianLatA);
        double sinLatB = Math.sin(radianLatB);
        double delta = radianLonB - radianLonA;
        double cosDelta = Math.cos(delta);
        double sinDelta = Math.sin(delta);

//      counting distance
        double y = Math.sqrt(Math.pow(cosLatB * sinDelta, 2) + Math.pow(cosLatA * sinLatB - sinLatA * cosLatB * cosDelta, 2));
        double x = sinLatA * sinLatB + cosLatA * cosLatB * cosDelta;
        final double radius = 6372795.0;
        double dist = Math.atan2(y, x) * radius;

        return dist;
    }
}

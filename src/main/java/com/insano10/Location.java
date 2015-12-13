package com.insano10;

import twitter4j.GeoLocation;

public enum Location
{
    NORTH_LONDON(new GeoLocation(51.610289, -0.104315), 8d),
    SOUTH_LONDON(new GeoLocation(51.382761, -0.121415), 8d),
    EAST_LONDON(new GeoLocation(51.521564, 0.124194), 8d),
    WEST_LONDON(new GeoLocation(51.502694, -0.359235), 8d),
    CENTRAL_LONDON(new GeoLocation(51.503077, -0.126492), 8d);

    private final GeoLocation geoLocation;
    private final double radiusKm;

    Location(final GeoLocation geoLocation, final double radiusKm)
    {
        this.geoLocation = geoLocation;
        this.radiusKm = radiusKm;
    }

    public GeoLocation getGeoLocation()
    {
        return geoLocation;
    }

    public double getRadiusKm()
    {
        return radiusKm;
    }
}

package com.insano10;

import twitter4j.GeoLocation;

public enum Location
{
    NORTH_LONDON(new GeoLocation(51.574358, -0.100861), 4d),
    SOUTH_LONDON(new GeoLocation(51.472250, -0.077556), 4d),
    EAST_LONDON(new GeoLocation(51.518801, -0.009096), 4d),
    WEST_LONDON(new GeoLocation(51.517895, -0.211563), 4d),
    CENTRAL_LONDON(new GeoLocation(51.517592, -0.100861), 4d);

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

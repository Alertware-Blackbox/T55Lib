package com.blackbox.utils;

/**
 * Created by Krishnendu on 4/6/2016.
 */
public class DATUM {
    /**
     *  Datum's name. E.g.: Standard datum is WGS84.
     */
    public String name;
    /**
     *  This datum's ellipsoid (index into ellipsoid table in class {@link COORD}).
     */
    public int ellipsoid;
    /**
     *  dx from center.
     */
    public int dx;
    /**
     *  dy from center.
     */
    public int dy;
    /**
     *  dz from center.
     */
    public int dz;


    /**
     *  DATUM constructor.
     *
     *@param  _n   Name
     *@param  _e   Ellipsoid (index into table in class {@link COORD}).
     *@param  _dx  dx
     *@param  _dy  dy
     *@param  _dz  dz
     */
    public DATUM(String _n, int _e, int _dx, int _dy, int _dz )
    {
        name = _n;
        ellipsoid = _e;
        dx = _dx;
        dy = _dy;
        dz = _dz;
    }
}

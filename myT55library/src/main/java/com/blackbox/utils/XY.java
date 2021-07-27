package com.blackbox.utils;

/**
 * Created by Krishnendu on 4/6/2016.
 */
public class XY {
    /**
     *  GaussKrueger x or latitude.
     */
    public double x;
    /**
     *  GaussKrueger y or longitude.
     */
    public double y;


    /**
     *  Create uninitialized coordinate.
     */
    public XY() { }


    /**
     *  Create a coordinate.
     *
     *@param  _x  x or latitude.
     *@param  _y  y or longitude.
     */
    public XY(double _x, double _y )
    {
        x = _x;
        y = _y;
    }
}

package com.blackbox.utils;

/**
 * Created by Anshu on 4/6/2016.
 */
public class ELLIPSOID {
    /**
     *  Ellipsoid's name.
     */
    public String name;
    /* name of ellipsoid */
    /**
     *  Semi-major axis (in meters).
     */
    public double a;
    /* semi-major axis, meters */
    /**
     *  Inverse flattening.
     */
    public double invf;


    /* 1/f */
    /**
     *  Create ELLIPSOID instance.
     *
     *@param  _n  name
     *@param  _a  semi-major axis (in meters).
     *@param  _i  inverse flattening.
     */
    public ELLIPSOID(String _n, double _a, double _i )
    {
        name = _n;
        a = _a;
        invf = _i;
    }
}

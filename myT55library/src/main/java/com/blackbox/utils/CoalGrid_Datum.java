package com.blackbox.utils;

/**
 * Created by krishnendu on 8/23/2016.
 */
public class CoalGrid_Datum {

    public static XY translate(double lat,double lon)
    {
        double E0=3000000,N0=1000000;
        double g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,AA,AB,AC,AD,AE,AF,AG;
        double AH,AI,AJ,AK,AL,AM,AN,AO,AP,AQ,AR,AS,AT,AU,AV,AW,AX,AY,AZ,BA,BB,BC,BD,BE,BF,BG,BH;
        double op1,op2;
        g=deg2rad(lat);
        h=lon;
        i=6377301.243;
        j=6356100.231;
        k=(Math.pow(i,2)- Math.pow(j,2))/ Math.pow(i,2);
        l= Math.sqrt(k);
        m=l/2;
        n=3000000;
        o=1000000;
        p=20421174.5092207;
        q=0.329759217152455;
        r=0.303754667109034;
        s=0.377283283970982;
        t=0.340368652311444;
        u=0.999394420825938;
        v=18253058.0129509;
        w=6379604.07984102;
        x=17;
        y=15;
        z=0;
        AA=21;
        AB=15;
        AC=0;
        AD=(x*3600+y*60+z)/206264.806246;
        AE=(AA*3600+AB*60+AC)/206264.806246;
        AF=79;
        AG=2.71828182845905;
        AH=(Math.sin(g))*l;
        AI= Math.log(Math.pow((1 - AH) / (1 + AH),m)*(Math.tan(((45*3600/206264.806246)+g/2))));
        AJ=(i/(Math.sqrt(1 - k * Math.pow(Math.sin(g), 2))));
        AK=(j*(1-k))/ Math.pow((1 - l * Math.pow(Math.sin(h), 2)), 1.5);
        AL=AJ*(Math.cos(g));
        AM=(i/ Math.sqrt(1 - k * Math.pow(Math.sin(AD), 2)))* Math.cos(AD);
        AN=(i/ Math.sqrt(1 - k * Math.pow(Math.sin(AE), 2)))* Math.cos(AE);
        AO= Math.log(AM)- Math.log(AN);
        AP= Math.log(Math.pow((1 - l * Math.sin(AD)) / (1 + l * Math.sin(AD)),m)* Math.tan(45*3600/206264.806246+AD/2));
        AQ= Math.log(Math.pow(((1 - l* Math.sin(AE))/(1+l* Math.sin(AE))),m)* Math.tan((45*3600/206264.806246+AE/2)));
        AR=(AO/(AQ-AP));
        AS=(-AR*AP);
        AT=(Math.pow(AG,AS))*AR;
        AU=(AM/AT);
        AV=(-AR*AQ);
        AW=(Math.pow(AG,AV))*AR;
        AX=AN/AW;
        AY=(AU+AX)/2;
        AZ= Math.asin(AR);
        BA= Math.log(Math.pow(((1 - l * Math.sin(AZ)) / (1 + l * Math.sin(AZ))), m) * Math.tan((45 * 3600 / 206264.806246 + AZ / 2)));
        BB=AY/(Math.pow(AG,(AR*BA)));
        BC=u*w*(1/ Math.tan(AZ));
        BD=(BB+BC)/2;
        BE=(AY/(Math.pow(AG,(AR*AI))));
        BF=AR*(h-AF);
        BG=(Math.sin(BF*3600/206264.806246))*BE;
        BH=(BD-(BE* Math.cos(BF*3600/206264.806246)));
        op1=N0+BH;
        op2=E0+BG;
        return new XY(op1,op2);
    }
    private static double deg2rad(double deg) {

        double r=deg *  Math.PI;
        return r/ 180;
    }
}

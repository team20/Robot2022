package frc.robot;

public class RegressionRangeFinder extends RangeFinder{
    public double RPM;
    public double Angle;
    public double[] values;

    public RegressionRangeFinder() {
        double[] values = new double[2];
    }


    public double[] getAngleAndRPM(double distanceToWall) {
        // RPM THEN ANGLE
        RPM = Math.sqrt(381575.5734*distanceToWall + 9047432.37);
        Angle = Math.sqrt(22.74831065*distanceToWall + 85.87766489);
        //System.out.println("RPM: " + RPM);
        double[] ret = {Angle-12, RPM};
        return ret;
    }


    
}
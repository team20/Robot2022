package frc.robot;

public class RegressionRangeFinder extends RangeFinder{
    public double RPM;
    public double Angle;

    public RegressionRangeFinder() {
    }


    public double[] getAngleAndRPM(double distanceToWall) {
        // RPM THEN ANGLE
        RPM = 6.41867*distanceToWall +1843.21663;
        Angle = .08482*distanceToWall + 0.889998;
        //System.out.println("RPM: " + RPM);
        double[] ret = {Angle, RPM};
        return ret;
    }


    
}
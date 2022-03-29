package frc.robot;

import frc.robot.subsystems.LimelightSubsystem;

public class RegressionRangeFinder extends RangeFinder{
    public double RPM;
    public double Angle;

    public RegressionRangeFinder() {
    }


    public double[] getAngleAndRPM(double limelightAngle) {
        double angle = LimelightSubsystem.get().getYAngle();
        // RPM THEN ANGLE
        RPM = -17.589*limelightAngle + 2694.4;
        Angle = -0.2671*limelightAngle + 12.545;
        //System.out.println("RPM: " + RPM);
        double[] ret = {Angle, RPM};
        return ret;
    }
    
}
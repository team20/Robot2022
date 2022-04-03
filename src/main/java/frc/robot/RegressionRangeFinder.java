package frc.robot;

import frc.robot.subsystems.LimelightSubsystem;

public class RegressionRangeFinder extends RangeFinder{
    public double RPM;
    public double Angle;

    public RegressionRangeFinder() {
    }


    public double[] getAngleAndRPM(double limelightAngle) {
        double angle = LimelightSubsystem.get().getYAngle();

        if(angle >= -13){
            // RPM THEN ANGLE
            RPM = -17.451*limelightAngle + 2609.4;
            Angle = -0.2659*limelightAngle + 11.255;
            //System.out.println("RPM: " + RPM);
        } else{
            // RPM THEN ANGLE
            RPM = -82.992*limelightAngle + 1665.1;
            Angle = -0.2659*limelightAngle + 11.255;
            System.out.println("ANGLE: " + Angle);
        }
        
        double[] ret = {Angle, RPM};
        return ret;
    }
    
}
package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


/**
 * Interacts with SmartDashboard to print and change values to control PID values.
 */
public class PIDHelper {
    public PIDHelper(double P, double I, double D, double target) {
        defaultP = P;
        defaultI = I;
        defaultD = D;
        defaultTarget = target;
    }

    public void initialize() {
        SmartDashboard.putNumber("P", defaultP);
        SmartDashboard.putNumber("I", defaultI);
        SmartDashboard.putNumber("D", defaultD);
        SmartDashboard.putNumber("Target", defaultTarget);
    }
    public static double getP() {
        return SmartDashboard.getNumber("P", defaultP);
    }
    public static double getI() {
        return SmartDashboard.getNumber("I", defaultI);
    }
    public static double getD() {
        return SmartDashboard.getNumber("D", defaultD);
    }
    public static double getTarget() {
        return SmartDashboard.getNumber("Target", defaultTarget);
    }


    private static double defaultP;
    private static double defaultI;
    private static double defaultD;
    private static double defaultTarget;

}
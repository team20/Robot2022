package frc.robot.subsystems;

import java.util.ArrayList;

import javax.sound.sampled.SourceDataLine;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants.LimelightConstants;
import frc.robot.ShuffleboardLogging;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LimelightSubsystem extends SubsystemBase implements ShuffleboardLogging {
    
    private static LimelightSubsystem s_subsytem;
    public static LimelightSubsystem get(){ return s_subsytem;}

    private final NetworkTable m_limelightTable = NetworkTableInstance.getDefault().getTable("limelight");

    private boolean isTargetVisible;
    private double xAngle, yAngle, distance;
    private ArrayList<Double> averageDistance = new ArrayList<>();

    private double[] rollingAverageStorage = new double[200];
    private int rollingAverageIndex = 0;
    private double totalRollingAverage = 0;

    public LimelightSubsystem() {
        s_subsytem = this;
        //turnOffLight();
    }

    /**
     * Update local data from the limelight network table
     */

    public void periodic() {
        //isLightOn();
        //turnOffLight();
        // SmartDashboard.putNumber("Avg Distance", getAverageDistance());
        // SmartDashboard.putNumber("Distance", getDistance());
        // SmartDashboard.putBoolean("Target Visible", isTargetVisible());
        System.out.println("is light on: " + isLightOn());
        // System.out.println("the x angle is: " + getXAngle());
        // System.out.println("the y angle is: " + getYAngle());

        //System.out.println("limelight y angle" + calculateRollingAverage(getYAngle()));

        //System.out.println("distance is: " + distance);
    }

    /**
     * @return Whether the limelight can see the target
     */
    public boolean isTargetVisible() {
        return isTargetVisible = m_limelightTable.getEntry("tv").getDouble(0) == 1; //0 = not visible, 1 = visible;
    }

    /**
     * @return The x angle of the target center to the limelight crosshair
     */
    public double getXAngle() {
        return xAngle = m_limelightTable.getEntry("tx").getDouble(-1);
    }

    /**
     * @return The y angle of the target center to the limelight crosshair
     */
    public double getYAngle() {
        return yAngle = m_limelightTable.getEntry("ty").getDouble(0);
    }

    /**
     * @return The estimated ground distance from the limelight to the target in inches
     */
    public double getDistance() {
        distance = isTargetVisible()
                ? (LimelightConstants.kTargetHeight - LimelightConstants.kCameraHeight)
                        / (Math.tan(Math.toRadians(LimelightConstants.kCameraAngle + getYAngle())))
                : 0;
        if (distance != 0) { //TODO use this rolling average in the limelight turn command!!!
            averageDistance.add(distance);
            if (averageDistance.size() > 10) {
                averageDistance.remove(0);
            }
        }
        return distance;
    }

    /**
     * @return The averaged ground distance from the limelight to the target over
     *         the past .2 seconds (account for loss in camera view while
     *         movingfast)
     */
    public double getAverageDistance() {
        double sum = 0;
        double size = averageDistance.size();
        for (int i = 0; i < averageDistance.size(); i++) {
            sum += averageDistance.get(i);
        }
        return size > 0 ? sum / size : 0;
    }

    /**
     * @return Whether the LIME light is on
     */
    public boolean isLightOn() {
        return m_limelightTable.getEntry("ledMode").getDouble(-1) == 3; //check force on
    }

    /**
     * Turn on the LIME light
     */
    public void turnOnLight() {
        m_limelightTable.getEntry("ledMode").setNumber(3); //force on
    }

    /**
     * Turn off the LIME light
     */
    public void turnOffLight() {
        m_limelightTable.getEntry("ledMode").setNumber(1); //force off
        //m_limelightTable.getEntry("ledMode").setNumber(1); //force off

    }

    public void configureShuffleboard() {
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Limelight");
        shuffleboardTab.addNumber("X Angle", () -> getXAngle()).withSize(1, 1).withPosition(0, 0)
                .withWidget(BuiltInWidgets.kTextView);
        shuffleboardTab.addNumber("Distance", () -> getDistance()).withSize(1, 1).withPosition(1, 0)
                .withWidget(BuiltInWidgets.kTextView);
        shuffleboardTab.addBoolean("Target Visible", () -> isTargetVisible()).withSize(1, 1).withPosition(0, 1)
                .withWidget(BuiltInWidgets.kBooleanBox);
        shuffleboardTab.addBoolean("Light On", () -> isLightOn()).withSize(1, 1).withPosition(1, 1)
                .withWidget(BuiltInWidgets.kBooleanBox);
        //TODO: add in a y-value?
    }

    public double calculateRollingAverage(double updatedAngleMeasurement) {
        totalRollingAverage -= rollingAverageStorage[rollingAverageIndex];
        rollingAverageStorage[rollingAverageIndex] = updatedAngleMeasurement;
        totalRollingAverage += rollingAverageStorage[rollingAverageIndex];
        rollingAverageIndex++;
        if (rollingAverageIndex >= rollingAverageStorage.length) {
            rollingAverageIndex = 0;
        }
        return totalRollingAverage / rollingAverageStorage.length;
    }
}

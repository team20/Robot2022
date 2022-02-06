package frc.robot.commands.LimelightCommands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LimelightSubsystem;

public class LimelightCommand extends CommandBase {

    private final LimelightSubsystem m_limelightSubsystem;
    private final DriveSubsystem m_driveSubsystem;
    
    private final PIDController m_turnController = new PIDController(
            LimelightConstants.kTurnP, LimelightConstants.kTurnI, LimelightConstants.kTurnD,
            LimelightConstants.kRefreshRate);
    private final double m_setpointAngle;

    private final PIDController m_distanceController = new PIDController(
            LimelightConstants.kDisP, LimelightConstants.kDisI, LimelightConstants.kDisD,
            LimelightConstants.kRefreshRate);
    private final double m_setpointDistance;

    private double[] rollingAverageStorage = new double[LimelightConstants.kRollingAverageSize];
    private int rollingAverageIndex = 0;
    private double totalRollingAverage = 0;

    /**
     * Use the limelight to reach a desired angle to the powerport
     * 
     * @param limelightSubsystem The limelight subsystem to gather data from
     * @param driveSubsystem     The drivetrain subsystem to be used
     */

    public LimelightCommand(LimelightSubsystem limelightSubsystem, DriveSubsystem driveSubsystem,
            double setpointAngle, double setpointDistance) {
        m_limelightSubsystem = limelightSubsystem;
        m_driveSubsystem = driveSubsystem;
        m_setpointAngle = setpointAngle;
        m_setpointDistance = setpointDistance;
        // if (setpointDistance > 113) { //change based on min ability to see target
        //     m_setpointDistance = 178;
        // } else {
        //     m_setpointDistance = 72;
        // }
        addRequirements(m_limelightSubsystem, m_driveSubsystem);

        //setpoint distance should be -6 inches for limelight on robot
        //also -8.75 inches for the setback of tape in the hub

    }

    /**
     * Set the tolerance and goal of the PIDs
     */
    public void initialize() {
        m_limelightSubsystem.turnOnLight();

        m_turnController.setTolerance(LimelightConstants.kTurnTolerance);
        m_turnController.setSetpoint(m_setpointAngle);
    
      // m_distanceController.setTolerance(LimelightConstants.kDistanceTolerance);
      // m_distanceController.setSetpoint(m_setpointDistance);
        System.out.println("started limelight");
    }

    /**
     * Update the motor outputs
     */
    public void execute() {
        System.out.println("running limelight");
        double measurementDistance = m_limelightSubsystem.getDistance();
        double measurementAngle = m_limelightSubsystem.getXAngle();
       // System.out.println("distance: "+ measurementDistance);
       // System.out.println("angle: " + measurementAngle);
       // double averageMeasurementAngle = calculateRollingAverage(measurementAngle);
       // double averageMeasurementDistance = calculateRollingAverage(measurementDistance);
       // if (!isFinished()) {
            double turnOutput = m_turnController.calculate(measurementAngle);
         //   System.out.println("turn output: " + turnOutput);
          //  double distanceOutput = m_distanceController.calculate(measurementDistance);
          //  System.out.println("distance output: " + distanceOutput);
            
            if (turnOutput > 0 && turnOutput < .05) { // TODO: fine tune the minimum oomf required for a talon
                turnOutput = 0.05;
            } else if (turnOutput < 0 && turnOutput > -.05) {
                turnOutput = -.05;
            } 

            // if (distanceOutput > 0 && distanceOutput < .05) { // TODO: fine tune the minimum oomf required for a talon
            //     distanceOutput = 0.05;
            // } else if (distanceOutput < 0 && distanceOutput > -.05) {
            //     distanceOutput = -.05;
            // }
            
            double leftSpeed = turnOutput;// + distanceOutput;
            double rightSpeed = -turnOutput;// + distanceOutput;

            if (leftSpeed > 0.25) {
                leftSpeed = 0.25;
            } else if (leftSpeed < -0.25) {
                leftSpeed = -0.25;
            }

            if (rightSpeed > 0.25) {
                rightSpeed = 0.25;
            } else if (rightSpeed < -0.25) {
                rightSpeed = -0.25;
            }

          //  System.out.println("limelight left speed: " + leftSpeed);
          //  System.out.println("limelight right speed: " + rightSpeed);
            m_driveSubsystem.tankDrive(leftSpeed, rightSpeed); // TODO: might have to reverse output signs based on motor alignment
        //}
    }

    /**
     * Stop the drivetrain at the end of the command
     */
    public void end(boolean interupted) {
        m_limelightSubsystem.turnOffLight();
        m_driveSubsystem.tankDrive(0, 0);
    }

    public boolean isFinished() { // TODO: assumes you would only press the button once, no holding down
        return m_turnController.atSetpoint(); // && m_distanceController.atSetpoint();
        //return m_distanceController.atSetpoint();
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
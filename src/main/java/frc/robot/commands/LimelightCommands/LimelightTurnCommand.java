package frc.robot.commands.LimelightCommands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LimelightSubsystem;

public class LimelightTurnCommand extends CommandBase {

    private final LimelightSubsystem m_limelightSubsystem;
    private final DriveSubsystem m_driveSubsystem;
    
    private final PIDController m_turnController = new PIDController(
            LimelightConstants.kTurnP, LimelightConstants.kTurnI, LimelightConstants.kTurnD,
            LimelightConstants.kRefreshRate);
    private final double m_setpointAngle;

   
    /**
     * Use the limelight to reach a desired angle to the powerport
     * 
     * @param limelightSubsystem The limelight subsystem to gather data from
     * @param driveSubsystem     The drivetrain subsystem to be used
     */

    public LimelightTurnCommand(LimelightSubsystem limelightSubsystem, DriveSubsystem driveSubsystem, double setpointAngle) {
        m_limelightSubsystem = limelightSubsystem;
        m_driveSubsystem = driveSubsystem;
        m_setpointAngle = setpointAngle;
    
        addRequirements(m_limelightSubsystem, m_driveSubsystem);
    }
    public LimelightTurnCommand(LimelightSubsystem limelightSubsystem, DriveSubsystem driveSubsystem) {
            m_limelightSubsystem = limelightSubsystem;
            m_driveSubsystem = driveSubsystem;
            m_setpointAngle = 0; //default setpoint angle is 0(directly in front of the robot)

            addRequirements(m_limelightSubsystem, m_driveSubsystem);
    }
    /**
     * Set the tolerance and goal of the PIDs
     */
    public void initialize() {
        m_limelightSubsystem.turnOnLight();

        m_turnController.setTolerance(LimelightConstants.kTurnTolerance);
        m_turnController.setSetpoint(m_setpointAngle);
    
    }

    /**
     * Update the motor outputs
     */
    public void execute() {
        double measurementAngle = m_limelightSubsystem.getXAngle();
            double turnOutput = m_turnController.calculate(measurementAngle); 
             // Apply max power limit
            if (Math.abs(turnOutput) > .25) { turnOutput = .25 * Math.signum(turnOutput); }
        
            // Apply min power limit
            if (Math.abs(turnOutput) < .05) {
                turnOutput = Math.signum(turnOutput) * 0.05;
            }

            m_driveSubsystem.tankDrive(turnOutput, -1 * turnOutput); // Flip these if the bot turns the wrong direction
    }

    /**
     * Stop the drivetrain at the end of the command
     */
    public void end(boolean interupted) {
        m_limelightSubsystem.turnOffLight();
        m_driveSubsystem.tankDrive(0, 0);
    }

    public boolean isFinished() { // TODO: assumes you would only press the button once, no holding down
        return m_turnController.atSetpoint();
    }

}
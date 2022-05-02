package frc.robot.commands.LimelightCommands;

import java.time.Duration;
import java.time.Instant;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LimelightConstants;
// import frc.robot.Constants.ArduinoConstants.LEDModes;
// import frc.robot.Constants.ArduinoConstants.LEDColors;
// import frc.robot.commands.ArduinoCommands.UpdateLEDsCommand;
// import frc.robot.commands.ArduinoCommands.UpdateShooterLEDsCommand;
// import frc.robot.subsystems.ArduinoSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LimelightSubsystem;

public class LimelightTurnCommand extends CommandBase {

    // private final LimelightSubsystem m_limelightSubsystem;
    // private final DriveSubsystem m_driveSubsystem;
    // private final ArduinoSubsystem m_arduinoSubsystem;

    private final PIDController m_turnController = new PIDController(
            LimelightConstants.kTurnP, LimelightConstants.kTurnI, LimelightConstants.kTurnD,
            LimelightConstants.kRefreshRate);
    private final double m_setpointAngle;
    private Instant m_startTime;

    /**
     * Use the limelight to reach a desired angle to the powerport
     * 
     * @param limelightSubsystem The limelight subsystem to gather data from
     * @param driveSubsystem     The drivetrain subsystem to be used
     */

    public LimelightTurnCommand(double setpointAngle) {

        m_setpointAngle = setpointAngle;

        addRequirements(LimelightSubsystem.get(), DriveSubsystem.get());
    }

    /**
     * Set the tolerance and goal of the PIDs
     */
    public void initialize() {
        m_startTime = Instant.now();

        //LimelightSubsystem.get().turnOnLight();

        m_turnController.setTolerance(LimelightConstants.kTurnTolerance);
        m_turnController.setSetpoint(m_setpointAngle);

    }

    /**
     * Update the motor outputs
     */
    public void execute() {
        //m_limelightSubsystem.turnOnLight();
        double measurementAngle = LimelightSubsystem.get().getXAngle();
        double turnOutput = m_turnController.calculate(measurementAngle);
        // Apply max power limit
        if (Math.abs(turnOutput) > .25) {
            turnOutput = .25 * Math.signum(turnOutput);
        }

        // Apply min power limit
        if (Math.abs(turnOutput) < .1) {
            turnOutput = .1;//Math.signum(turnOutput) * 2;
        }

        DriveSubsystem.get().tankDrive(-turnOutput, turnOutput); // Flip these if the bot turns the wrong direction
    }

    /**
     * Stop the drivetrain at the end of the command
     */
    public void end(boolean interupted) {
        //LimelightSubsystem.get().turnOffLight();
        DriveSubsystem.get().tankDrive(0, 0);
    }

    public boolean isFinished() { // TODO: assumes you would only press the button once, no holding down
        double elapsed = Duration.between(m_startTime, Instant.now()).toMillis();
        if (elapsed < 100) {
            return false;
        }
        return m_turnController.atSetpoint();
    }

}
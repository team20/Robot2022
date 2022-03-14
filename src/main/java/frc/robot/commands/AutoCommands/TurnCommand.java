package frc.robot.commands.AutoCommands;

import java.time.Duration;
import java.time.Instant;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;

public class TurnCommand extends CommandBase {

    private final DriveSubsystem m_driveSubsystem;
    private final double m_angle;
    private final PIDController m_turnController = new PIDController(DriveConstants.kTurnP, DriveConstants.kTurnI,
            DriveConstants.kTurnD);
    private Instant m_startTime;

    public TurnCommand(DriveSubsystem driveSubsystem, double turnAngle) {
        m_driveSubsystem = driveSubsystem;
        m_angle = turnAngle;
        addRequirements(m_driveSubsystem);
    }

    public void initialize() {
        //m_driveSubsystem.zeroHeading();
        m_startTime = Instant.now();
        m_driveSubsystem.zeroHeading();
        m_turnController.setSetpoint(m_angle);
        m_turnController.setTolerance(DriveConstants.kTurnTolerance);
    }

    public void execute() {
        double measurementAngle = m_driveSubsystem.getHeading();
        //System.out.println("Angle: " + measurementAngle);
        SmartDashboard.putNumber("Angle ", measurementAngle);
        if(Duration.between(m_startTime, Instant.now()).toMillis() > 250){
            double turnOutput = m_turnController.calculate(measurementAngle);
            m_driveSubsystem.arcadeDrive(0, turnOutput, -turnOutput);
        }
        
    }

    public void end(boolean interupted) {
        m_driveSubsystem.tankDrive(0, 0);
    }

    public boolean isFinished() {
        if(Duration.between(m_startTime, Instant.now()).toMillis() < 250){
            return false;
        }
        return m_turnController.atSetpoint();
    }
}

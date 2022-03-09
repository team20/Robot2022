package frc.robot.commands.AutoCommands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;

public class TurnCommand extends CommandBase {

    private final DriveSubsystem m_driveSubsystem;
    private final double m_angle;
    private final PIDController m_turnController = new PIDController(DriveConstants.kTurnP, DriveConstants.kTurnI,
            DriveConstants.kTurnD);

    public TurnCommand(DriveSubsystem driveSubsystem, double turnAngle) {
        m_driveSubsystem = driveSubsystem;
        m_angle = turnAngle;
        addRequirements(m_driveSubsystem);
    }

    public void initialize() {
        m_driveSubsystem.zeroHeading();
        m_turnController.setSetpoint(m_angle);
        m_turnController.setTolerance(DriveConstants.kTurnTolerance);
    }

    public void execute() {
        double measurementAngle = m_driveSubsystem.getHeading();
        double turnOutput = m_turnController.calculate(measurementAngle);
        m_driveSubsystem.arcadeDrive(0, turnOutput, -turnOutput);
    }

    public void end(boolean interupted) {
        m_driveSubsystem.tankDrive(0, 0);
    }

    public boolean isFinished() {
        return m_turnController.atSetpoint();
    }
}

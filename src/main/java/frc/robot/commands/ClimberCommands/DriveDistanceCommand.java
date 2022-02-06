package frc.robot.commands.ClimberCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveSubsystem;

public class DriveDistanceCommand extends CommandBase {

    private final DriveSubsystem m_driveSubsystem;
    private final double m_distance;

    //pass in distance as meters
    public DriveDistanceCommand(DriveSubsystem driveSubsystem, double distance) {
        m_driveSubsystem = driveSubsystem;
        m_distance = distance;
        addRequirements(m_driveSubsystem);
    }

    public void initialize() {
        m_driveSubsystem.resetEncoders();
    }

    public void execute() {
        m_driveSubsystem.arcadeDrive(0.1, 0, 0);
    }

    public boolean isFinished() {
        return Math.abs(m_driveSubsystem.getAverageEncoderDistance()) >= m_distance;
    }

    public void end() {

    }

    
}

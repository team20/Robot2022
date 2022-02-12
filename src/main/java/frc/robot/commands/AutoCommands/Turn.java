package frc.robot.commands.AutoCommands;


import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveSubsystem;


public class Turn extends CommandBase {
    private final DriveSubsystem m_driveSubsystem;
    private final double turn;
    public Turn(DriveSubsystem driveSubsystem, double CounterClockWiseTurnRadians) {
        m_driveSubsystem = driveSubsystem;
        turn = CounterClockWiseTurnRadians;
        addRequirements(m_driveSubsystem);
    }

    public void initialize() {
        m_driveSubsystem.arcadeDrive(0,turn);
    }
}

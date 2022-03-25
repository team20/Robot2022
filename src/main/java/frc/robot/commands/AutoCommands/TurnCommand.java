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
    private final double m_turnAngle;
    private double m_setpointAngle;
    private final PIDController m_turnController = new PIDController(DriveConstants.kTurnP, DriveConstants.kTurnI,
            DriveConstants.kTurnD);
    private double m_startAngle;
    //private Instant m_startTime;

    public TurnCommand(DriveSubsystem driveSubsystem, double turnAngle) {
        m_driveSubsystem = driveSubsystem;
        m_turnAngle = turnAngle;
        addRequirements(m_driveSubsystem);
    }

    public void initialize() {

        m_driveSubsystem.zeroHeading();
        System.out.println("starting turn");
        m_setpointAngle = m_driveSubsystem.getHeading() + m_turnAngle;
        //m_startTime = Instant.now();
        //m_driveSubsystem.zeroHeading();
        m_turnController.setSetpoint(m_turnAngle);
        //m_turnController.setSetpoint(0);
        m_turnController.setTolerance(DriveConstants.kTurnTolerance);
    }

    public void execute() {
        double measurementAngle = m_driveSubsystem.getHeading();
        //System.out.println("Angle: " + measurementAngle);
        SmartDashboard.putNumber("Angle ", measurementAngle);
        
        double turnOutput = m_turnController.calculate(measurementAngle);
        //double turnOutput = m_turnController.calculate(getError(measurementAngle));
        m_driveSubsystem.arcadeDrive(0, turnOutput, -turnOutput);
        
    }

    private double getError(double measurementAngle){
        double error = m_setpointAngle - measurementAngle;
        if(error > 180){
            error -= 360;
        }else if(error < -180){
            error += 360;
        }
        return error;
    }
    public void end(boolean interupted) {
        m_driveSubsystem.tankDrive(0, 0);
    }

    public boolean isFinished() {
        return m_turnController.atSetpoint();
    }
}

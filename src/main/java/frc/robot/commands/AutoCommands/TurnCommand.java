package frc.robot.commands.AutoCommands;

// import java.time.Duration;
// import java.time.Instant;

import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;

public class TurnCommand extends CommandBase {

    private final double m_angle;
    private final ProfiledPIDController m_turnController = new ProfiledPIDController(DriveConstants.kTurnP, DriveConstants.kTurnI,
            DriveConstants.kTurnD, new TrapezoidProfile.Constraints(720, 720));
    // private Instant m_startTime;

    public TurnCommand(double turnAngle) {
        m_angle = turnAngle;
        addRequirements(DriveSubsystem.get());
    }

    public void initialize() {
        //m_startTime = Instant.now();
        System.out.println("starting turn");
        // double goalAngle = m_angle + DriveSubsystem.get().getHeading();
        // if(goalAngle > 180){
        //     goalAngle -= 360;
        // }else if(goalAngle < -180){
        //     goalAngle += 360;
        // }
        //DriveSubsystem.get().zeroHeading();
        m_turnController.setGoal(m_angle);
        m_turnController.enableContinuousInput(-180, 180);
        m_turnController.setTolerance(DriveConstants.kTurnTolerance);
    }

    public void execute() {
        double measurementAngle = DriveSubsystem.get().getHeading();
        SmartDashboard.putNumber("Angle ", measurementAngle);
        double turnOutput = m_turnController.calculate(measurementAngle);
        DriveSubsystem.get().arcadeDrive(0, turnOutput, -turnOutput);
        
    }

    private double getError(double measurementAngle){
        double error = m_angle - measurementAngle;
        if(error > 180){
            error -= 360;
        }else if(error < -180){
            error += 360;
        }
        return error;
    }
    public void end(boolean interupted) {
      //System.out.println("ending turn: " + Duration.between(m_startTime, Instant.now()).toMillis());
      DriveSubsystem.get().tankDrive(0, 0);
    }

    public boolean isFinished() {
      if(DriveSubsystem.get().getHeading() < m_angle + DriveConstants.kTurnTolerance && DriveSubsystem.get().getHeading() > m_angle - DriveConstants.kTurnTolerance){
        return true;
      }else{
        return false;
      }

    }
}

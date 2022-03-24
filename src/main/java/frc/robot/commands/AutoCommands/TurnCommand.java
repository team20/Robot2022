package frc.robot.commands.AutoCommands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.ProfiledPIDController;
import edu.wpi.first.math.trajectory.TrapezoidProfile;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;

public class TurnCommand extends CommandBase {

    private final double m_angle;
    private final ProfiledPIDController m_turnController = new ProfiledPIDController(DriveConstants.kTurnP, DriveConstants.kTurnI,
            DriveConstants.kTurnD, new TrapezoidProfile.Constraints(360, 720));

    public TurnCommand(double turnAngle) {
        m_angle = turnAngle;
        addRequirements(DriveSubsystem.get());
    }

    public void initialize() {
        System.out.println("starting turn");
        double goalAngle = m_angle + DriveSubsystem.get().getHeading();
        if(goalAngle > 180){
            goalAngle -= 360;
        }else if(goalAngle < -180){
            goalAngle += 360;
        }
        m_turnController.setGoal(goalAngle);
        m_turnController.enableContinuousInput(-180, 180);
        m_turnController.setTolerance(DriveConstants.kTurnTolerance);
    }

    public void execute() {
        double measurementAngle = DriveSubsystem.get().getHeading();
        SmartDashboard.putNumber("Angle ", measurementAngle);
        double turnOutput = MathUtil.clamp(m_turnController.calculate(measurementAngle), -1, 1);
        DriveSubsystem.get().tankDrive(turnOutput, -turnOutput);
        
    }

    public void end(boolean interupted) {
        DriveSubsystem.get().tankDrive(0, 0);
    }

    public boolean isFinished() {
        return m_turnController.atSetpoint();
    }
}

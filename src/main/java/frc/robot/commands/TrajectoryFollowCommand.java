package frc.robot.commands;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;

public class TrajectoryFollowCommand extends RamseteCommand {

    private final DriveSubsystem m_driveSubsystem;

    /**
     * Follow a trajectory, either created through Pathweaver or manually
     * 
     * @param driveSubsystem The subsystem to be used
     * @param trajectory     The trajectory that the ramsete controller seeks to
     *                       follow
     */
    public TrajectoryFollowCommand(DriveSubsystem driveSubsystem, Trajectory trajectory) {

        super(trajectory, driveSubsystem::getPose,
                new RamseteController(DriveConstants.kRamseteB, DriveConstants.kRamseteZeta),
                new SimpleMotorFeedforward(
                        DriveConstants.ksVolts,
                        DriveConstants.kvVoltSecondsPerMeter,
                        DriveConstants.kaVoltSecondsSquaredPerMeter),
                DriveConstants.kDriveKinematics,
                driveSubsystem::getWheelSpeeds,
                new PIDController(DriveConstants.kPDriveVel, 0, 0),
                new PIDController(DriveConstants.kPDriveVel, 0, 0),
                driveSubsystem::tankDriveVolts,
                driveSubsystem);
        m_driveSubsystem = driveSubsystem;

        //PID control with spark max PIDs
        // new RamseteCommand(trajectory, driveSubsystem::getPose,
        // new RamseteController(DriveConstants.kRamseteB, DriveConstants.kRamseteZeta),
        // DriveConstants.kDriveKinematics, (left, right) -> driveSubsystem.tankDriveVelocity(
        //     new DifferentialDriveWheelSpeeds(left, right)), driveSubsystem);

        // Reset odometry to the starting pose of the trajectory.
        m_driveSubsystem.resetOdometry(trajectory.getInitialPose());

    }

    /**
     * Stop the drivetrain at the end of the command
     */
    public void end(boolean interrupted) {
        m_driveSubsystem.tankDriveVolts(0, 0);
    }
}
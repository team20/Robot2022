package frc.robot.commands.AutoCommands;

import java.util.List;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.DriveSubsystem;


public class Taxi extends CommandBase {

    public Taxi(DriveSubsystem driveSubsystem, double distance) {
        
        // List of waypoints and trajectory config constructor. Uses distance to drive straight.
        Trajectory path = TrajectoryGenerator.generateTrajectory(
        List.of(driveSubsystem.getPose(), 
            new Pose2d( new Translation2d(distance,driveSubsystem.getPose().getRotation()),
                driveSubsystem.getPose().getRotation())
            ),
            DriveConstants.kTrajectoryConfig
        );

    
    
        //  Trajectory t = TrajectoryGenerator.generateTrajectory(waypoints, DriveConstants.kTrajectoryConfig);

        // m_drive
        // Trajectory t = new Trajectory(TrajectoryGenerator)

        // addCommands(
        // new TrajectoryFollowCommand(driveSubsystem, trajectory)
        // );
        // }

        // public SitAndShootHigh(FlywheelSubsystem flywheelSubsystem, HoodSubsystem
        // hoodSubsystem,
        // IndexerSubsystem indexerSubsystem) {
        // addCommands(
        // new ShootSetupCommand(flywheelSubsystem, hoodSubsystem, () ->
        // FieldLocation.TARMACHIGH),
        // new AutoIndexCommand(indexerSubsystem, flywheelSubsystem::atSetpoint)
        // );

    }
   

}
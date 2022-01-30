package frc.robot.commands;

import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Constants.FieldLocation;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.IndexerSubsystem;

public class Taxi extends CommandBase {

        public Taxi(DriveSubsystem driveSubsystem) {
            Trajectory t = new Trajectory(TrajectoryGenerator)

                addCommands(
                    new TrajectoryFollowCommand(driveSubsystem, trajectory)
                );
        }
}
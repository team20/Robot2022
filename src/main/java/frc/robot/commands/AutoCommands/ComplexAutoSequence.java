package frc.robot.commands.AutoCommands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.FieldLocation;
import frc.robot.commands.ShooterCommands.AutoIndexCommand;
import frc.robot.commands.ShooterCommands.ShootSetupCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.IndexerSubsystem;

public class ComplexAutoSequence extends SequentialCommandGroup {


    public ComplexAutoSequence(DriveSubsystem driveSubsystem, FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem, IndexerSubsystem indexerSubsystem, int choice) {
      
        // TODO integrate into the CommandComposer 
      
        switch(choice) {
           // Simple Taxi
            case 1:
                addCommands(new Taxi(driveSubsystem, 3));
                break;
            // Shoot then taxi
            case 2:
                addCommands(new SitAndShootHigh(flywheelSubsystem, hoodSubsystem, indexerSubsystem),
                new Taxi(driveSubsystem, 3));
                break;
            // Taxi, Intake, Shoot Twice (Upper Cargo)
            case 3: 
                addCommands(new Taxi(driveSubsystem,3),
                new IntakeCommand(),
                new SitAndShootHigh(flywheelSubsystem,hoodSubsystem,indexerSubsystem),
                // Wait command
                new SitAndShootHigh(flywheelSubsystem,hoodSubsystem,indexerSubsystem)
                );
                break;
            // Collect and shoot bottom two balls.
            case 4:
                addCommands(new Taxi(driveSubsystem,3),
                new IntakeCommand(),
                new SitAndShootHigh(flywheelSubsystem,hoodSubsystem,indexerSubsystem),
                // Wait
                new SitAndShootHigh(flywheelSubsystem,hoodSubsystem,indexerSubsystem),
                new Turn(driveSubsystem, 300),
                new Taxi(driveSubsystem, 3),
                new IntakeCommand(),
                new SitAndShootHigh(flywheelSubsystem,hoodSubsystem,indexerSubsystem)
                );
            



        }
    }
   

}
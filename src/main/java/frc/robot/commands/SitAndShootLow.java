package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import frc.robot.Constants.FieldLocation;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.IndexerSubsystem;

public class SitAndShootLow extends ParallelCommandGroup {

        public SitAndShootLow(FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem,
                        IndexerSubsystem indexerSubsystem) {
                addCommands(
                        new ShootSetupCommand(flywheelSubsystem, hoodSubsystem, () -> FieldLocation.TARMACLOW),
                        new AutoIndexCommand(indexerSubsystem, flywheelSubsystem::atSetpoint)
                );
        }
}
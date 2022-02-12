// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.ClimberCommands.*;
import frc.robot.commands.IndexerCommands.*;
import frc.robot.commands.LimelightCommands.*;
import frc.robot.commands.ShooterCommands.*;
import frc.robot.subsystems.*;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.Constants.*;

/** Add your docs here. */
public class CommandComposer {

    private ArduinoSubsystem m_arduinoSubsystem;
    private DriveSubsystem m_driveSubsystem;
    private FlywheelSubsystem m_flywheelSubsystem;
    private HoodSubsystem m_hoodSubsystem;
    private IndexerSubsystem m_indexerSubsystem;
    private IntakeArmSubsystem m_intakeArmSubsystem;
    private IntakeSubsystem m_intakeSubsytem;
    private LimelightSubsystem m_limelightSubsystem;

    public CommandComposer(ArduinoSubsystem arduinoSubsystem, DriveSubsystem driveSubsystem,
            FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem, IndexerSubsystem indexerSubsystem,
            IntakeArmSubsystem intakeArmSubsystem, IntakeSubsystem intakeSubsystem,
            LimelightSubsystem limelightSubsystem) {
        m_arduinoSubsystem = arduinoSubsystem;
        m_driveSubsystem = driveSubsystem;
        m_flywheelSubsystem = flywheelSubsystem;
        m_hoodSubsystem = hoodSubsystem;
        m_indexerSubsystem = indexerSubsystem;
        m_intakeArmSubsystem = intakeArmSubsystem;
        m_intakeSubsytem = intakeSubsystem;
        m_limelightSubsystem = limelightSubsystem;

    }

    public Command getAimAndShootCommand(String shootClass) {

        // base of the hub is 8.75" offset from the tape at the top
        double distanceBase = (m_limelightSubsystem.getDistance() - 8.75) / 12.0;

        Command aimCommand = new LimelightTurnCommand(m_limelightSubsystem, m_driveSubsystem);

        Command shootCommand = new SequentialCommandGroup(
                ShootCommandComposer.getShootCommand(m_flywheelSubsystem, m_hoodSubsystem, distanceBase, shootClass),
                IndexerCommandComposer.getShootCommand(m_indexerSubsystem));

        return new SequentialCommandGroup(aimCommand, shootCommand);
    }

    public Command getClimbCommand() {
        TelescopeHookCommand telescopeWait = new TelescopeHookCommand(
                TelescopeHookCommand.Operation.CMD_POSITION_SETTLE, 0);
        SlideHookCommand slideHookWait = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION_SETTLE, 0);

        SequentialCommandGroup SlideToStart = new SequentialCommandGroup(
                new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kStartPosition),
                slideHookWait);

        SequentialCommandGroup TelescopeExtend = new SequentialCommandGroup(new TelescopeHookCommand(
                TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition), telescopeWait);

        DriveDistanceCommand DriveToBar = new DriveDistanceCommand(m_driveSubsystem, DriveConstants.toBarPosition);

        SequentialCommandGroup TelescopeRetract = new SequentialCommandGroup(new TelescopeHookCommand(
                TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition), telescopeWait);

        SequentialCommandGroup SlideToTelescope = new SequentialCommandGroup(
                new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition),
                slideHookWait);

        SequentialCommandGroup TelescopeRelease = new SequentialCommandGroup(
                new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION,
                        TelescopeHookConstants.kDisengageFromRetractedPosition),
                telescopeWait);

        SlideHookCommand SlideToTelescopeBehind = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE,
                SlideHookConstants.kTelescopeBehindRung);
        SlideHookCommand SlideToTelescopeTouching = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE,
                SlideHookConstants.kTelescopeTouchingRung);

        SequentialCommandGroup TelescopeControlledRetract = new SequentialCommandGroup(new TelescopeHookCommand(
                TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlled), telescopeWait);

        SequentialCommandGroup SlideControlledExtend = new SequentialCommandGroup(
                new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kControlled),
                slideHookWait);

        ParallelCommandGroup ControlledMove = new ParallelCommandGroup(TelescopeControlledRetract,
                SlideControlledExtend);

        SequentialCommandGroup TelescopeRetractFromControlled = new SequentialCommandGroup(
                new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION,
                        TelescopeHookConstants.kDisengageFromControlledPosition),
                telescopeWait);

        return new SequentialCommandGroup(SlideToStart, TelescopeExtend, DriveToBar, TelescopeRetract, SlideToTelescope,
                TelescopeRelease, SlideToTelescopeBehind, TelescopeExtend, SlideToTelescopeTouching,
                TelescopeControlledRetract, SlideControlledExtend, ControlledMove, TelescopeRetractFromControlled,
                SlideToStart, TelescopeExtend, SlideToTelescope, TelescopeRelease,
                SlideToTelescopeBehind, TelescopeExtend, SlideToTelescopeTouching, ControlledMove, TelescopeRetract);
    }
}

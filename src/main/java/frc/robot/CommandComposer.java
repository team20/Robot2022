// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.command.WaitCommand;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.DeferredCommand;
import frc.robot.commands.ClimberCommands.*;
import frc.robot.commands.IndexerCommands.*;
import frc.robot.commands.IntakeCommands.IntakeCommand;
import frc.robot.commands.LimelightCommands.*;
import frc.robot.commands.ShooterCommands.*;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.ArduinoSubsystem;
import frc.robot.Constants.*;
/** Add your docs here. */
public class CommandComposer {

    public static Command getAimAndShootCommand(String shootClass) {

        ArduinoSubsystem m_arduinoSubsystem = new ArduinoSubsystem();

        // base of the hub is 8.75" offset from the tape at the top
        double distanceBase = (LimelightSubsystem.get().getDistance() - 8.75) / 12.0;

        Command aimCommand = new LimelightTurnCommand(LimelightSubsystem.get(), DriveSubsystem.get(), m_arduinoSubsystem, 0);
        //Command aimCommand = new LimelightTurnCommand(m_limelightSubsystem, m_driveSubsystem, m_arduinoSubsystem);

        Command startFlywheelAndPrepRTS = new ParallelCommandGroup(new DeferredCommand(() -> (ShootCommandComposer.getShootCommand(distanceBase, shootClass))), new DeferredCommand(IndexerCommandComposer::getReadyToShoot));
        Command shootCommand = new SequentialCommandGroup(startFlywheelAndPrepRTS, new DeferredCommand(IndexerCommandComposer::getShootCommand), ShootCommandComposer.getShootStopCommand());

        return new SequentialCommandGroup(aimCommand, shootCommand);
    }
    public static Command getSpitCommand(){
        return new SequentialCommandGroup(new IndexerCommand(IndexerCommand.Operation.CMD_REV_MAN), new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF), new IndexerCommand(IndexerCommand.Operation.CMD_STOP), new IntakeCommand(IntakeCommand.Operation.CMD_RUN_REV));
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

        DriveDistanceCommand DriveToBar = new DriveDistanceCommand(DriveSubsystem.get(), DriveConstants.toBarPosition);

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

    public static Command getLoadCommand(){
        System.out.println("Getting Load Command");
        //Command prepIndexer = IndexerCommandComposer.prep(m_indexerSubsystem);
        IntakeCommand startIntake = new IntakeCommand(IntakeCommand.Operation.CMD_RUN_FWD);
        IndexerCommand waitRTF = new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF);
        IntakeCommand stopIntake = new IntakeCommand(IntakeCommand.Operation.CMD_STOP);
        Command index = new DeferredCommand(IndexerCommandComposer::getLoadCommand);

        return new SequentialCommandGroup(startIntake, waitRTF, stopIntake, index);
    }
    public static Command getManualFlywheelCommand(){
        NetworkTableEntry flywheelSpeed = Shuffleboard.getTab("Testing").add("Flywheel RPMS",1).getEntry();
        return new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, flywheelSpeed.getDouble(0));
    }
}

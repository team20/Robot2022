// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
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
import frc.robot.Constants.*;
/** Add your docs here. */
public class CommandComposer {

    public static Command getAimAndPrepCommand(ShootCommandComposer.Operation shootType) {
        // base of the hub is 8.75" offset from the tape at the top
        // double distanceBase = (LimelightSubsystem.get().getDistance() - 8.75) / 12.0;

        Command aimCommand = new LimelightTurnCommand(LimelightSubsystem.get(), DriveSubsystem.get());
        //Command aimCommand = new LimelightTurnCommand(m_limelightSubsystem, m_driveSubsystem, m_arduinoSubsystem);

        Command startFlywheelAndPrepRTS = new ParallelCommandGroup(new DeferredCommand(() -> ShootCommandComposer.getShootCommand(10, shootType)));
        // Command shootCommand = new SequentialCommandGroup(startFlywheelAndPrepRTS, new DeferredCommand(IndexerCommandComposer::getShootCommand), ShootCommandComposer.getShootStopCommand());

        return new SequentialCommandGroup(startFlywheelAndPrepRTS);
    }
    public static Command getShootCommand() {
        
        Command shootCommand = new SequentialCommandGroup(new DeferredCommand(IndexerCommandComposer::getShootCommand), ShootCommandComposer.getShootStopCommand());

        return new SequentialCommandGroup(shootCommand);
    }
    public static Command getSpitCommand(){
        return new SequentialCommandGroup(new IndexerCommand(IndexerCommand.Operation.CMD_REV_MAN), new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF), new IndexerCommand(IndexerCommand.Operation.CMD_STOP), new IntakeCommand(IntakeCommand.Operation.CMD_RUN_REV));
    }
    public static Command getPresetShootCommand(ShootCommandComposer.Operation shootType) {
       return new ParallelCommandGroup(new DeferredCommand(() -> (ShootCommandComposer.getShootCommand(0, shootType))), new DeferredCommand(IndexerCommandComposer::getReadyToShoot));
    }

    public static Command getTraversalClimbCommand() {

        SlideHookCommand SlideToStart1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kStartPosition);
        TelescopeHookCommand TelescopeExtend1=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        // DriveDistanceCommand DriveToBar1 = new DriveDistanceCommand(DriveSubsystem.get(), DriveConstants.toBarPosition);
        
        TelescopeHookCommand TelescopeRetract1 =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        SlideHookCommand SlideToTelescope1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);
    ParallelCommandGroup Transition1=new ParallelCommandGroup(TelescopeRetract1, SlideToTelescope1.withTimeout(SlideHookConstants.kUntilTelescopeDown));

        // ParallelCommandGroup TelescopeToSlideTransfer=new ParallelCommandGroup(TelescopeRetract1, )

        TelescopeHookCommand TelescopeRelease1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromRetractedPosition);
        SlideHookCommand SlideToTelescopeBehind1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE, SlideHookConstants.kTelescopeBehindRung);
        TelescopeHookCommand TelescopeExtend2=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        SlideHookCommand SlideToTelescopeTouching1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE,SlideHookConstants.kTelescopeTouchingRung);

        TelescopeHookCommand TelescopeControlledRetract1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlled);
        SlideHookCommand SlideControlledExtend1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kControlled);
        ParallelCommandGroup ControlledMove1 = new ParallelCommandGroup(TelescopeControlledRetract1,SlideControlledExtend1);

        TelescopeHookCommand TelescopeRetractFromControlled1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromControlledPosition);

        SlideHookCommand SlideToStart2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kStartPosition);
        TelescopeHookCommand TelescopeRetract2=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        SlideHookCommand SlideToTelescope2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);
        TelescopeHookCommand TelescopeRelease2 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromRetractedPosition);
        SlideHookCommand SlideToTelescopeBehind2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE, SlideHookConstants.kTelescopeBehindRung);
        TelescopeHookCommand TelescopeExtend4=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        SlideHookCommand SlideToTelescopeTouching2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE,SlideHookConstants.kTelescopeTouchingRung);

        TelescopeHookCommand TelescopeControlledRetract2 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlled);
        SlideHookCommand SlideControlledExtend2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kControlled);
        ParallelCommandGroup ControlledMove2 = new ParallelCommandGroup(TelescopeControlledRetract2,SlideControlledExtend2);

        TelescopeHookCommand TelescopeRetract3 =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);

        return new SequentialCommandGroup( 
        TelescopeExtend1, 
        //DriveToBar1, 
        TelescopeRetract1, //on mid bar
        SlideToTelescope1, 
        TelescopeRelease1, 
        SlideToTelescopeBehind1, 
        TelescopeExtend2, 
        SlideToTelescopeTouching1, 



        ControlledMove1, //climbing to high

        TelescopeRetractFromControlled1,

        SlideToStart2, 
        TelescopeRetract2, 
        SlideToTelescope2, 
        TelescopeRelease2,
        SlideToTelescopeBehind2, 
        TelescopeExtend4, 
        SlideToTelescopeTouching2, 



        ControlledMove2, 

        TelescopeRetract3);
    }

    public static Command getHighClimbCommand() {

        SlideHookCommand SlideToStart1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kStartPosition);
        TelescopeHookCommand TelescopeExtend1=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        DriveDistanceCommand DriveToBar1 = new DriveDistanceCommand(DriveSubsystem.get(), DriveConstants.toBarPosition);
        
        TelescopeHookCommand TelescopeRetract1 =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        SlideHookCommand SlideToTelescope1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);
    ParallelCommandGroup Transition1=new ParallelCommandGroup(TelescopeRetract1, SlideToTelescope1.withTimeout(SlideHookConstants.kUntilTelescopeDown));

        TelescopeHookCommand TelescopeRelease1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromRetractedPosition);
        SlideHookCommand SlideToTelescopeBehind1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE, SlideHookConstants.kTelescopeBehindRung);
        TelescopeHookCommand TelescopeExtend2=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        SlideHookCommand SlideToTelescopeTouching1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE,SlideHookConstants.kTelescopeTouchingRung);

        TelescopeHookCommand TelescopeControlledRetract1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlled);
        SlideHookCommand SlideControlledExtend1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kControlled);
        ParallelCommandGroup ControlledMove1 = new ParallelCommandGroup(TelescopeControlledRetract1,SlideControlledExtend1);

        TelescopeHookCommand TelescopeRetract2=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
         return new SequentialCommandGroup( 
        SlideToStart1,
        TelescopeExtend1, 
        DriveToBar1, 
        TelescopeRetract1, //on mid bar
        SlideToTelescope1, 
        TelescopeRelease1, 
        SlideToTelescopeBehind1, 
        TelescopeExtend2, 
        SlideToTelescopeTouching1, 
        ControlledMove1, //climbing to high
        TelescopeRetract2);
    }

    public static Command getLoadCommand(){
        System.out.println("Getting Load Command");
        //Command prepIndexer = IndexerCommandComposer.prep(m_indexerSubsystem);
        IntakeCommand startIntake = new IntakeCommand(IntakeCommand.Operation.CMD_RUN_FWD);
        IndexerCommand waitRTF = new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF);
        // IntakeCommand stopIntake = new IntakeCommand(IntakeCommand.Operation.CMD_STOP);
        Command index = new DeferredCommand(IndexerCommandComposer::getLoadCommand);

        return new SequentialCommandGroup(startIntake, waitRTF, index);
    }
    public static Command getManualFlywheelCommand(){
        NetworkTableEntry flywheelSpeed = Shuffleboard.getTab("Testing").add("Flywheel RPMS",1).getEntry();
        return new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, flywheelSpeed.getDouble(0));
    }
}

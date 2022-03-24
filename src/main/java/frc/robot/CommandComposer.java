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
import frc.robot.commands.AutoCommands.*;
import frc.robot.commands.ClimberCommands.*;
import frc.robot.commands.IndexerCommands.*;
import frc.robot.commands.IntakeCommands.IntakeArmCommand;
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
        
        Command shootCommand = new SequentialCommandGroup(new DeferredCommand(IndexerCommandComposer::getShootCommand));

        return new SequentialCommandGroup(shootCommand);
    }
    public static Command getAutoShootCommand() {
        
        Command shootCommand = new SequentialCommandGroup(new ParallelCommandGroup(new FlywheelCommand(FlywheelCommand.Operation.CMD_SETTLE,0), new HoodCommand(HoodCommand.Operation.CMD_SETTLE,0)).withTimeout(2), new DeferredCommand(IndexerCommandComposer::getShootCommand), new DeferredCommand(IndexerCommandComposer::getShootCommand));

        return new SequentialCommandGroup(shootCommand);
    }
    public static Command getAutoShootCommandNoWait() {
        
        Command shootCommand = new SequentialCommandGroup(new DeferredCommand(IndexerCommandComposer::getShootCommand), new DeferredCommand(IndexerCommandComposer::getShootCommand));

        return new SequentialCommandGroup(shootCommand);
    }
    public static Command getSpitCommand(){
        return new SequentialCommandGroup(new IndexerCommand(IndexerCommand.Operation.CMD_REV_MAN), new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF), new IndexerCommand(IndexerCommand.Operation.CMD_STOP), new IntakeCommand(IntakeCommand.Operation.CMD_RUN_REV));
    }
    public static Command getPresetShootCommand(ShootCommandComposer.Operation shootType) {
       return new ParallelCommandGroup(new DeferredCommand(() -> (ShootCommandComposer.getShootCommand(0, shootType))));
    }

    public static Command getTraversalClimbCommand() {

        SlideHookCommand SlideToStart1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kStartPosition);
        TelescopeHookCommand TelescopeExtend1=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        // DriveDistanceCommand DriveToBar1 = new DriveDistanceCommand(DriveSubsystem.get(), DriveConstants.toBarPosition);
        
        TelescopeHookCommand TelescopeRetract1 =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        SlideHookCommand SlideToTelescope1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);
        ParallelCommandGroup Transition1 = new ParallelCommandGroup(TelescopeRetract1, SlideToTelescope1.withTimeout(SlideHookConstants.kUntilTelescopeDown));

        // ParallelCommandGroup TelescopeToSlideTransfer=new ParallelCommandGroup(TelescopeRetract1, )

        TelescopeHookCommand TelescopeRelease1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromRetractedPosition);
        SlideHookCommand SlideToTelescopeBehind1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE, SlideHookConstants.kTelescopeBehindRung);
        TelescopeHookCommand TelescopeExtend2 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        SlideHookCommand SlideToTelescopeTouching1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE,SlideHookConstants.kTelescopeTouchingRung);

        TelescopeHookCommand TelescopeControlledRetract1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlled);
        SlideHookCommand SlideControlledExtend1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kControlled);
        ParallelCommandGroup ControlledMove1 = new ParallelCommandGroup(TelescopeControlledRetract1,SlideControlledExtend1);

        TelescopeHookCommand TelescopeRetractFromControlled1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromControlledPosition);

        SlideHookCommand SlideToStart2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kStartPosition);
        TelescopeHookCommand TelescopeRetract2 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        SlideHookCommand SlideToTelescope2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);
        TelescopeHookCommand TelescopeRelease2 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromRetractedPosition);
        SlideHookCommand SlideToTelescopeBehind2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE, SlideHookConstants.kTelescopeBehindRung);
        TelescopeHookCommand TelescopeExtend4=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        SlideHookCommand SlideToTelescopeTouching2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_TO_ANGLE,SlideHookConstants.kTelescopeTouchingRung);

        TelescopeHookCommand TelescopeControlledRetract2 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlled);
        SlideHookCommand SlideControlledExtend2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kControlled);
        ParallelCommandGroup ControlledMove2 = new ParallelCommandGroup(TelescopeControlledRetract2,SlideControlledExtend2);

        TelescopeHookCommand TelescopeRetract3 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);

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
        DriveDistanceCommand DriveToBar1 = new DriveDistanceCommand(DriveConstants.toBarPosition);
        
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
        //System.out.println("Getting Load Command");
        //Command prepIndexer = IndexerCommandComposer.prep(m_indexerSubsystem);
        IntakeCommand startIntake = new IntakeCommand(IntakeCommand.Operation.CMD_RUN_FWD);
        IndexerCommand waitRTF = new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF);
        // IntakeCommand stopIntake = new IntakeCommand(IntakeCommand.Operation.CMD_STOP);
        Command index = new DeferredCommand(IndexerCommandComposer::getLoadCommand);
        IndexerCommand waitRTF2 = new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF);
        // IntakeCommand stopIntake = new IntakeCommand(IntakeCommand.Operation.CMD_STOP);
        Command index2 = new DeferredCommand(IndexerCommandComposer::getLoadCommand);

        return new SequentialCommandGroup(startIntake, waitRTF, index, waitRTF2, index2);
    }
    public static Command getAutoLoadCommand(){
        //System.out.println("Getting Load Command");
        //Command prepIndexer = IndexerCommandComposer.prep(m_indexerSubsystem);
        IntakeCommand startIntake = new IntakeCommand(IntakeCommand.Operation.CMD_RUN_FWD);
        IndexerCommand waitRTF = new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF);
        // IntakeCommand stopIntake = new IntakeCommand(IntakeCommand.Operation.CMD_STOP);
        Command index = new DeferredCommand(IndexerCommandComposer::getLoadCommand);
        IndexerCommand waitRTF2 = new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF);
        // IntakeCommand stopIntake = new IntakeCommand(IntakeCommand.Operation.CMD_STOP);
        Command index2 = new DeferredCommand(IndexerCommandComposer::getLoadCommand);

        return new SequentialCommandGroup(startIntake, waitRTF, index);
    }
    public static Command getManualFlywheelCommand(){
        NetworkTableEntry flywheelSpeed = Shuffleboard.getTab("Testing").add("Flywheel RPMS",1).getEntry();
        return new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, flywheelSpeed.getDouble(0));
    }

    // public static Command testAutoIndexer(){//go back straight and shoot
    //     return getAutoShootCommand();
    // }

    public static Command getTwoBallStraight(){//go back straight and shoot
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new waitCommand(1000),
            new ParallelCommandGroup(new DriveDistanceCommand(45), getAutoLoadCommand()).withTimeout(5), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 12), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4200)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
                
        );
    }

    public static Command getTwoBallStarting4Red(){//4
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new TurnCommand( 32).withTimeout(1.5),
            new ParallelCommandGroup(new DriveDistanceCommand(45), getAutoLoadCommand()), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 12), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4200)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
                
        );
    }

    public static Command getTwoBallStarting4Blue(){//4
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new TurnCommand( 23).withTimeout(.75),//was 27 turn
            new ParallelCommandGroup(new DriveDistanceCommand(55), getAutoLoadCommand().withTimeout(5)), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new TurnCommand( -30).withTimeout(.75),
            //new LimelightTurnCommand(LimelightSubsystem.get(), DriveSubsystem.get()).withTimeout(8),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 12), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4200)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
                
        );
    }

    public static Command getTwoBallStarting2Blue(){//2
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new TurnCommand( -32).withTimeout(1.5),
            new ParallelCommandGroup(new DriveDistanceCommand(40), getAutoLoadCommand()),
            new TurnCommand( 20).withTimeout(1.5), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.1), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4000)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
                
        );
    }

    public static Command getFourToTwoAutoCommand(){//4,2
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new TurnCommand( 32).withTimeout(1.5),
            new ParallelCommandGroup(new DriveDistanceCommand(40), getAutoLoadCommand()), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP).withTimeout(1),
            new TurnCommand( -25).withTimeout(1.5),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4250)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( 118).withTimeout(2),
            new ParallelCommandGroup(new DriveDistanceCommand(35.0), getAutoLoadCommand()), 
            new TurnCommand(-65).withTimeout(2),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4000)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
        );
    }
    public static Command getOneToTwoAutoCommand(){//1,2
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new ParallelCommandGroup(new DriveDistanceCommand(45.0), getAutoLoadCommand()), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP).withTimeout(1),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), //TODO tune hood and velocity
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4250)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( -120).withTimeout(2),
            new ParallelCommandGroup(new DriveDistanceCommand(109), getAutoLoadCommand()), 
            new TurnCommand(6),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4250)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
        );
    }
    public static Command getFourToThreeAutoCommand(){//4,3
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new ParallelCommandGroup(new DriveDistanceCommand(45.0), getAutoLoadCommand()), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP).withTimeout(1),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4250)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( 90).withTimeout(2),
            new ParallelCommandGroup(new DriveDistanceCommand(250), getAutoLoadCommand()), 
            new TurnCommand(90),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4250)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
        );
    }
    
    public static Command getTwoToThreeAutoCommandBlue(){//2,3
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new TurnCommand( -32).withTimeout(1.5), //was 1.5
            new ParallelCommandGroup(new DriveDistanceCommand(30.0), getAutoLoadCommand()).withTimeout(4),
            new TurnCommand( 20).withTimeout(1), //was 1.5
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4150)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( -29.5).withTimeout(1),//29 for red
            new ParallelCommandGroup(new DriveDistanceCommand(157), getAutoLoadCommand().withTimeout(4)),
            //getAutoLoadCommand().withTimeout(1),
            //new IntakeCommand(IntakeCommand.Operation.CMD_STOP), 
            //new TurnCommand( -30).withTimeout(1),
            //new DriveDistanceCommand(-157, 0.9),

            new ParallelCommandGroup(
                new DriveDistanceCommand(-150, 0.9),//was -157
                new SequentialCommandGroup(
                    getAutoLoadCommand().withTimeout(1),
                    new IntakeCommand(IntakeCommand.Operation.CMD_STOP)            
                ),
                new ParallelCommandGroup(
                    new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
                    new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4050)).withTimeout(1)
               ),
            new TurnCommand( 29.5).withTimeout(1), //was 28 now moving to -28
            //new ParallelCommandGroup(
            //    new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
            //    new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4150)).withTimeout(1),
            getAutoShootCommandNoWait(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
        );
    }
    public static Command getTwoToThreeAutoCommandRed(){//2,3
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new TurnCommand( -32).withTimeout(1.5), //was 1.5
            new ParallelCommandGroup(new DriveDistanceCommand(30.0), getAutoLoadCommand()).withTimeout(4),
            new TurnCommand( 20).withTimeout(1), //was 1.5
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4150)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( -29).withTimeout(1),//29 for red
            new ParallelCommandGroup(new DriveDistanceCommand(157), getAutoLoadCommand().withTimeout(4)),
            //getAutoLoadCommand().withTimeout(1),
            //new IntakeCommand(IntakeCommand.Operation.CMD_STOP), 
            //new TurnCommand( -30).withTimeout(1),
            //new DriveDistanceCommand(-157, 0.9),

            new ParallelCommandGroup(
                new DriveDistanceCommand(-150, 0.9),//was -157
                new SequentialCommandGroup(
                getAutoLoadCommand().withTimeout(0.5),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)            
                ),
                new ParallelCommandGroup(
                    new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
                    new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4050)).withTimeout(1)
               ),
            new TurnCommand( 24).withTimeout(1), //was 28 now moving to -28
            //new ParallelCommandGroup(
            //    new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
            //    new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4150)).withTimeout(1),
            getAutoShootCommandNoWait(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
        );
    }
    public static Command getTwoToFourAutoCommand(){
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new ParallelCommandGroup(new DriveDistanceCommand(45.0), getAutoLoadCommand()), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP).withTimeout(1),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4250)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( 120).withTimeout(2),
            new ParallelCommandGroup(new DriveDistanceCommand(169), getAutoLoadCommand()), 
            new TurnCommand(120),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4250)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
        );
    }
    public static Command getFourToTwoToThreeAutoCommand(){
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new ParallelCommandGroup(new DriveDistanceCommand(45.0), getAutoLoadCommand()), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP).withTimeout(1),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4250)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( 115).withTimeout(2),
            new ParallelCommandGroup(new DriveDistanceCommand(75.0), getAutoLoadCommand()), 
            new TurnCommand(30),
            new ParallelCommandGroup(new DriveDistanceCommand(167), getAutoLoadCommand()), 
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4250)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
        );
    }
}

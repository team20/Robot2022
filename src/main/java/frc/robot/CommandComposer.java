// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
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

        Command aimCommand = new LimelightTurnCommand(0);
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
        
        Command shootCommand = new SequentialCommandGroup(
            new ParallelCommandGroup(new FlywheelCommand(FlywheelCommand.Operation.CMD_SETTLE,0), 
                                     new HoodCommand(HoodCommand.Operation.CMD_SETTLE,0)).withTimeout(2), 
            new DeferredCommand(IndexerCommandComposer::getShootCommand),
            new DeferredCommand(IndexerCommandComposer::getShootCommand),

            //new FlywheelCommand(FlywheelCommand.Operation.CMD_SETTLE,0),
            new WaitCommand(0.3), 
            new DeferredCommand(IndexerCommandComposer::getShootCommand));

        return new SequentialCommandGroup(shootCommand);
    }
    public static Command getAutoShootCommandNoWait() {
        
        Command shootCommand = new SequentialCommandGroup(
            new DeferredCommand(IndexerCommandComposer::getShootCommand),
            new WaitCommand(0.25), 
            new DeferredCommand(IndexerCommandComposer::getShootCommand),
            new DeferredCommand(IndexerCommandComposer::getShootCommand));

        return new SequentialCommandGroup(shootCommand);
    }
    public static Command getSpitCommand(){
        return new SequentialCommandGroup(new IndexerCommand(IndexerCommand.Operation.CMD_REV_MAN), new IndexerCommand(IndexerCommand.Operation.CMD_WAIT_RTF), new IndexerCommand(IndexerCommand.Operation.CMD_STOP), new IntakeCommand(IntakeCommand.Operation.CMD_RUN_REV));
    }
    public static Command getPresetShootCommand(ShootCommandComposer.Operation shootType) {
       return new ParallelCommandGroup(new DeferredCommand(() -> (ShootCommandComposer.getShootCommand(0, shootType))));
    }

    public static Command getHighClimbCommand() {
        //slide 0 to -85, telescope 0 to 170
        TelescopeHookCommand TelescopeRetract =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        
        SlideHookCommand SlideToTelescope = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);//17.5
        TelescopeHookCommand TelescopePowerDown=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, -.5);
        ParallelRaceGroup SlideToTelescopeGroup=new ParallelRaceGroup(SlideToTelescope, TelescopePowerDown);

        TelescopeHookCommand TelescopeExtend=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        SequentialCommandGroup SlideExtendWithWait=new SequentialCommandGroup(new WaitCommand(.4),new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kIntermediate));//-38
        ParallelCommandGroup ReachForHigh=new ParallelCommandGroup(TelescopeExtend, SlideExtendWithWait);

        SlideHookCommand SlideToTelescopeTouch = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kUntilTelescopeBack);//-57
        SlideHookCommand SlideExtend = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kMaxPosition);//-57
        TelescopeHookCommand TelescopeRetract2 =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromControlledPosition);//25

        ParallelCommandGroup BringAllDown=new ParallelCommandGroup(SlideExtend, TelescopeRetract2);
        return new SequentialCommandGroup( 
        TelescopeRetract, //on mid bar
        SlideToTelescopeGroup, 
        ReachForHigh,  
        SlideToTelescopeTouch,
        BringAllDown);
    }
    public static Command getTraversalClimbCommand() {
        //slide 0 to -85, telescope 0 to 170
        TelescopeHookCommand TelescopeRetract1 =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        
        SlideHookCommand SlideToTelescope = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);//17.5
        TelescopeHookCommand TelescopePowerDown=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, -.5);
        ParallelRaceGroup SlideToTelescopeGroup=new ParallelRaceGroup(SlideToTelescope, TelescopePowerDown);

        // TelescopeHookCommand TelescopeRelease1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromRetractedPosition);//21
        
        SequentialCommandGroup SlideToTelescopeBehindWithWait = new SequentialCommandGroup(new WaitCommand(.4),new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kTelescopeBehindRung)); //-81
        TelescopeHookCommand TelescopeExtend2=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        ParallelCommandGroup LeanBack1=new ParallelCommandGroup(SlideToTelescopeBehindWithWait, TelescopeExtend2);

        SlideHookCommand SlideToTelescopeTouching1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION,SlideHookConstants.kTelescopeTouchingRung);//-60

        TelescopeHookCommand TelescopeEngage1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlled);//136 both on telescope

        TelescopeHookCommand TelescopeControlledRetract1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlledEnd);
        SlideHookCommand SlideControlledExtend1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kControlled);
        ParallelCommandGroup ControlledMove1 = new ParallelCommandGroup(TelescopeControlledRetract1,SlideControlledExtend1);

        TelescopeHookCommand TelescopeRetractPart2=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromControlledPosition);//35, then hooks to 0
        SlideHookCommand SlideToStart1=new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kStartPosition); 
        ParallelCommandGroup SlideToStartGroup=new ParallelCommandGroup(TelescopeRetractPart2, SlideToStart1);
        
        TelescopeHookCommand TelescopeRetract =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
       
        SlideHookCommand SlideToTelescope2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);//17.5
        TelescopeHookCommand TelescopePowerDown2=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, -.5);
        ParallelRaceGroup SlideToTelescopeGroup2=new ParallelRaceGroup(SlideToTelescope2, TelescopePowerDown2);

        TelescopeHookCommand TelescopeExtend=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        SequentialCommandGroup SlideExtendWithWait=new SequentialCommandGroup(new WaitCommand(.4),new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kIntermediate));//-38
        ParallelCommandGroup ReachForHigh=new ParallelCommandGroup(TelescopeExtend, SlideExtendWithWait);

        SlideHookCommand SlideToTelescopeTouch = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kUntilTelescopeBack);//-57
        
        SlideHookCommand SlideExtend = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kMaxPosition);//-57
        TelescopeHookCommand TelescopeRetract2 =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromControlledPosition);//25
        ParallelCommandGroup BringAllDown=new ParallelCommandGroup(SlideExtend, TelescopeRetract2);

        return new SequentialCommandGroup( 
        TelescopeRetract1, //on mid bar
        SlideToTelescopeGroup, 
        LeanBack1, 
        SlideToTelescopeTouching1, 
        TelescopeEngage1,
        ControlledMove1, //climbing to high
        SlideToStartGroup,
        // TelescopeRetract2,
        
        TelescopeRetract, //on mid bar
        SlideToTelescopeGroup2, 
        ReachForHigh,  
        SlideToTelescopeTouch,
        BringAllDown);
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

    public static Command testingTurn(double angle) {
        //return new TurnCommand(DriveSubsystem.get(), angle).withTimeout(1);
        return new TurnCommand(angle).withTimeout(1);

    }

    public static Command testingDistance(double distance) {
        return new DriveDistanceCommand(distance);
    }

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
    public static Command testShots(){
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2600)),//was 3000 //was 4150
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)));
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

    public static Command getTwoToThreeAutoCommandBlue(){ //2,3
        return new SequentialCommandGroup(
            //new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new TurnCommand(-24).withTimeout(1), //was 1.5
            new ParallelCommandGroup(new DriveDistanceCommand(60.0), getAutoLoadCommand()).withTimeout(1),

            new TurnCommand(-15).withTimeout(2), //was 1.5
            //new LimelightTurnCommand(-2),
           new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
             new ParallelCommandGroup(
                 new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 13), 
                 new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2700)),//was 3000 //was 4150
            // //getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( -38).withTimeout(1.5),//29 for red
            new ParallelCommandGroup(new DriveDistanceCommand(161), getAutoLoadCommand().withTimeout(4)),
            new ParallelCommandGroup(
                new DriveDistanceCommand(-161, 0.9),//was -157
                new SequentialCommandGroup(
                    //getAutoLoadCommand().withTimeout(1),
                    new IntakeCommand(IntakeCommand.Operation.CMD_STOP)            
                )//,
            //     // new ParallelCommandGroup(
            //     //     new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
            //     //     new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4050)).withTimeout(1)
            ),
            new ParallelCommandGroup(
               new TurnCommand( -8).withTimeout(1), //was 28 now moving to -28
               new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11), 
               new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2650)).withTimeout(1),
            //new WaitCommand(0.5),
            //getAutoShootCommand(),
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
    public static Command getTwoToThreeAutoCommandTest(){ //2,3
        return new SequentialCommandGroup(
            //new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            //new TurnCommand(-24).withTimeout(1), //was 1.5
            new ParallelCommandGroup(new DriveDistanceCommand(60.0), getAutoLoadCommand()).withTimeout(2),

            //new TurnCommand(-15).withTimeout(2), //was 1.5
            new LimelightTurnCommand(-2),
           new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
             new ParallelCommandGroup(
                 new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 13), 
                 new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2700)),//was 3000 //was 4150
            getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( -15).withTimeout(1.5),//29 for red
            //new WaitCommand(8),
            new ParallelCommandGroup(new DriveDistanceCommand(164), getAutoLoadCommand().withTimeout(4)),
            new ParallelCommandGroup(
                new DriveDistanceCommand(-164, 0.9),//was -157
                 new SequentialCommandGroup(
                     getAutoLoadCommand().withTimeout(3.5))          
            ),
            // //     // new ParallelCommandGroup(
            // //     //     new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
            // //     //     new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4050)).withTimeout(1)
            // ),
             new ParallelCommandGroup(
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
                new SequentialCommandGroup(
                new TurnCommand( 10).withTimeout(1),
                new LimelightTurnCommand(-2)
                ), //was 28 now moving to -28
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2650)).withTimeout(1),
            // //new WaitCommand(0.5),
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

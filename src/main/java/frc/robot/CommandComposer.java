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
                                     new HoodCommand(HoodCommand.Operation.CMD_SETTLE,0)).withTimeout(1.25), 
            new DeferredCommand(IndexerCommandComposer::getShootCommand),
            new WaitCommand(0.25),
            new DeferredCommand(IndexerCommandComposer::getShootCommand),

            //new FlywheelCommand(FlywheelCommand.Operation.CMD_SETTLE,0),
            //new WaitCommand(0.3), 
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

    public static Command getHighClimbCommand() {//slide 0 to -87, telescope 0 to 170
        //pull up on mid
        TelescopeHookCommand TelescopeRetract =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        
        //switch from telescope on mid bar to slide on mid bar
        SlideHookCommand SlideToTelescope = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);//17.5
            TelescopeHookCommand TelescopePowerDown=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, -.5);
        ParallelDeadlineGroup SlideToTelescopeGroup=new ParallelDeadlineGroup(SlideToTelescope, SlideToTelescope, TelescopePowerDown);

        ParallelRaceGroup SlowDisengage=new ParallelRaceGroup(new WaitCommand(.5), new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, .1)).withTimeout(1);

        //telescopes extend in front of high lean back
        TelescopeHookCommand TelescopeExtend=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
            SequentialCommandGroup SlideExtendWithWait=new SequentialCommandGroup(new WaitCommand(.4),new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kIntermediate));//-38
        ParallelCommandGroup ReachForHigh=new ParallelCommandGroup(TelescopeExtend, SlideExtendWithWait);
        
        //telescopes all the way up touching high front
        SlideHookCommand SlideToTelescopeTouch = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kUntilTelescopeBack);//-57
        
        //telescope push against  high, telescope pull down
            SlideHookCommand SlideExtend = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kMaxPosition);//-57
            TelescopeHookCommand TelescopeRetract2 =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromControlledPosition);//25
        ParallelCommandGroup BringAllDown=new ParallelCommandGroup(SlideExtend, TelescopeRetract2);
        return new SequentialCommandGroup( 
        TelescopeRetract, //on mid bar
        SlideToTelescopeGroup, 
        SlowDisengage,
        ReachForHigh,  
        // SlideToTelescopeTouch,
        BringAllDown);
    }
    public static Command getTraversalClimbCommand() {
        //slide 0 to -85, telescope 0 to 170

        //pull up on mid
        TelescopeHookCommand TelescopeRetract1 =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        
        //switch from telescope on mid bar to slide on mid bar
            SlideHookCommand SlideToTelescope = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);//17.5
            TelescopeHookCommand TelescopePowerDown=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, -.5);
        ParallelRaceGroup SlideToTelescopeGroup=new ParallelRaceGroup(SlideToTelescope, TelescopePowerDown);
        
        ParallelRaceGroup SlowDisengage=new ParallelRaceGroup(new WaitCommand(.5), new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, .1)).withTimeout(1);

        //extend telescopes behind high bar lean back
            SequentialCommandGroup SlideToTelescopeBehindWithWait = new SequentialCommandGroup(new WaitCommand(.4),new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kTelescopeBehindRung)); //-81
            TelescopeHookCommand TelescopeExtend1=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
            ParallelRaceGroup LeanBack1=new ParallelRaceGroup(SlideToTelescopeBehindWithWait, TelescopeExtend1);

        //extended telescope touch high bar
        SlideHookCommand SlideToTelescopeTouching1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION,SlideHookConstants.kTelescopeTouchingRung);//-55

        //telescope down resting on bar
        TelescopeHookCommand TelescopeEngage1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION,145);// TelescopeHookConstants.kControlled);//136 both on telescope

        //vantine move
            TelescopeHookCommand TelescopeControlledRetract1 = new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlledEnd);
            SlideHookCommand SlideControlledExtend1 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kControlled);
        ParallelCommandGroup ControlledMove1 = new ParallelCommandGroup(TelescopeControlledRetract1,SlideControlledExtend1);

        //telescope partial retract, slide hooks to start
            TelescopeHookCommand TelescopeRetractPart2=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromControlledPosition);
            SequentialCommandGroup SlideToStartWithWait=new SequentialCommandGroup(new WaitCommand(.5), new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kStartPosition)); 
        ParallelCommandGroup SlideToStartGroup=new ParallelCommandGroup(TelescopeRetractPart2, SlideToStartWithWait);
        
        //telescopes all the way up, robot fully on high
        TelescopeHookCommand TelescopeRetract =new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        
        //slide hooks on high
            SlideHookCommand SlideToTelescope2 = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);
            TelescopeHookCommand TelescopePowerDown2=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, -.5);
        ParallelDeadlineGroup SlideToTelescopeGroup2=new ParallelDeadlineGroup(SlideToTelescope2, SlideToTelescope2, TelescopePowerDown2);

        ParallelRaceGroup SlowDisengage2=new ParallelRaceGroup(new WaitCommand(.5), new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, .1)).withTimeout(1);

        //telescopes extend in front traverse lean back
            TelescopeHookCommand TelescopeExtend=new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
            SequentialCommandGroup SlideExtendWithWait=new SequentialCommandGroup(new WaitCommand(.5),new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kIntermediate));
        ParallelCommandGroup ReachForHigh=new ParallelCommandGroup(TelescopeExtend, SlideExtendWithWait);

        //telescopes all the way up touching traverse
        SlideHookCommand SlideToTelescopeTouch = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kUntilTelescopeBack);//TODO: change this higher?
        
        //telescope down onto traverse, slide all the way out for vantine
            SlideHookCommand SlideExtend = new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kMaxPosition);
            SequentialCommandGroup TelescopeRetract2 =new SequentialCommandGroup(new WaitCommand(.25),new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromControlledPosition));
        ParallelCommandGroup BringAllDown=new ParallelCommandGroup(SlideExtend, TelescopeRetract2);

        return new SequentialCommandGroup( 
        TelescopeRetract1, //on mid bar
        SlideToTelescopeGroup, 
        SlowDisengage,
        LeanBack1, 
        SlideToTelescopeTouching1, 
        TelescopeEngage1,
        // ControlledMove1, //climbing to high
        SlideToStartGroup,
        // TelescopeRetract2,
        
        TelescopeRetract, //on mid bar
        SlideToTelescopeGroup2, 
        SlowDisengage2,
        ReachForHigh,  
        // SlideToTelescopeTouch,
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

    public static Command getShootType() {
        if (LimelightSubsystem.get().isTargetVisible()) {
            return getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION);
        } else {
            return new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 9.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2525)              
            );
        }
    }

    public static Command getShootTypeSTUY() {
        if (LimelightSubsystem.get().isTargetVisible()) {
            return getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION);
        } else {
            return new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 12), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4200)              
            );
        }
    }
    public static Command getTurnType() {
        if (LimelightSubsystem.get().getXAngle() < 0 && LimelightSubsystem.get().getXAngle() > -2) {
            return new LimelightOnCommand(); //TODO another filler command?
        } else {
            return new LimelightTurnCommand(-2);
        }        
    }

    //the relevant autos

    public static Command getFourBallLimelight(){ //four ball auto with limelight aiming
        return new SequentialCommandGroup(
            new LimelightOnCommand(),
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            //new TurnCommand(13.3).withTimeout(1), //was 1.5
            new ParallelCommandGroup(new DriveDistanceCommand(60.0), getAutoLoadCommand()).withTimeout(2),

            new TurnCommand(13.3).withTimeout(2), //turn first
            new DeferredCommand(CommandComposer::getTurnType), //decide whether to use the limelight to correct the turn, if it's aligned or off it won't be run
            
            //new LimelightTurnCommand(-2).withTimeout(1.5), //then correct with limelight - this is what was being used NYC 4/9
            
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            // new ParallelCommandGroup(
            //      new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 13), 
            //      new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2700)),//was 3000 //was 4150
            
            // getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION), - this is what was being used NYC 4/9
            new DeferredCommand(CommandComposer::getShootType),

            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand(-11.5).withTimeout(1.5),//red got it at -15, blue at -13.5
            //new WaitCommand(8),
            //new ParallelCommandGroup(, ),
            new ParallelCommandGroup(
                new SequentialCommandGroup(
                    new DriveDistanceCommand(164),
                    new TurnCommand(-8).withTimeout(.75),
                    new TurnCommand(-11.5).withTimeout(1.5),
                    new DriveDistanceCommand(-164, 0.9)

                ),//was -157
                new SequentialCommandGroup(
                    getAutoLoadCommand(),
                    getAutoLoadCommand()).withTimeout(6.5)          
            ),
            // //     // new ParallelCommandGroup(
            // //     //     new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
            // //     //     new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4050)).withTimeout(1)
            // ),
             new ParallelCommandGroup(
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
                new SequentialCommandGroup(
                //new TurnCommand(12.08).withTimeout(1)
                new TurnCommand(10).withTimeout(1),
                
                //new LimelightTurnCommand(-2).withTimeout(1.5) - this is what was being used NYC 4/9

                new DeferredCommand(CommandComposer::getTurnType)

                )),
            new SequentialCommandGroup( //was 28 now moving to -28

                //getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION), //this is what was being used NYC 4/9
                new DeferredCommand(CommandComposer::getTurnType),
                
                getAutoShootCommandNoWait()), //add in

                // new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11), //take out?
                // new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2650)).withTimeout(1), //take out?
            // //new WaitCommand(0.5),

            //getAutoShootCommandNoWait(), //take out??
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))//,
            //new LimelightOffCommand()
        );
    }

    public static Command getFourBallNavx(){ //four ball auto using the navx
        return new SequentialCommandGroup(
            new LimelightOnCommand(),
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            //new TurnCommand(13.3).withTimeout(1), //was 1.5
            new ParallelCommandGroup(new DriveDistanceCommand(60.0), getAutoLoadCommand()).withTimeout(2),

            new TurnCommand(13.3).withTimeout(2), //was 1.5
            //new LimelightTurnCommand(-2),
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new ParallelCommandGroup(
                 new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 10), //13
                 new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2550)),//was 2700 //was 3000 //was 4150
            //getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION), //TODO might need to go back in
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand( -15).withTimeout(1.5),//29 for red
            //new WaitCommand(8),
            new ParallelCommandGroup(new DriveDistanceCommand(164), getAutoLoadCommand().withTimeout(2.5)),
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
                new TurnCommand(12.08).withTimeout(1)
                // new TurnCommand(10).withTimeout(1),
                // new LimelightTurnCommand(-2)
                ), //was 28 now moving to -28
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 10), //11
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2550)).withTimeout(1), //2650
            // //new WaitCommand(0.5),
            getAutoShootCommandNoWait(),
             new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
        );
    }

    public static Command getFiveBallNYC() {
        return new SequentialCommandGroup(
            new LimelightOnCommand(),
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            //new TurnCommand(13.3).withTimeout(1), //was 1.5
            new ParallelCommandGroup(new DriveDistanceCommand(60.0), getAutoLoadCommand()).withTimeout(2),

            //new TurnCommand(13.3).withTimeout(2), //was 1.5
            // new LimelightTurnCommand(-2), //was running on 4/9
            new TurnCommand(13.3).withTimeout(2), //turn first
            new DeferredCommand(CommandComposer::getTurnType), //decide whether to use the limelight to correct the turn, if it's aligned or off it won't be run
           
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            // new ParallelCommandGroup(
            //      new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 13), 
            //      new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2700)),//was 3000 //was 4150
            
            //getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION), //was running on 4/9
            new DeferredCommand(CommandComposer::getShootType),

            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new TurnCommand(-10).withTimeout(1.5),//red got it at -15, blue at -13.5
            //new WaitCommand(8),
            new ParallelCommandGroup(new DriveDistanceCommand(164), getAutoLoadCommand().withTimeout(3)),
            new ParallelCommandGroup(
                new SequentialCommandGroup(
                    new TurnCommand(-20).withTimeout(1.5), //was -22
                    new DriveDistanceCommand(-250, 0.9)            
                ),//was -157
                 new SequentialCommandGroup(
                     getAutoLoadCommand().withTimeout(3.5))          
            ),
            // //     // new ParallelCommandGroup(
            // //     //     new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11.5), 
            // //     //     new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4050)).withTimeout(1)
            // )
             new ParallelCommandGroup(
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
                new SequentialCommandGroup(
                //new TurnCommand(12.08).withTimeout(1)
                new TurnCommand(66).withTimeout(1), //going to stop the turn after one second?
                //new LimelightTurnCommand(-2) //was running on 4/9
                new DeferredCommand(CommandComposer::getTurnType) //decide whether to use the limelight to correct the turn, if it's aligned or off it won't be run
           
                ), //was 28 now moving to -28
                // new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 11), 
                // new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2650)
                //getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION) //was running on 4/9
                new DeferredCommand(CommandComposer::getShootType)

                ).withTimeout(2),
            // //new WaitCommand(0.5),
            getAutoShootCommandNoWait(),
            new TurnCommand(66).withTimeout(.75), //was 1 second
            // new ParallelCommandGroup(
            //     new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
            //     new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
            //     new IntakeCommand(IntakeCommand.Operation.CMD_STOP))//,
            new ParallelCommandGroup(
                new DriveDistanceCommand(24, 0.9),//was 8
                 new SequentialCommandGroup(
                     getAutoLoadCommand().withTimeout(1))          
            ),
            new DeferredCommand(CommandComposer::getShootType),

             getAutoShootCommandNoWait(),
             new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
            //new LimelightOffCommand()
        );
    }

    //TODO add timeouts
    public static Command getTwoBallFarRight(){ //2 ball starting in furthest right position
        return new SequentialCommandGroup(
            new LimelightOnCommand(),
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            //new TurnCommand( -32).withTimeout(1.5),
            new ParallelCommandGroup(new DriveDistanceCommand(40), getAutoLoadCommand().withTimeout(2)),
            //new TurnCommand( 20).withTimeout(1.5), 
            new LimelightTurnCommand(-2),
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 10.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2600)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_UP),
            new WaitCommand(.25),
            new DriveDistanceCommand(12),
            new DriveDistanceCommand(-12),
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN)//,
            //new LimelightOffCommand()  
        );
    }

    public static Command getTwoBallHangar(){ //2 ball starting near the hangar
        return new SequentialCommandGroup(
            new LimelightOnCommand(),
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            //new TurnCommand( -32).withTimeout(1.5),
            new ParallelCommandGroup(new DriveDistanceCommand(55), getAutoLoadCommand()),
            //new TurnCommand(-3.63).withTimeout(1.5), 
            new LimelightTurnCommand(-2),
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 10.5), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2600)), //was 2600
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP)),
            new DriveDistanceCommand(9)//,
            //new LimelightOffCommand()    
        );
    }

    public static Command getTwoBallMiddlePosition(){ //two ball starting in middle position
        return new SequentialCommandGroup(
            new LimelightOnCommand(),
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            //new TurnCommand(-24).withTimeout(1), //was 1.5
            new ParallelCommandGroup(new DriveDistanceCommand(60.0), getAutoLoadCommand()).withTimeout(1),

            //new TurnCommand(-15).withTimeout(2), //was 1.5
            new LimelightTurnCommand(-2),
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new ParallelCommandGroup(
                 new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 13), 
                 new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2700)),//was 3000 //was 4150
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))//,
            //new LimelightOffCommand()    
        );
    }

    public static Command getFiveBall(){ //5 ball auto
        return new SequentialCommandGroup(
            new LimelightOnCommand(),
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new ParallelCommandGroup(new DriveDistanceCommand(37.0), getAutoLoadCommand()),
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)
                ),
            new TurnCommand(-121.68),
            new ParallelCommandGroup(new DriveDistanceCommand(172), getAutoLoadCommand()),
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new TurnCommand(-78),
            getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)
        ),           new ParallelCommandGroup(new DriveDistanceCommand(91), getAutoLoadCommand()),
           new ParallelCommandGroup(new DriveDistanceCommand(-118), getAutoLoadCommand()),
           getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION),
           getAutoShootCommand(),
           new ParallelCommandGroup(
               new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
               new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)
        )//,
        //new LimelightOffCommand()
        );
    }

    //irrelevant autos

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
    public static Command getStuyPulseAuto(){//go back straight and shoot
        return new SequentialCommandGroup(
            new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN),
            new ParallelCommandGroup(new WaitCommand(5), getAutoLoadCommand().withTimeout(5)),
            // new waitCommand(1000),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 2300)),//THESE ARE FENDER HIGH PRESETS
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)//,
                // new IntakeCommand(IntakeCommand.Operation.CMD_STOP)
                ),
            new ParallelCommandGroup(new DriveDistanceCommand(108), getAutoLoadCommand()).withTimeout(5), 
            new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
            new DeferredCommand(CommandComposer::getShootTypeSTUY),

            //new ParallelCommandGroup(
                // new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 12), 
                // new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 4200)),
            getAutoShootCommand(),
            new ParallelCommandGroup(
                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), 
                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
                new IntakeCommand(IntakeCommand.Operation.CMD_STOP))
        );
    }

}

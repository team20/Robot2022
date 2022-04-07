package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.Constants.ControllerConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.LoggingConstants;
import frc.robot.Constants.SlideHookConstants;
import frc.robot.Constants.TelescopeHookConstants;
import frc.robot.Constants.ControllerConstants.Axis;
import frc.robot.Constants.ControllerConstants.Button;
import frc.robot.Constants.ControllerConstants.DPad;
import frc.robot.commands.*;
import frc.robot.commands.AutoCommands.ComplexAutoSequence;
import frc.robot.commands.AutoCommands.*;
import frc.robot.commands.ClimberCommands.SlideHookCommand;
import frc.robot.commands.ClimberCommands.TelescopeHookCommand;
import frc.robot.commands.DriveCommands.ArcadeDriveCommand;
import frc.robot.commands.IndexerCommands.IndexerCommand;
import frc.robot.commands.IntakeCommands.*;
import frc.robot.commands.LimelightCommands.LimelightOffCommand;
import frc.robot.commands.LimelightCommands.LimelightTurnCommand;
import frc.robot.commands.ShooterCommands.*;
import frc.robot.subsystems.*;

/**
 * This class is where the bulk of the robot should be declared. Since
 * Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in
 * the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of
 * the robot (including
 * subsystems, commands, and button mappings) should be declared here.
 */

public class RobotContainer {

        // subsystems
        private final ArduinoSubsystem m_arduinoSubsystem = new ArduinoSubsystem();
        private final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
        private final FlywheelSubsystem m_flywheelSubsystem = new FlywheelSubsystem();
        private final HoodSubsystem m_hoodSubsystem = new HoodSubsystem();
        private final IndexerSubsystem m_indexerSubsystem = new IndexerSubsystem();
        private final IntakeArmSubsystem m_intakeArmSubsystem = new IntakeArmSubsystem();
        private final IntakeSubsystem m_intakeSubsystem = new IntakeSubsystem();
        private final LimelightSubsystem m_limelightSubsystem = new LimelightSubsystem();
        private final SlideHookSubsystem m_slideHookSubsystem = new SlideHookSubsystem();
        private final TelescopeHookSubsystem m_telescopeHookSubsystem = new TelescopeHookSubsystem();

        // controllers
        private final Joystick m_driverController = new Joystick(ControllerConstants.kDriverControllerPort);
        private final Joystick m_operatorController = new Joystick(ControllerConstants.kOperatorControllerPort);

        // auto selector
        private final SendableChooser<Command> m_autoChooser = new SendableChooser<>();

        // shuffleboard logging (driver visuals) TODO actually configure this to be
        // useful
        private final ShuffleboardLogging[] m_subsystems = { m_driveSubsystem,
                        m_flywheelSubsystem, m_hoodSubsystem, m_limelightSubsystem };

        /**
         * The container for the robot. Contains subsystems, OI devices, and commands.
         */
        public RobotContainer() {

                // m_limelightSubsystem.turnOffLight();
                m_limelightSubsystem.turnOnLight();
                // CameraServer.getInstance().startAutomaticCapture();
                configureShuffleboard();
                CommandScheduler.getInstance().unregisterSubsystem(m_arduinoSubsystem);
                m_autoChooser.addOption("5 Ball Straight", CommandComposer.getFiveBall());
                // m_autoChooser.addOption("Test turn", new TurnCommand(30));
                // m_autoChooser.addOption("Test shots", CommandComposer.testShots());
                // m_autoChooser.addOption("Test drive", new DriveDistanceCommand(157));
                // m_autoChooser.addOption("Two Ball Straight", CommandComposer.getTwoBallStraight());
                // m_autoChooser.addOption("Two Ball 4 Red", CommandComposer.getTwoBallStarting4Red());
                // m_autoChooser.addOption("Two Ball 4 Blue", CommandComposer.getTwoBallStarting4Blue()); // using this one
                //                                                                                        // 3/14
                // m_autoChooser.addOption("Two Ball 2 Blue", CommandComposer.getTwoBallStarting2Blue()); // good **start
                //                                                                                        // with this one
                // m_autoChooser.addOption("Four To Two", CommandComposer.getFourToTwoAutoCommand());
                // m_autoChooser.addOption("One To Two", CommandComposer.getOneToTwoAutoCommand());
                // m_autoChooser.addOption("Four To Three", CommandComposer.getFourToThreeAutoCommand());
                // m_autoChooser.addOption("Two To Three Red", CommandComposer.getTwoToThreeAutoCommandRed());
                // m_autoChooser.addOption("Two To Three Test", CommandComposer.getTwoToThreeAutoCommandTest());
                // m_autoChooser.addOption("Two To Four", CommandComposer.getTwoToFourAutoCommand());
                // m_autoChooser.addOption("Four To Two To Three", CommandComposer.getFourToTwoToThreeAutoCommand()); // m_autoChooser.addOption("Taxi"
                                                                                                                   // ,
                                                                                                                   // new
                                                                                                                   // Taxi(m_driveSubsystem));
                // m_autoChooser.addOption("Taxi, shoot high");
                // m_autoChooser.addOption("Shoot high, taxi");
                // m_autoChooser.addOption("Shoot low, taxi");
                // m_autoChooser.addOption("Taxi, pick-up, shoot high (2x)");
                // m_autoChooser.addOption("Shoot high, taxi, pick-up, shoot high");

                // TODO do we need shots from all possible positions? red and blue? do get fms
                // info for the colors? choice in code not shuffleboard??
                // DriverStation.Alliance getAlliance() will return Red or Blue (Alliance.Red or
                // Alliance.Blue)

                // taxi, taxi then shoot, shoot then taxi (both height considerations)
                // shoot taxi pick up ball and shoot
                // shoot both with taxiing

                SmartDashboard.putData(m_autoChooser);

                configureButtonBindings();
        }

        private void configureButtonBindings() {

                // *******************************************
                // **************DRIVER CONTROLS**************
                // *******************************************

                // ---------------X BUTTON--------------
                // ---------------High Climb-------------
                new JoystickButton(m_driverController, ControllerConstants.Button.kX)
                                .whenHeld(CommandComposer.getHighClimbCommand());

                new JoystickButton(m_driverController, ControllerConstants.Button.kX)
                                .whenReleased(new ParallelCommandGroup(
                                        new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_STOP, 0).withTimeout(.1), 
                                        new SlideHookCommand(SlideHookCommand.Operation.CMD_STOP, 0).withTimeout(.1)));

                // --------------TRIANGLE BUTTON--------------
                // -------------Indexer Manual Forwards--------
                new JoystickButton(m_driverController, ControllerConstants.Button.kTriangle)
                                .whenHeld(new IndexerCommand(IndexerCommand.Operation.CMD_FWD_MAN));

                new JoystickButton(m_driverController, ControllerConstants.Button.kTriangle)
                                .whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

                // --------------SQUARE BUTTON--------------
                new JoystickButton(m_driverController, ControllerConstants.Button.kSquare)
                                .whenHeld(CommandComposer.getTraversalClimbCommand());

                new JoystickButton(m_driverController, ControllerConstants.Button.kSquare)
                                .whenReleased(new ParallelRaceGroup(
                                        new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_STOP, 0).withTimeout(.1), 
                                        new SlideHookCommand(SlideHookCommand.Operation.CMD_STOP, 0).withTimeout(.1)));
                // --------------CIRCLE BUTTON--------------
                new JoystickButton(m_driverController, ControllerConstants.Button.kCircle)
                        .whenPressed(new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_ZERO_ENCODERS, 0));

                // -----------------UP DPAD-----------------
                // ------Telescope To Top Position OR manual--------

                // new POVButton(m_driverController, DPad.kUp).or(new POVButton(m_driverController, 45))
                //                 .or(new POVButton(m_driverController, 315))
                //                 .whileActiveOnce(new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION,
                //                                 TelescopeHookConstants.kExtendedPosition));
                // new POVButton(m_driverController, DPad.kUp).or(new POVButton(m_driverController, 45))
                //                 .or(new POVButton(m_driverController, 315))
                //                 .whenInactive(new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, 0));

                new POVButton(m_driverController, DPad.kUp).or(new POVButton(m_driverController, 45)).or(new POVButton(m_driverController, 315))
                        .whileActiveOnce(new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kMaxPosition));
                new POVButton(m_driverController, DPad.kUp).or(new POVButton(m_driverController, 45)).or(new POVButton(m_driverController, 315))
                        .whenInactive(new SlideHookCommand(SlideHookCommand.Operation.CMD_STOP, 0));
                // -----------------RIGHT DPAD-----------------
                // ---------Move Telescope Manual--------
                new POVButton(m_driverController, DPad.kRight)
                                .whenHeld(new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, -.2));

                // -----------------DOWN DPAD-----------------
                // ---------Telescope To Bottom Position OR manual----------
                // new POVButton(m_driverController, DPad.kDown).or(new POVButton(m_driverController, 135))
                //                 .or(new POVButton(m_driverController, 225)).whileActiveOnce(
                //                                 new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_POSITION,
                //                                                 TelescopeHookConstants.kRetractedPosition));

                // new POVButton(m_driverController, DPad.kDown).or(new POVButton(m_driverController, 135))
                //                 .or(new POVButton(m_driverController, 225)).whenInactive(
                //                                 new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, 0));

                new POVButton(m_driverController, DPad.kDown).or(new POVButton(m_driverController, 135))
                                .or(new POVButton(m_driverController, 225)).whileActiveOnce(
                                                new SlideHookCommand(SlideHookCommand.Operation.CMD_POSITION,
                                                                SlideHookConstants.kStartPosition));

                new POVButton(m_driverController, DPad.kDown).or(new POVButton(m_driverController, 135))
                                .or(new POVButton(m_driverController, 225)).whenInactive(
                                                new SlideHookCommand(SlideHookCommand.Operation.CMD_STOP, 0));

                // -----------------LEFT DPAD-----------------
                // --------Slide Hook Manual Move-------------
                new POVButton(m_driverController, DPad.kLeft)
                         .whenHeld(new SlideHookCommand(SlideHookCommand.Operation.CMD_MOVE, 0.5));

                // ---------------LEFT BUMPER---------------
                // -------------Fine Steer Left------------

                new JoystickButton(m_driverController, Button.kLeftBumper)
                        .whileActiveOnce(new SequentialCommandGroup(new LimelightTurnCommand(-2), CommandComposer
                        .getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION)));
                                 
                new JoystickButton(m_driverController, Button.kLeftBumper)
                        .whenInactive(new ParallelCommandGroup(
                                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
                                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)
                                ));
                

                // new JoystickButton(m_driverController, Button.kLeftBumper)
                //                 .whenHeld(new ArcadeDriveCommand(m_driveSubsystem,
                //                                 () -> 0.0, () -> DriveConstants.kFineTurningSpeed,
                //                                 () -> -DriveConstants.kFineTurningSpeed));
                
                // ---------------RIGHT BUMPER---------------
                // -------------Fine Steer Right------------
                new JoystickButton(m_driverController, Button.kRightBumper)
                                .whenHeld(new ArcadeDriveCommand(m_driveSubsystem,
                                                () -> 0.0, () -> -DriveConstants.kFineTurningSpeed,
                                                () -> DriveConstants.kFineTurningSpeed));

                // ---------------LEFT AXIS JOYSTICK---------------

                // -------------------LEFT TRIGGER------------------

                // ---------------RIGHT TRIGGER---------------
                // ----------------Arcade Drive----------------
                m_driveSubsystem.setDefaultCommand(
                                new ArcadeDriveCommand(m_driveSubsystem,
                                                () -> -m_driverController.getRawAxis(Axis.kLeftY),
                                                () -> m_driverController.getRawAxis(Axis.kLeftTrigger),
                                                () -> m_driverController.getRawAxis(Axis.kRightTrigger)));

                // ---------------RIGHT AXIS JOYSTICK---------------
                // slide hook manual position
                // m_slideHookSubsystem.setDefaultCommand(
                //                 new SlideHookCommand(SlideHookCommand.Operation.CMD_JOYSTICK_POSITION,
                //                                 () -> m_driverController.getRawAxis(Axis.kRightY)));

                m_telescopeHookSubsystem.setDefaultCommand(
                                new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_JOYSTICK_POSITION,
                                                () -> m_driverController.getRawAxis(Axis.kRightY)));
                // ---------------LEFT BUTTON JOYSTICK---------------

                // ---------------RIGHT BUTTON JOYSTICK---------------

                // ------------------SHARE BUTTON--------------------
                new JoystickButton(m_driverController, ControllerConstants.Button.kShare)
                                .whenPressed(new ParallelCommandGroup(
                                                new TelescopeHookCommand(
                                                                TelescopeHookCommand.Operation.CMD_ZERO_ENCODERS, 0),
                                                new SlideHookCommand(SlideHookCommand.Operation.CMD_ZERO_ENCODERS, 0)));

                // ------------------OPTIONS BUTTON--------------------

                // ------------------TRACKPAD BUTTON--------------------

                // *******************************************
                // ************OPERATOR CONTROLS**************
                // *******************************************

                // ---------------X BUTTON--------------
                // --------Ramp up for fender low shot---------
                new JoystickButton(m_operatorController, ControllerConstants.Button.kX)
                                .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                                                .negate())
                                .whileActiveOnce(CommandComposer.getPresetShootCommand(
                                                ShootCommandComposer.Operation.PRESET_FENDER_LOW));
                new JoystickButton(m_operatorController, ControllerConstants.Button.kX)
                                .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                                                .negate())
                                .whenInactive(new ParallelCommandGroup(
                                                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
                                                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)));

                // --------------TRIANGLE BUTTON--------------
                // --------Ramp up for safe shot---------
             
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kTriangle)
                // .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                //                 .negate())
                // .whileActiveOnce(CommandComposer.getPresetShootCommand(
                //                 ShootCommandComposer.Operation.PRESET_SAFE));
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kTriangle)
                // .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                //                 .negate())
                // .whenInactive(new ParallelCommandGroup(
                //                 new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
                //                 new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)));


                //UNCOMMENT THIS TO HAVE A SAFE POSITION TO SHOOT FROM - you added this on 4/1/22
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kTriangle)
                //                 .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                //                                 .negate())
                //                 .whileActiveOnce(CommandComposer.getPresetShootCommand(
                //                                 ShootCommandComposer.Operation.PRESET_SAFE));
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kTriangle)
                //                 .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                //                                 .negate())
                //                 .whenInactive(new ParallelCommandGroup(
                //                                 new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
                //                                 new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)));

                // --------------SQUARE BUTTON--------------

                // new JoystickButton(m_operatorController, ControllerConstants.Button.kSquare)
                // .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                //                 .negate())
                // .whileActiveOnce(CommandComposer.getPresetShootCommand(
                //                 ShootCommandComposer.Operation.PRESET_SAFE));
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kSquare)
                // .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                //                 .negate())
                // .whenInactive(new ParallelCommandGroup(
                //                 new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
                //                 new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)));
                       
                
                // --------Ramp up for limelight shot--------- //experimental: min ramp up being tested on 4/1/22
              
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kSquare)
                // .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                //                 .negate())
                // .whileActiveOnce(new SequentialCommandGroup(new LimelightTurnCommand(-2), CommandComposer
                //                 .getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION)));
                 
                //uncomment this 4/3/22
                new JoystickButton(m_operatorController, ControllerConstants.Button.kSquare)
                                .and(new JoystickButton(m_driverController, ControllerConstants.Button.kLeftBumper)
                                                .negate())
                                .whileActiveOnce(CommandComposer.getPresetShootCommand(
                                                ShootCommandComposer.Operation.MIN_RAMP_UP));

                new JoystickButton(m_operatorController, ControllerConstants.Button.kSquare)
                                .or(new JoystickButton(m_driverController, ControllerConstants.Button.kLeftBumper))
                                .whenInactive(new ParallelCommandGroup(
                                                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
                                                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)));
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kSquare)
                //                 .and(new JoystickButton(m_driverController, ControllerConstants.Button.kLeftBumper))
                //                 .whenActive(new LimelightOffCommand());
                //                         /*new ParallelCommandGroup(
                                        //         new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
                                        //     new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)*///)));
                                              
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kSquare)
                //                 .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                //                                 .negate())
                //                 .whileActiveOnce(CommandComposer
                //                                 .getPresetShootCommand(ShootCommandComposer.Operation.LIMELIGHT_REGRESSION));
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kSquare)
                //                 .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                //                                 .negate())
                //                 .whenInactive(new ParallelCommandGroup(
                //                                 new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
                //                                 new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)));

                // --------------CIRCLE BUTTON--------------
                // -------Ramp up for fender high shot-------
                new JoystickButton(m_operatorController, ControllerConstants.Button.kCircle)
                                .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                                                .negate())
                                .whileActiveOnce(CommandComposer.getPresetShootCommand(
                                                ShootCommandComposer.Operation.PRESET_FENDER_HIGH));
                // new JoystickButton(m_operatorController, ControllerConstants.Button.kCircle)
                // .and(new JoystickButton(m_operatorController,
                // ControllerConstants.Button.kLeftBumper)
                // .negate())
                // .whileActiveOnce(CommandComposer.getPresetShootCommand(
                // ShootCommandComposer.Operation.LIMELIGHT_LINEAR));
                new JoystickButton(m_operatorController, ControllerConstants.Button.kCircle)
                                .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                                                .negate())
                                .whenInactive(new ParallelCommandGroup(
                                                new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
                                                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)));

                // -----------------UP DPAD-----------------
                // -----------Bring the intake arm up-------
                new POVButton(m_operatorController, DPad.kUp)
                                .whenPressed(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_UP));

                // -----------------RIGHT DPAD-----------------
                // -------Bring the hood to mechanical zero----
                new POVButton(m_operatorController, DPad.kRight)
                                .whenHeld(new HoodCommand(HoodCommand.Operation.CMD_POWER_ZERO, 0));
                new POVButton(m_operatorController, DPad.kRight)
                                .whenReleased(new HoodCommand(HoodCommand.Operation.CMD_STOP, 0));

                // -----------------DOWN DPAD-----------------
                // --------Bring the intake arm down----------
                new POVButton(m_operatorController, DPad.kDown)
                                .whenPressed(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN));

                // -----------------LEFT DPAD-----------------
                // Manual index
                new POVButton(m_operatorController, DPad.kLeft)
                                .whenHeld(new IndexerCommand(IndexerCommand.Operation.CMD_FWD_MAN));

                new POVButton(m_operatorController, DPad.kLeft)
                                .whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

                new JoystickButton(m_operatorController, DPad.kLeft)
                                .whenHeld(new IndexerCommand(IndexerCommand.Operation.CMD_FWD_MAN));

                // ---------------LEFT BUMPER----------------
                // --------------Intake arm manual-------------
                // new JoystickButton(m_operatorController,
                // Constants.ControllerConstants.Button.kLeftBumper)
                // .whenHeld(CommandComposer.getLoadCommand());
                // new JoystickButton(m_operatorController,
                // Constants.ControllerConstants.Button.kLeftBumper)
                // .whenReleased(new IntakeCommand(IntakeCommand.Operation.CMD_STOP));
                // new JoystickButton(m_operatorController,
                // Constants.ControllerConstants.Button.kLeftBumper)
                // .whenHeld(new IndexerCommand(IndexerCommand.Operation.CMD_FWD_MAN));
                // new JoystickButton(m_operatorController,
                // Constants.ControllerConstants.Button.kLeftBumper)
                // .whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));
                new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                                .whenHeld(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_MANUAL));
                new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
                                .whenReleased(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_STOP));

                // ---------------RIGHT BUMPER---------------
                // ---------Run the intake fowards-----------
                // new JoystickButton(m_operatorController,
                // Constants.ControllerConstants.Button.kRightBumper)
                // .whenHeld(CommandComposer.getLoadCommand());
                // new JoystickButton(m_operatorController,
                // Constants.ControllerConstants.Button.kRightBumper)
                // .whenHeld(new IntakeCommand(IntakeCommand.Operation.CMD_RUN_FWD));
                // new JoystickButton(m_operatorController,
                // Constants.ControllerConstants.Button.kRightBumper)
                // .whenReleased(new IntakeCommand(IntakeCommand.Operation.CMD_STOP));
                new JoystickButton(m_operatorController, Constants.ControllerConstants.Button.kRightBumper)
                                .whenHeld(CommandComposer.getLoadCommand());
                new JoystickButton(m_operatorController, Constants.ControllerConstants.Button.kRightBumper)
                                .whenReleased(new IntakeCommand(IntakeCommand.Operation.CMD_STOP));

                // ---------------LEFT AXIS JOYSTICK---------------

                // -------------------LEFT TRIGGER------------------

                // ---------------RIGHT TRIGGER---------------

                // ---------------RIGHT AXIS JOYSTICK---------------

                // ---------------LEFT BUTTON JOYSTICK---------------

                // ---------------RIGHT BUTTON JOYSTICK---------------

                // ------------------SHARE BUTTON--------------------

                new JoystickButton(m_operatorController, ControllerConstants.Button.kShare)
                        .whenHeld(new ParallelCommandGroup(
                                new IndexerCommand(IndexerCommand.Operation.CMD_REV_MAN)
                                ));
                new JoystickButton(m_operatorController, ControllerConstants.Button.kShare)
                        .whenReleased(new ParallelCommandGroup(
                                new IndexerCommand(IndexerCommand.Operation.CMD_STOP)
                        ));
               
                // ------------------OPTIONS BUTTON--------------------
                // ------Run the indexer and intake backwards----------
                new JoystickButton(m_operatorController, ControllerConstants.Button.kOptions)
                                .whenHeld(new ParallelCommandGroup(
                                                new IndexerCommand(IndexerCommand.Operation.CMD_REV_MAN),
                                                new IntakeCommand(IntakeCommand.Operation.CMD_RUN_REV),
                                                new FlywheelCommand(FlywheelCommand.Operation.CMD_REVERSE, 0)));
                new JoystickButton(m_operatorController, ControllerConstants.Button.kOptions)
                                .whenReleased(new ParallelCommandGroup(
                                                new IndexerCommand(IndexerCommand.Operation.CMD_STOP),
                                                new IntakeCommand(IntakeCommand.Operation.CMD_STOP),
                                                new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)));

                // ------------------TRACKPAD BUTTON--------------------

                // ------------------NOT YET FINALIZED------------------
                new JoystickButton(m_operatorController, Constants.ControllerConstants.Axis.kRightTrigger)
                                .whenHeld(CommandComposer.getSpitCommand());

                // new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftStick)
                //                 .whenHeld(new DriveIntakeArmCommand(
                //                                 () -> m_operatorController.getRawAxis(Axis.kLeftY) * 0.5));
                new JoystickButton(m_operatorController, ControllerConstants.Button.kTrackpad)
                                .whenPressed(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_RESET_ENCODER));

        }

        public void configureTestingBindings() {
                new JoystickButton(m_driverController, 1)
                                .whenPressed((new TurnCommand(30)));
                // new JoystickButton(m_driverController, 1)
                // .whenReleased(new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY,
                // 0));

                new POVButton(m_driverController, 0)
                                .whenPressed(new IndexerCommand(IndexerCommand.Operation.CMD_FWD_MAN));
                new POVButton(m_driverController, 0)
                                .whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

                new POVButton(m_driverController, 180)
                                .whenPressed(new IndexerCommand(IndexerCommand.Operation.CMD_REV_MAN));
                new POVButton(m_driverController, 180)
                                .whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

                new POVButton(m_driverController, 90)
                                .whenPressed(new IntakeCommand(IntakeCommand.Operation.CMD_RUN_FWD));
                new POVButton(m_driverController, 90)
                                .whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

                new POVButton(m_driverController, 270)
                                .whenPressed(new IntakeCommand(IntakeCommand.Operation.CMD_RUN_REV));
                new POVButton(m_driverController, 270)
                                .whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

                // new JoystickButton(m_operatorController, 2).whenPressed(new LimelightCommand(
                // // 2 is x
                // m_limelightSubsystem, m_driveSubsystem, 0, 178)); // last input is in units
                // of inches

                // turning to 0 and aligning to the center of circle
                // new JoystickButton(m_operatorController, 3).whenPressed(new LimelightCommand(
                // // 3 is circle
                // m_limelightSubsystem, m_driveSubsystem, 0,
                // m_limelightSubsystem.getDistance())); // last input is in units of
                // // inches

                // new JoystickButton(m_operatorController, Button.kX).whenPressed(new
                // LimelightTurnCommand(
                // m_limelightSubsystem, m_driveSubsystem, m_arduinoSubsystem, 0));

        }

        public void configureShuffleboard() {
                for (int i = 0; i < m_subsystems.length; i++) {
                        if (LoggingConstants.kSubsystems[i]) {
                                m_subsystems[i].configureShuffleboard(true);
                        }
                }
        }

        public Command getAutonomousCommand() {
                // return new SequentialCommandGroup(new ZeroCommand(),
                // m_autoChooser.getSelected());
                return m_autoChooser.getSelected();
        }

        public void generateAutonomousCommands() {
                m_autoChooser.setDefaultOption("Shoot then Taxi",
                                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem,
                                                m_indexerSubsystem, 2));
                m_autoChooser.addOption("Taxi Only",
                                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem,
                                                m_indexerSubsystem, 1));
                m_autoChooser.addOption("Shoot then Taxi",
                                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem,
                                                m_indexerSubsystem, 2));
                m_autoChooser.addOption("Drive to Cargo, Shoot Twice",
                                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem,
                                                m_indexerSubsystem, 3));
                m_autoChooser.addOption("Shoot Lower Two Cargo",
                                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem,
                                                m_indexerSubsystem, 4));

                SmartDashboard.putData(m_autoChooser);

        }
}

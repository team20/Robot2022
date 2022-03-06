package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.Constants.ControllerConstants;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.FieldLocation;
import frc.robot.Constants.LoggingConstants;
import frc.robot.Constants.ArduinoConstants.LEDModes;
import frc.robot.Constants.ArduinoConstants.LEDColors;
import frc.robot.Constants.ControllerConstants.Axis;
import frc.robot.Constants.ControllerConstants.Button;
import frc.robot.Constants.ControllerConstants.DPad;
import frc.robot.commands.DeferredCommand;
import frc.robot.commands.ArduinoCommands.UpdateLEDsCommand;
import frc.robot.commands.AutoCommands.ComplexAutoSequence;
import frc.robot.commands.AutoCommands.SitAndShootHigh;
import frc.robot.commands.AutoCommands.SitAndShootLow;
import frc.robot.commands.ClimberCommands.SlideHookCommand;
import frc.robot.commands.ClimberCommands.TelescopeHookCommand;
import frc.robot.commands.DriveCommands.ArcadeDriveCommand;
import frc.robot.commands.DriveCommands.PixyTargetCommand;
import frc.robot.commands.IndexerCommands.IndexerCommand;
import frc.robot.commands.IntakeArmCommands.DriveArmCommand;
import frc.robot.commands.IntakeArmCommands.ExtendArmCommand;
import frc.robot.commands.IntakeArmCommands.RetractArmCommand;
import frc.robot.commands.IntakeCommands.DriveIntakeArmCommand;
import frc.robot.commands.IntakeCommands.IntakeArmCommand;
import frc.robot.commands.IntakeCommands.IntakeCommand;
import frc.robot.commands.LimelightCommands.LimelightTurnCommand;
import frc.robot.commands.ShooterCommands.AutoIndexCommand;
import frc.robot.commands.ShooterCommands.FlywheelCommand;
import frc.robot.commands.ShooterCommands.HoodCommand;
import frc.robot.commands.ShooterCommands.ShootCommandComposer;
import frc.robot.commands.ShooterCommands.ShootSetupCommand;
import frc.robot.subsystems.ArduinoSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.IndexerSubsystem;
import frc.robot.subsystems.IntakeArmSubsystem;
import frc.robot.subsystems.IntakeSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.SlideHookSubsystem;
import frc.robot.subsystems.TelescopeHookSubsystem;

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

        configureShuffleboard();

        m_autoChooser.addOption("Sit and shoot high",
                new SitAndShootHigh(m_flywheelSubsystem, m_hoodSubsystem, m_indexerSubsystem));
        m_autoChooser.addOption("Sit and shoot low",
                new SitAndShootLow(m_flywheelSubsystem, m_hoodSubsystem, m_indexerSubsystem));
        // m_autoChooser.addOption("Taxi" , new Taxi(m_driveSubsystem));
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

        // Driver

        // x button: pixy ball follow
        new JoystickButton(m_driverController, ControllerConstants.Button.kX)
                .whenHeld(new PixyTargetCommand(m_driveSubsystem, m_arduinoSubsystem, () -> DriveConstants.kPixySpeed));

        // left bumper and triangle button: traversal climb
        // new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
        //         .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kTriangle))
        //         .whenActive(CommandComposer.getTraversalClimbCommand());

        // left bumper and square button: high climb
        // new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
        //         .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kTriangle))
        //         .whenActive(CommandComposer.getHighClimbCommand());

        // Slide hook variable speed (when L bumper held and R joystick pressed)
        // new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
        //         .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftStick))
        //         .whenActive(new SlideHookCommand(SlideHookCommand.Operation.CMD_MOVE,
        //                 m_operatorController.getRawAxis(Axis.kRightY)));

        // Telescope hook variable speed (when L bumper held and L joystick pressed)
        // new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper)
        //         .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftStick))
        //         .whenActive(new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE,
        //                 m_operatorController.getRawAxis(Axis.kLeftY)));

        // bring the intake up
        // new POVButton(m_driverController, DPad.kUp)
        //         .whenPressed(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_UP));

        // new POVButton(m_driverController, DPad.kUp).whenPressed(new
        // RetractArmCommand(m_intakeArmSubsystem));

        // bring the intake down
        // new POVButton(m_driverController, DPad.kDown)
        //         .whenPressed(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN));

        // new POVButton(m_driverController, DPad.kDown).whenPressed(new
        // ExtendArmCommand(m_intakeArmSubsystem));

        // fine steering left
        new JoystickButton(m_driverController, Button.kLeftBumper).whenHeld(new ArcadeDriveCommand(m_driveSubsystem,
                () -> 0.0, () -> -DriveConstants.kFineTurningSpeed, () -> DriveConstants.kFineTurningSpeed));

        // fine steering right
        new JoystickButton(m_driverController, Button.kRightBumper).whenHeld(new ArcadeDriveCommand(m_driveSubsystem,
                () -> 0.0, () -> DriveConstants.kFineTurningSpeed, () -> -DriveConstants.kFineTurningSpeed));

        // arcade drive
        m_driveSubsystem.setDefaultCommand(
                new ArcadeDriveCommand(m_driveSubsystem,
                        () -> -m_driverController.getRawAxis(Axis.kLeftY),
                        () -> m_driverController.getRawAxis(Axis.kLeftTrigger),
                        () -> m_driverController.getRawAxis(Axis.kRightTrigger)));

        //aim and shoot linear limelight                
        new JoystickButton(m_driverController, ControllerConstants.Button.kTriangle)
                .whenHeld(CommandComposer.getAimAndPrepCommand(ShootCommandComposer.Operation.LIMELIGHT_LINEAR));

        //get shoot command
        new JoystickButton(m_driverController, ControllerConstants.Button.kCircle)
                .whenHeld(CommandComposer.getShootCommand());

        //bring arm up driver        
        new POVButton(m_driverController, ControllerConstants.DPad.kUp)
                .whenHeld(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_UP));

        //bring arm down driver        
        new POVButton(m_driverController, ControllerConstants.DPad.kDown)
                .whenHeld(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN));

        // operator

        // manually drive the intake
        // m_intakeSubsystem.setDefaultCommand(
        // new DriveArmCommand(m_intakeArmSubsystem, () ->
        // m_operatorController.getRawAxis(Axis.kLeftY)));

        // Right trigger: spit one ball out
        new JoystickButton(m_operatorController, Constants.ControllerConstants.Axis.kRightTrigger)
                .whenHeld(CommandComposer.getSpitCommand());

        // Left Trigger: intake and index one ball
        new JoystickButton(m_operatorController, Constants.ControllerConstants.Button.kRightBumper)
                .whenHeld(CommandComposer.getLoadCommand());
      
        new JoystickButton(m_operatorController, Constants.ControllerConstants.Button.kRightBumper)
                .whenReleased(new IntakeCommand(IntakeCommand.Operation.CMD_STOP));

        new JoystickButton(m_operatorController, ControllerConstants.Button.kSquare)
                .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper).negate())
                .whenInactive(new ParallelCommandGroup(new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0), new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0)));
        
        new JoystickButton(m_operatorController, ControllerConstants.Button.kTriangle)
                .and(new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftBumper).negate())
                .whileActiveOnce(
                        CommandComposer.getPresetShootCommand(ShootCommandComposer.Operation.PRESET_LAUNCHPAD));

        //shoot from fender high                
        new JoystickButton(m_operatorController, ControllerConstants.Button.kCircle)
                .whenHeld(CommandComposer.getPresetShootCommand(ShootCommandComposer.Operation.PRESET_FENDER_HIGH));
        
        //shoot from fender low        
        new JoystickButton(m_operatorController, ControllerConstants.Button.kX)
                .whenHeld(CommandComposer.getPresetShootCommand(ShootCommandComposer.Operation.PRESET_FENDER_LOW));

        //manually drive the arm 
        new JoystickButton(m_operatorController, ControllerConstants.Button.kLeftStick)
                .whenHeld(new DriveIntakeArmCommand(() -> m_operatorController.getRawAxis(Axis.kLeftY)));
    }

    public void configureTestingBindings() {
        new JoystickButton(m_driverController, 1)
                .whenPressed(new DeferredCommand(CommandComposer::getManualFlywheelCommand));
        new JoystickButton(m_driverController, 1)
                .whenReleased(new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0));

        new POVButton(m_driverController, 0).whenPressed(new IndexerCommand(IndexerCommand.Operation.CMD_FWD_MAN));
        new POVButton(m_driverController, 0).whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

        new POVButton(m_driverController, 180).whenPressed(new IndexerCommand(IndexerCommand.Operation.CMD_REV_MAN));
        new POVButton(m_driverController, 180).whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

        new POVButton(m_driverController, 90).whenPressed(new IntakeCommand(IntakeCommand.Operation.CMD_RUN_FWD));
        new POVButton(m_driverController, 90).whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

        new POVButton(m_driverController, 270).whenPressed(new IntakeCommand(IntakeCommand.Operation.CMD_RUN_REV));
        new POVButton(m_driverController, 270).whenReleased(new IndexerCommand(IndexerCommand.Operation.CMD_STOP));

        new JoystickButton(m_driverController, 3)
                .whenPressed(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_UP));

        new JoystickButton(m_driverController, 2)
                .whenPressed(new IntakeArmCommand(IntakeArmCommand.Operation.CMD_ARM_DOWN));

        new POVButton(m_operatorController, 0)
                .whenPressed(new SlideHookCommand(SlideHookCommand.Operation.CMD_MOVE, 0.3));
        new POVButton(m_operatorController, 0)
                .whenReleased(new SlideHookCommand(SlideHookCommand.Operation.CMD_MOVE, 0));

        new POVButton(m_operatorController, 180)
                .whenPressed(new SlideHookCommand(SlideHookCommand.Operation.CMD_MOVE, -0.3));
        new POVButton(m_operatorController, 180)
                .whenReleased(new SlideHookCommand(SlideHookCommand.Operation.CMD_MOVE, 0));

        new POVButton(m_operatorController, 90)
                .whenPressed(new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, 0.3));
        new POVButton(m_operatorController, 90)
                .whenReleased(new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, 0));

        new POVButton(m_operatorController, 270)
                .whenPressed(new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, -0.3));
        new POVButton(m_operatorController, 270)
                .whenReleased(new TelescopeHookCommand(TelescopeHookCommand.Operation.CMD_MOVE, 0));
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
                m_subsystems[i].configureShuffleboard();
            }
        }
    }

    public Command getAutonomousCommand() {
        return m_autoChooser.getSelected();
    }

    public void generateAutonomousCommands() {
        m_autoChooser.setDefaultOption("Shoot then Taxi",
                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem, m_indexerSubsystem, 2));
        m_autoChooser.addOption("Taxi Only",
                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem, m_indexerSubsystem, 1));
        m_autoChooser.addOption("Shoot then Taxi",
                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem, m_indexerSubsystem, 2));
        m_autoChooser.addOption("Drive to Cargo, Shoot Twice",
                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem, m_indexerSubsystem, 3));
        m_autoChooser.addOption("Shoot Lower Two Cargo",
                new ComplexAutoSequence(m_driveSubsystem, m_flywheelSubsystem, m_hoodSubsystem, m_indexerSubsystem, 4));

        SmartDashboard.putData(m_autoChooser);

    }
}

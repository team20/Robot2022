package frc.robot;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.networktables.NetworkTable;
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
import frc.robot.Constants.ArduinoConstants.MainLEDModes;
import frc.robot.Constants.ArduinoConstants.ShooterLEDModes;
import frc.robot.Constants.ControllerConstants.Axis;
import frc.robot.commands.ArduinoCommands.UpdateLEDsCommand;
import frc.robot.commands.AutoCommands.SitAndShootHigh;
import frc.robot.commands.AutoCommands.SitAndShootLow;
import frc.robot.commands.DriveCommands.ArcadeDriveCommand;
import frc.robot.commands.LimelightCommands.LimelightCommand;
import frc.robot.commands.ShooterCommands.AutoIndexCommand;
import frc.robot.commands.ShooterCommands.ShootSetupCommand;
import frc.robot.subsystems.ArduinoSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.IndexerSubsystem;
import frc.robot.subsystems.LimelightSubsystem;
import frc.robot.subsystems.SlideHookSubsystem;

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
  private final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  private final LimelightSubsystem m_limelightSubsystem = new LimelightSubsystem();
  private final HoodSubsystem m_hoodSubsystem = new HoodSubsystem();
  private final FlywheelSubsystem m_flywheelSubsystem = new FlywheelSubsystem();
  private final IndexerSubsystem m_indexerSubsystem = new IndexerSubsystem();
  private final ArduinoSubsystem m_arduinoSubsystem = new ArduinoSubsystem();
  private final SlideHookSubsystem m_slideHookSubsystem = new SlideHookSubsystem();

  // controllers
  private final Joystick m_driverController = new Joystick(ControllerConstants.kDriverControllerPort);
  private final Joystick m_operatorController = new Joystick(ControllerConstants.kOperatorControllerPort);

  // auto selector
  private final SendableChooser<Command> m_autoChooser = new SendableChooser<>();

  // shuffleboard logging (driver visuals) TODO actually configure this to be
  // useful
  private final ShuffleboardLogging[] m_subsystems = { m_driveSubsystem,
      m_flywheelSubsystem, m_hoodSubsystem,
      m_limelightSubsystem
  };

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
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

    m_arduinoSubsystem.setDefaultCommand(
        new UpdateLEDsCommand(m_arduinoSubsystem, () -> {
          return MainLEDModes.kChasing;
        },
            () -> {
              return 0.0;
            }, () -> {
              return ShooterLEDModes.kOff;
            }, () -> {
              return 0.0;
            }));

    configureButtonBindings();
  }

  private void configureButtonBindings() {
    
    // new JoystickButton(m_driverController, 5).whenHeld(new SlideHookMoveCommand(m_slideHookSubsystem, .1));
    // new JoystickButton(m_driverController, 5).whenHeld(new SlideHookPositionCommand(m_slideHookSubsystem, 1));

    // get distance to target from limelight and then adjust the rpm and angle of
    // the shooter
    new POVButton(m_operatorController, 180).whenHeld(new SequentialCommandGroup(
        new LimelightCommand(m_limelightSubsystem, m_driveSubsystem, 0, m_limelightSubsystem.getDistance()),
        new ParallelCommandGroup(new ShootSetupCommand(
            m_flywheelSubsystem, m_hoodSubsystem, ((m_limelightSubsystem.getDistance() / 12.0) - (8.75 / 12.0)),
            "LINEAR"),
            new AutoIndexCommand(
                m_indexerSubsystem, m_flywheelSubsystem::atSetpoint))));

    m_driveSubsystem.setDefaultCommand(
        new ArcadeDriveCommand(m_arduinoSubsystem, m_driveSubsystem,
            () -> -m_operatorController.getRawAxis(Axis.kLeftY),
            () -> m_operatorController.getRawAxis(Axis.kLeftTrigger),
            () -> m_operatorController.getRawAxis(Axis.kRightTrigger)));

  }

  public void configureTestingBindings() {

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
  }

  public Command getAutonomousCommand() {
    return m_autoChooser.getSelected();
  }

  public void generateAutonomousCommands() { // TODO lots of problems here....
    // // An example trajectory to follow. All units in meters.
    // Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
    // // Start at the origin facing the +X direction
    // new Pose2d(0, 0, new Rotation2d(0)),
    // // Pass through these two interior waypoints, making an 's' curve path
    // List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
    // // End 3 meters straight ahead of where we started, facing forward
    // new Pose2d(3, 0, new Rotation2d(0)),
    // // Pass config
    // config);

    // Hashtable<String, Trajectory> trajectories = new Hashtable<String,
    // Trajectory>();
    // File[] files = new File("\\home\\lvuser\\deploy\\paths\\output").listFiles();
    // for (File file : files)
    // try {
    // trajectories.put(file.getName(), TrajectoryUtil
    // .fromPathweaverJson(Filesystem.getDeployDirectory().toPath().resolve(file.getPath())));
    // } catch (IOException e) {
    // Shuffleboard.getTab("Errors").add("Trajectory Error",
    // e.getStackTrace().toString()).withSize(4, 4)
    // .withPosition(0, 0).withWidget(BuiltInWidgets.kTextView);
    // }
    // for (String name : trajectories.keySet())
    // m_autoChooser.addOption(name, new TrajectoryFollowCommand(m_driveSubsystem,
    // trajectories.get(name)));

    // }

  }

}

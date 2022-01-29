// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

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
import frc.robot.commands.ArcadeDriveCommand;
import frc.robot.commands.AutoFeederCommand;
import frc.robot.commands.FeederCommand;
import frc.robot.commands.LimelightCommand;
import frc.robot.commands.RunCarouselCommand;
import frc.robot.commands.ShootSetupCommand;
import frc.robot.commands.ToOpenSpaceCommand;
import frc.robot.subsystems.CarouselSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.FeederSubsystem;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.LimelightSubsystem;

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
  private final FeederSubsystem m_feederSubsystem = new FeederSubsystem();
  private final HoodSubsystem m_hoodSubsystem = new HoodSubsystem();
  private final FlywheelSubsystem m_flywheelSubsystem = new FlywheelSubsystem();
  private final CarouselSubsystem m_carouselSubsystem = new CarouselSubsystem();

  // controllers
  private final Joystick m_driverController = new Joystick(ControllerConstants.kDriverControllerPort);
  private final Joystick m_operatorController = new Joystick(ControllerConstants.kOperatorControllerPort);

  // auto selector
  private final SendableChooser<Command> m_autoChooser = new SendableChooser<>();

  // shuffleboard logging (driver visuals) TODO actually configure this to be
  // useful
  private final ShuffleboardLogging[] m_subsystems = { m_carouselSubsystem,
      m_feederSubsystem, m_flywheelSubsystem, m_hoodSubsystem,
      m_limelightSubsystem };

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    m_autoChooser.addOption("Just follow path", COMMAND);
    SmartDashboard.putData(m_autoChooser);

    configureButtonBindings();
  }

  private void configureButtonBindings() {

    m_carouselSubsystem.setDefaultCommand(new ToOpenSpaceCommand(m_carouselSubsystem));

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
    // putting it all together
    // new POVButton(m_operatorController, 180).whenHeld(new SequentialCommandGroup(
    // new LimelightCommand(m_limelightSubsystem, m_driveSubsystem, 0,
    // m_limelightSubsystem.getDistance()),
    // new ParallelCommandGroup(new ShootSetupCommand(
    // m_flywheelSubsystem, m_hoodSubsystem, m_limelightSubsystem.getDistance()),
    // new AutoFeederCommand(
    // m_feederSubsystem, m_carouselSubsystem::atOpenSpace,
    // m_flywheelSubsystem::atSetpoint),
    // new RunCarouselCommand(m_carouselSubsystem, 20))));

    // new POVButton(m_operatorController, 180).whenHeld(new
    // SequentialCommandGroup(new LimelightCommand(m_limelightSubsystem,
    // m_driveSubsystem, 0, m_limelightSubsystem.getDistance()),
    // new ParallelCommandGroup(new ShootSetupCommand(
    // m_flywheelSubsystem, m_hoodSubsystem, m_limelightSubsystem.getDistance() /
    // 12.0, "REGRESSION"),
    // new AutoFeederCommand(
    // m_feederSubsystem, m_carouselSubsystem::atOpenSpace,
    // m_flywheelSubsystem::atSetpoint),
    // new RunCarouselCommand(m_carouselSubsystem, 20))));

    new POVButton(m_operatorController, 180).whenHeld(new SequentialCommandGroup(
        new LimelightCommand(m_limelightSubsystem, m_driveSubsystem, 0, m_limelightSubsystem.getDistance()),
        new ParallelCommandGroup(new ShootSetupCommand(
            m_flywheelSubsystem, m_hoodSubsystem, ((m_limelightSubsystem.getDistance() / 12.0) - (8.75 / 12.0)),
            "LINEAR"),
            new AutoFeederCommand(
                m_feederSubsystem, m_carouselSubsystem::atOpenSpace, m_flywheelSubsystem::atSetpoint),
            new RunCarouselCommand(m_carouselSubsystem, 20))));

    // new POVButton(m_operatorController, 180).whenHeld(
    // new ParallelCommandGroup(new ShootSetupCommand(
    // m_flywheelSubsystem, m_hoodSubsystem,
    // ((m_limelightSubsystem.getDistance()/12.0)-(8.75/12.0)), "LINEAR"),
    // new AutoFeederCommand(
    // m_feederSubsystem, m_carouselSubsystem::atOpenSpace,
    // m_flywheelSubsystem::atSetpoint),
    // new RunCarouselCommand(m_carouselSubsystem, 20)));
    // ((m_limelightSubsystem.getDistance()/12.0)-(8.75/12.0))

    // m_driveSubsystem.setDefaultCommand(
    // new ArcadeDriveCommand(m_driveSubsystem, () ->
    // -m_operatorController.getRawAxis(1),
    // () -> m_operatorController.getRawAxis(3), () ->
    // m_operatorController.getRawAxis(4)));

    // new POVButton(m_operatorController, 180).whenHeld(new
    // ParallelCommandGroup(new ShootSetupCommand(
    // m_flywheelSubsystem, m_hoodSubsystem, () -> FieldLocation.WALL),
    // new AutoFeederCommand(m_feederSubsystem, m_carouselSubsystem::atOpenSpace,
    // m_flywheelSubsystem::atSetpoint),
    // new RunCarouselCommand(m_carouselSubsystem, 20)));

    // new POVButton(m_operatorController, 180).whenHeld(new ParallelCommandGroup(
    // new ShootSetupCommand(m_flywheelSubsystem, m_hoodSubsystem, () ->
    // FieldLocation.WALL),
    // new AutoFeederCommand(m_feederSubsystem, m_carouselSubsystem::atOpenSpace,
    // m_flywheelSubsystem::atSetpoint)));

  }

  public Command getAutonomousCommand() {
    return m_autoChooser.getSelected();
  }

  public void generateAutonomousCommands() {
    // An example trajectory to follow. All units in meters.
    Trajectory exampleTrajectory = TrajectoryGenerator.generateTrajectory(
        // Start at the origin facing the +X direction
        new Pose2d(0, 0, new Rotation2d(0)),
        // Pass through these two interior waypoints, making an 's' curve path
        List.of(new Translation2d(1, 1), new Translation2d(2, -1)),
        // End 3 meters straight ahead of where we started, facing forward
        new Pose2d(3, 0, new Rotation2d(0)),
        // Pass config
        config);

    Hashtable<String, Trajectory> trajectories = new Hashtable<String, Trajectory>();
    File[] files = new File("\\home\\lvuser\\deploy\\paths\\output").listFiles();
    for (File file : files)
      try {
        trajectories.put(file.getName(), TrajectoryUtil
            .fromPathweaverJson(Filesystem.getDeployDirectory().toPath().resolve(file.getPath())));
      } catch (IOException e) {
        Shuffleboard.getTab("Errors").add("Trajectory Error", e.getStackTrace().toString()).withSize(4, 4)
            .withPosition(0, 0).withWidget(BuiltInWidgets.kTextView);
      }
    for (String name : trajectories.keySet())
      m_autoChooser.addOption(name, new TrajectoryFollowCommand(m_driveSubsystem, trajectories.get(name)));

  }

}

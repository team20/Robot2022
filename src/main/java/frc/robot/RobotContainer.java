// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
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
  // The robot's subsystems and commands are defined here...
  private final DriveSubsystem m_driveSubsystem = new DriveSubsystem();
  private final LimelightSubsystem m_limelightSubsystem = new LimelightSubsystem();
  private final FeederSubsystem m_feederSubsystem = new FeederSubsystem();
  private final HoodSubsystem m_hoodSubsystem = new HoodSubsystem();
  private final FlywheelSubsystem m_flywheelSubsystem = new FlywheelSubsystem();
  private final CarouselSubsystem m_carouselSubsystem = new CarouselSubsystem();

  private final Joystick m_operatorController = new Joystick(1);

  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer() {
    configureButtonBindings();
  }

  private void configureButtonBindings() {

   // m_carouselSubsystem.setDefaultCommand(new ToOpenSpaceCommand(m_carouselSubsystem));

    // new JoystickButton(m_operatorController, 2).whenPressed(new LimelightCommand( // 2 is x
    //     m_limelightSubsystem, m_driveSubsystem, 0, 178)); // last input is in units of inches

    //turning to 0 and aligning to the center of circle
    // new JoystickButton(m_operatorController, 3).whenPressed(new LimelightCommand( // 3 is circle
    //     m_limelightSubsystem, m_driveSubsystem, 0, m_limelightSubsystem.getDistance())); // last input is in units of
    //                                                                                      // inches
    //putting it all together
    new POVButton(m_operatorController, 180).whenHeld(new SequentialCommandGroup(
        new LimelightCommand(m_limelightSubsystem, m_driveSubsystem, 0,
            m_limelightSubsystem.getDistance()),
        new ParallelCommandGroup(new ShootSetupCommand(
            m_flywheelSubsystem, m_hoodSubsystem, m_limelightSubsystem.getDistance()),
            new AutoFeederCommand(
                m_feederSubsystem, m_carouselSubsystem::atOpenSpace, m_flywheelSubsystem::atSetpoint),
            new RunCarouselCommand(m_carouselSubsystem, 20))));

    // m_driveSubsystem.setDefaultCommand(
    // new ArcadeDriveCommand(m_driveSubsystem, () ->
    // -m_operatorController.getRawAxis(1),
    // () -> m_operatorController.getRawAxis(3), () ->
    // m_operatorController.getRawAxis(4)));

    // new POVButton(m_operatorController, 180).whenHeld(new
    // ParallelCommandGroup(new ShootSetupCommand(
    // m_flywheelSubsystem, m_hoodSubsystem, () -> FieldLocation.WALL), new
    // FeederCommand(m_feederSubsystem), new RunCarouselCommand(m_carouselSubsystem, 20)));

    // new POVButton(m_operatorController, 180).whenHeld(new ParallelCommandGroup(
    // new ShootSetupCommand(m_flywheelSubsystem, m_hoodSubsystem, () ->
    // FieldLocation.WALL),
    // new AutoFeederCommand(m_feederSubsystem, m_carouselSubsystem::atOpenSpace,
    // m_flywheelSubsystem::atSetpoint)));

  }

}

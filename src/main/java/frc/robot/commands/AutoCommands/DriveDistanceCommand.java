// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AutoCommands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveSubsystem;

public class DriveDistanceCommand extends CommandBase {
  private double m_distance;
  private double m_startDistanceLeft;
  private double m_startDistanceRight;
  /** Creates a new DriveDistanceCommand. */
  public DriveDistanceCommand(double distance) {
    m_distance = distance;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(DriveSubsystem.get());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    DriveSubsystem.get().resetEncoders();
    m_startDistanceLeft = DriveSubsystem.get().getLeftEncoderPosition();
    m_startDistanceRight= DriveSubsystem.get().getRightEncoderPosition();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    DriveSubsystem.get().tankDrive(.2, .2); //TODO set speeds
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    double currDistanceLeft = DriveSubsystem.get().getLeftEncoderPosition();
    double currDistanceRight= DriveSubsystem.get().getRightEncoderPosition();
    SmartDashboard.putBoolean("DriveDistance finished", (currDistanceLeft - m_startDistanceLeft) > m_distance && (currDistanceRight - m_startDistanceRight) > m_distance);
    return (currDistanceLeft - m_startDistanceLeft) > m_distance && (currDistanceRight - m_startDistanceRight) > m_distance;
  }
}

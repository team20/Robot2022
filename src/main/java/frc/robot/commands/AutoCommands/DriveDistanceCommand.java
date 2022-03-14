// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AutoCommands;

import java.time.Instant;

import java.time.Duration;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveSubsystem;

public class DriveDistanceCommand extends CommandBase {
  private double m_distance;
  private double m_startDistanceLeft;
  private double m_startDistanceRight;
  private Instant m_startTime;
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
    SmartDashboard.putNumber("encoder start left", DriveSubsystem.get().getLeftEncoderPosition());
    SmartDashboard.putNumber("encoder start right", DriveSubsystem.get().getRightEncoderPosition());
    m_startTime = Instant.now();
    //m_startDistanceLeft = DriveSubsystem.get().getLeftEncoderPosition();
    //m_startDistanceRight= DriveSubsystem.get().getRightEncoderPosition();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
          m_startDistanceLeft = 0;
          m_startDistanceRight= 0;
          SmartDashboard.putNumber("encoder start left", DriveSubsystem.get().getLeftEncoderPosition());
          SmartDashboard.putNumber("encoder start right", DriveSubsystem.get().getRightEncoderPosition());
          DriveSubsystem.get().tankDrive(.4 * Math.signum(m_distance), .4 * Math.signum(m_distance)); //TODO set speeds
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    DriveSubsystem.get().tankDrive(0, 0); //TODO set speeds
    
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    double currDistanceLeft = DriveSubsystem.get().getLeftEncoderPosition();
    double currDistanceRight= DriveSubsystem.get().getRightEncoderPosition();
    SmartDashboard.putNumber("encoder current left", DriveSubsystem.get().getLeftEncoderPosition());
    SmartDashboard.putNumber("encoder current right", DriveSubsystem.get().getRightEncoderPosition());
    SmartDashboard.putBoolean("DriveDistance finished", (currDistanceLeft - m_startDistanceLeft) > m_distance && (currDistanceRight - m_startDistanceRight) > m_distance);
    double elapsed = Duration.between(m_startTime, Instant.now()).toMillis();
    if (elapsed < 350) {
        return false;
    }
    return Math.abs(currDistanceLeft - m_startDistanceLeft) > m_distance && Math.abs(currDistanceRight - m_startDistanceRight) > m_distance;

  }
}

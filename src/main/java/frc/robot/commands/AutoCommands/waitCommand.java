// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AutoCommands;

import java.time.Duration;
import java.time.Instant;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class waitCommand extends CommandBase {
  private Instant m_startTime;
  private double m_waitLength;

  /** Creates a new waitCommand. */
  public waitCommand(double waitLength) {
    m_waitLength = waitLength;
    // Use addRequirements() here to declare subsystem dependencies.
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_startTime = Instant.now();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return Duration.between(m_startTime, Instant.now()).toMillis() > m_waitLength;
  }
}

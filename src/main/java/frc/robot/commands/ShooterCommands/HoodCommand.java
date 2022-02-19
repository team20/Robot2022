// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.ShooterCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.HoodSubsystem;

public class HoodCommand extends CommandBase {
  private HoodSubsystem m_hoodSubsystem;
  private Operation m_operation;
  private double m_hoodParam;

  public enum Operation {
    CMD_SET_POSITION,
    CMD_SETTLE
  }

  /** Creates a new HoodCommand. */
  public HoodCommand(HoodSubsystem hoodSubsystem, Operation operation, double hoodParam) {
    m_hoodSubsystem = hoodSubsystem;
    m_operation = operation;
    m_hoodParam = hoodParam;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_hoodSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if (m_operation == Operation.CMD_SET_POSITION) {
      m_hoodSubsystem.setPosition(m_hoodParam);
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (m_operation == Operation.CMD_SET_POSITION) {
      return true;
    } else if (m_operation == Operation.CMD_SETTLE) {
      return m_hoodSubsystem.atSetpoint();
    }
    return true;
  }
}

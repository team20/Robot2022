// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.ShooterCommands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ArduinoSubsystem;
import frc.robot.subsystems.FlywheelSubsystem;

public class FlywheelCommand extends CommandBase {
  FlywheelSubsystem m_flywheelSubsystem;
  double m_flywheelParam;
  private Operation m_operation;

  public enum Operation {
    CMD_SET_VELOCITY,
    CMD_SETTLE,
    CMD_REVERSE
  }

  /** Creates a new FlywheelCommand. */
  public FlywheelCommand(Operation operation, double flywheelParam) {
    m_flywheelParam = flywheelParam;
    m_operation = operation;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(FlywheelSubsystem.get());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    // System.out.println("STARTING FLYWHEEL");
    // System.out.println("VELOCITY: " + m_flywheelParam);
    // System.out.println("OPERATION: " + m_operation);
    if (m_operation == Operation.CMD_SET_VELOCITY) {
      FlywheelSubsystem.get().setVelocity(m_flywheelParam);
    } else if (m_operation == Operation.CMD_REVERSE) {
      FlywheelSubsystem.get().setVelocityForNegatives();
      FlywheelSubsystem.get().setSpeed(-0.5);
    }

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    if(m_operation==Operation.CMD_REVERSE){
      FlywheelSubsystem.get().setSpeed(0);
    }
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if (m_operation == Operation.CMD_SET_VELOCITY) {
      return true;
    } else if (m_operation == Operation.CMD_SETTLE) {
      SmartDashboard.putBoolean("Flywheel at setpoint", FlywheelSubsystem.get().atSetpoint());
      return FlywheelSubsystem.get().atSetpoint();
    } else if (m_operation == Operation.CMD_REVERSE) {
      return false;
    }
    return true;

  }
}

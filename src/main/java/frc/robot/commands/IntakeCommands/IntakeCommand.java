// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IntakeCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IntakeSubsystem;

public class IntakeCommand extends CommandBase {
  /** Creates a new IntakeCommand. */
  public enum Operation{
    CMD_RUN_FWD,
    CMD_RUN_REV,
    CMD_STOP
  }
  private Operation m_operation;

  public IntakeCommand(Operation operation) {
    m_operation = operation;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(IntakeSubsystem.get());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("starting intake command");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(m_operation == Operation.CMD_RUN_FWD){
      IntakeSubsystem.get().setSpeed(150);
    }else if(m_operation == Operation.CMD_RUN_REV){
      IntakeSubsystem.get().setSpeed(-150);
    }else if(m_operation == Operation.CMD_STOP){
      IntakeSubsystem.get().setSpeed(0);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}

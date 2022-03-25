// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IntakeCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IntakeArmSubsystem;

public class IntakeArmCommand extends CommandBase {
  /** Creates a new IntakeArmCommand. */
  public enum Operation{
    CMD_ARM_UP,
    CMD_ARM_DOWN,
    CMD_ARM_SETTLE,
    CMD_RESET_ENCODER,
    CMD_ARM_MANUAL,
    CMD_ARM_STOP,
  }

  private Operation m_operation;
  public IntakeArmCommand(Operation operation) {
    m_operation = operation;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(IntakeArmSubsystem.get());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("starting arm");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(m_operation == Operation.CMD_ARM_UP){
      IntakeArmSubsystem.get().setPosition(IntakeArmSubsystem.Position.UP_POSITION);
      IntakeArmSubsystem.get().setBrakeMode();
      //System.out.println("setting to up");
      // IntakeArmSubsystem.get().setPercentOutput(0);//TODO find speed
    } else if(m_operation == Operation.CMD_ARM_DOWN){
      //System.out.println("setting to down");
      IntakeArmSubsystem.get().setPosition(IntakeArmSubsystem.Position.DOWN_POSITION);
      IntakeArmSubsystem.get().setCoastMode();
      // IntakeArmSubsystem.get().setPercentOutput(0);//TODO find speed
    } 
    else if(m_operation==Operation.CMD_RESET_ENCODER){
      IntakeArmSubsystem.get().resetEncoder();
    }else if(m_operation==Operation.CMD_ARM_MANUAL){
      IntakeArmSubsystem.get().setPercentOutput(-.6);
    }else if(m_operation==Operation.CMD_ARM_STOP){
      IntakeArmSubsystem.get().setPercentOutput(0);
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if(m_operation == Operation.CMD_ARM_UP || m_operation == Operation.CMD_ARM_DOWN){
      return true;
    }else if(m_operation == Operation.CMD_ARM_SETTLE){
      return IntakeArmSubsystem.get().atSetpoint();
    }
    return true;
  }
}

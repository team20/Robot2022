// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IntakeCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IntakeArmSubsystem;

public class DriveIntakeArmCommand extends CommandBase {
  private Supplier<Double> m_speed;
  /** Creates a new DriveIntakeArmCommand. */
  public DriveIntakeArmCommand(Supplier<Double> percentOutput) {
    // Use addRequirements() here to declare subsystem dependencies.
    m_speed = percentOutput;
    addRequirements(IntakeArmSubsystem.get());
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    //System.out.println("setting speed to: " + m_speed.get());
    IntakeArmSubsystem.get().setPercentOutput(m_speed.get());
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    IntakeArmSubsystem.get().setPercentOutput(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}

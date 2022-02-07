// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IndexerCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IndexerSubsystem;

public class AdvanceIndexerCommand extends CommandBase {
  private IndexerSubsystem m_indexerSubsystem;
  private boolean m_isFinished;

  /** Creates a new AdvanceIndexerCommand. */
  public AdvanceIndexerCommand(IndexerSubsystem indexerSubsystem) {
    m_indexerSubsystem = indexerSubsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_indexerSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    byte currIndexerState = m_indexerSubsystem.getCurrState();
    byte desiredIndexerState = (byte)(currIndexerState >> 1);
    m_indexerSubsystem.setState(desiredIndexerState);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_isFinished = m_indexerSubsystem.atSetState();
    if(m_indexerSubsystem.atSetpoint() && !m_isFinished){
      m_indexerSubsystem.incrementPosition();
    }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_isFinished;
  }
}

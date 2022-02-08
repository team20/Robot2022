// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IndexerCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IndexerSubsystem;

public class AdvanceIndexerCommand extends CommandBase {
  private IndexerSubsystem m_indexerSubsystem;
  private boolean m_isFinished;
  private Supplier<Boolean> m_flywheelReady;
  private byte m_initialIndexerState;
  private byte m_desiredIndexerState;

  /** Creates a new AdvanceIndexerCommand. */
  public AdvanceIndexerCommand(IndexerSubsystem indexerSubsystem) {
    m_indexerSubsystem = indexerSubsystem;
    m_flywheelReady = ()->null;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_indexerSubsystem);
  }
  public AdvanceIndexerCommand(IndexerSubsystem indexerSubsystem, Supplier<Boolean> flywheelReady) {
    m_indexerSubsystem = indexerSubsystem;
    m_flywheelReady = flywheelReady;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_indexerSubsystem);
  }
  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_initialIndexerState = m_indexerSubsystem.getCurrState();
    m_desiredIndexerState = (byte)(m_initialIndexerState >> 1);
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(m_flywheelReady == null || m_flywheelReady.get()){
      m_indexerSubsystem.setState(m_desiredIndexerState, 1);
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

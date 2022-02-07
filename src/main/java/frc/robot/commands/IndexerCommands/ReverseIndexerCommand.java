// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IndexerCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IndexerSubsystem;

public class ReverseIndexerCommand extends CommandBase {
  private IndexerSubsystem m_indexerSubsystem;
  private boolean m_keepBallRTF;
  private byte andState = 00000111;
  private byte orState = 00000100;
  private boolean m_isFinished;
  /** Creates a new ReverseIndexerCommand. */
  public ReverseIndexerCommand(IndexerSubsystem indexerSubsystem, boolean keepBallRTF) {
    // Use addRequirements() here to declare subsystem dependencies.
    m_indexerSubsystem = indexerSubsystem;
    m_keepBallRTF = keepBallRTF;
    addRequirements(m_indexerSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if(m_keepBallRTF){
      byte currState = m_indexerSubsystem.getCurrState();
      byte desState = (byte)((byte)(currState << 1) & andState | orState);
      m_indexerSubsystem.setState(desState);
    }else{
      byte currState = m_indexerSubsystem.getCurrState();
      byte desState = (byte)((byte)(currState << 1) & andState);
      m_indexerSubsystem.setState(desState);
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    m_isFinished = m_indexerSubsystem.atSetState();
    if(m_indexerSubsystem.atSetpoint() && !isFinished()){
      m_indexerSubsystem.decrementPosition();
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

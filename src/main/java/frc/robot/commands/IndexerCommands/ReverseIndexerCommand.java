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

  private byte m_initialIndexerState;
  private byte m_desiredIndexerState;
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
    m_initialIndexerState = m_indexerSubsystem.getCurrState();
    if(m_keepBallRTF){
      m_desiredIndexerState = (byte)((byte)(m_initialIndexerState << 1) & andState | orState);
    }else{
      m_desiredIndexerState = (byte)((byte)(m_initialIndexerState << 1) & andState);
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {  
    m_indexerSubsystem.setSpeed(-1); //TODO set speed    
    m_indexerSubsystem.setState(m_desiredIndexerState);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    m_indexerSubsystem.setSpeed(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return m_indexerSubsystem.atSetState();
  }
}

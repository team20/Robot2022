// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IndexerCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.IndexerSubsystem;

public class IndexerCommand extends CommandBase {
  private IndexerSubsystem m_indexerSubsystem;
  private byte m_initialIndexerState;
  private byte m_desiredIndexerState;
  private Operation m_operation;
  public enum Operation{
    CMD_ADV,
    CMD_REV
  };
  private byte andState = 00000111;
  private byte orState = 00000100;
  private boolean m_keepBallRTF = true;
  /** Creates a new AdvanceIndexerCommand. */
  public IndexerCommand(IndexerSubsystem indexerSubsystem, Operation operation) {
    m_operation = operation;
    m_indexerSubsystem = indexerSubsystem;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_indexerSubsystem);
  }
  public IndexerCommand(IndexerSubsystem indexerSubsystem, boolean keepBallRTF, Operation operation) {
    m_operation = operation;
    m_indexerSubsystem = indexerSubsystem;
    m_keepBallRTF = keepBallRTF;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_indexerSubsystem);
  }
  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    m_initialIndexerState = m_indexerSubsystem.getCurrState();
    if(m_operation == Operation.CMD_ADV){
      m_desiredIndexerState = (byte)(m_initialIndexerState >> 1);
    }else{
      if(m_keepBallRTF){
        m_desiredIndexerState = (byte)((byte)(m_initialIndexerState << 1) & andState | orState);
      }else{
        m_desiredIndexerState = (byte)((byte)(m_initialIndexerState << 1) & andState);
      }
    }
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
      m_indexerSubsystem.setState(m_desiredIndexerState);
      if(m_operation == Operation.CMD_ADV){
        m_indexerSubsystem.setSpeed(1); //TODO find speed
      }else{
        m_indexerSubsystem.setSpeed(-1); //TODO find speed
      }
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

  public static Command getLoadCommand(IndexerSubsystem indexerSubsystem){
    if(indexerSubsystem.gamePieceRTF() && indexerSubsystem.gamePieceRTS()){
        return new SequentialCommandGroup(new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_REV), new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV));
    }else{
        return new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV);
    }
}
/**
 * NOTE: MUST BE USED IN SEQUENTIAL COMMAND GROUP AFTER FLYWHEEL SETTLE COMMAND **NO PARALLEL COMMAND GROUPS**
 * TODO: Add flywheel settle command so that we wait until flywheel is at setpoint before continuing
 * @param indexerSubsystem
 * @param flywheelSubsystem
 * @return
 */
public static Command getShootCommand(IndexerSubsystem indexerSubsystem){
    if(indexerSubsystem.gamePieceRTS()){
        return new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV);
    }else if(indexerSubsystem.gamePieceAtCenter()){
        return new SequentialCommandGroup(new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV), new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV));
    }else{
        return new SequentialCommandGroup(new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV), new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV), new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV));
    }
}
}

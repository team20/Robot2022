// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IndexerCommands;

import java.time.Duration;
import java.time.Instant;

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
    //advance ball(s) one pos forward
    CMD_ADV,
    //bring ball(s) one pos backwards 
    CMD_REV,
    //manually run indexer forward
    CMD_FWD_MAN,
    //manually run indexer backward
    CMD_REV_MAN
  };

  
  private boolean m_keepBallRTF = true;

  private Instant m_startTime;

  /** Creates a new AdvanceIndexerCommand. */
  public IndexerCommand(IndexerSubsystem indexerSubsystem, Operation operation) {

    //save desired operation and the subsystem
    m_operation = operation;
    m_indexerSubsystem = indexerSubsystem;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_indexerSubsystem);
  }
  public IndexerCommand(IndexerSubsystem indexerSubsystem, boolean keepBallRTF, Operation operation) {
    //save desired operation, the indexer subsystem, and whether or not we want the sensors to reflect that if we move backwards, the RTF ball will stay in the same position
    m_operation = operation;
    m_indexerSubsystem = indexerSubsystem;
    m_keepBallRTF = keepBallRTF;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(m_indexerSubsystem);
  }
  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    if(m_operation == Operation.CMD_ADV){
      m_desiredIndexerState = m_indexerSubsystem.getAdvanceTargetState();
    }else{
        m_desiredIndexerState = m_indexerSubsystem.getReverseTargetState(m_keepBallRTF);
    }
    m_startTime = Instant.now();
    

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
      //set speeds and/or target states depending on desired operations
      if(m_operation == Operation.CMD_ADV){
        m_indexerSubsystem.setTargetState(m_desiredIndexerState);
        m_indexerSubsystem.setSpeed(1); //TODO find speed
      }else if(m_operation == Operation.CMD_REV){
        m_indexerSubsystem.setTargetState(m_desiredIndexerState);
        m_indexerSubsystem.setSpeed(-1); //TODO find speed
      } else if(m_operation == Operation.CMD_FWD_MAN){
        m_indexerSubsystem.setSpeed(1); //TODO find speed
      }else{
        m_indexerSubsystem.setSpeed(-1); //TODO find speed
      }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {

    //stop the motor when the command finishes
    m_indexerSubsystem.setSpeed(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {

    //amount of time elapsed since we started the command
    double elapsed = Duration.between(m_startTime, Instant.now()).toMillis();
    
    //max time to run to get to a state(will be ignored if controlled manually)
    double max_duration = 2000;
    
    //finish when we reach our target state or timeout
    if(elapsed > max_duration && !((m_operation == Operation.CMD_FWD_MAN) || (m_operation == Operation.CMD_REV_MAN))){
      return true;
    }
    return m_indexerSubsystem.atTargetState();
  }


  /**
   * Based on the current indexer sensor states, decide what commands are needed in order to load a ball and return them
   * @param indexerSubsystem
   * @return command or command group to load a ball into the indexer
   */
  public static Command getLoadCommand(IndexerSubsystem indexerSubsystem){
    if(indexerSubsystem.gamePieceRTS()){
        return new SequentialCommandGroup(new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_REV), 
                                          new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV));
    }else{
        return new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV);
    }
}
/**
 * Based on the current indexer sensor states, decide what commands are needed in order to shoot a ball and return them
 * NOTE: MUST BE USED IN SEQUENTIAL COMMAND GROUP AFTER FLYWHEEL SETTLE COMMAND **NO PARALLEL COMMAND GROUPS**
 * TODO: Add flywheel settle command so that we wait until flywheel is at setpoint before continuing
 * @param indexerSubsystem
 * @param flywheelSubsystem
 * @return command or command group to shoot a ball on the indexer side of things
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
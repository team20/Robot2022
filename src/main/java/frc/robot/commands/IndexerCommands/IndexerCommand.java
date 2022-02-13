// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IndexerCommands;

import java.time.Duration;
import java.time.Instant;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IndexerSubsystem;

public class IndexerCommand extends CommandBase {
  private IndexerSubsystem m_indexerSubsystem;
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
    CMD_REV_MAN,

    CMD_WAIT_RTF,

    CMD_TO_EXPECTED_POSITION
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
    } else if(m_operation == Operation.CMD_REV){
      m_desiredIndexerState = m_indexerSubsystem.getReverseTargetState(m_keepBallRTF);
    } else if(m_operation == Operation.CMD_TO_EXPECTED_POSITION){
      m_desiredIndexerState = m_indexerSubsystem.getCurrTargetState();
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
      }else if(m_operation == Operation.CMD_REV_MAN){
        m_indexerSubsystem.setSpeed(-1); //TODO find speed
      }else if(m_operation == Operation.CMD_TO_EXPECTED_POSITION){
        m_indexerSubsystem.setSpeed(m_indexerSubsystem.getLastSpeed());
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
    if(m_operation == Operation.CMD_ADV || m_operation == Operation.CMD_REV || m_operation == Operation.CMD_TO_EXPECTED_POSITION){
      //amount of time elapsed since we started the command
      double elapsed = Duration.between(m_startTime, Instant.now()).toMillis();
          
      //max time to run to get to a state(will be ignored if controlled manually)
      double max_duration = 2000;

      //finish when we reach our target state or timeout
      if(elapsed > max_duration){
        return true;
      }
      return m_indexerSubsystem.atTargetState();
    } else if(m_operation == Operation.CMD_FWD_MAN || m_operation == Operation.CMD_REV_MAN){
      return false;
    } else if(m_operation == Operation.CMD_WAIT_RTF){
      return m_indexerSubsystem.gamePieceRTF();
    }
    return true;
    
  }

}

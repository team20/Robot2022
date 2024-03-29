// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IndexerCommands;

import java.time.Duration;
import java.time.Instant;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IndexerSubsystem;

public class IndexerCommand extends CommandBase {
  // private byte m_desiredIndexerState;
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
    //wait until a ball is ready to feed, then end
    CMD_WAIT_RTF,
    //try to move to the target position
    CMD_STOP,
    CMD_SMALL_REV
  };

  
  // private boolean m_keepBallRTF = true;

  private Instant m_startTime;

  /** Creates a new AdvanceIndexerCommand. */
  public IndexerCommand(Operation operation) {

    //save desired operation and the subsystem
    m_operation = operation;

    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(IndexerSubsystem.get());
  }
  public IndexerCommand(IndexerSubsystem indexerSubsystem, boolean keepBallRTF, Operation operation) {
    //save desired operation, the indexer subsystem, and whether or not we want the sensors to reflect that if we move backwards, the RTF ball will stay in the same position
    m_operation = operation;
    // m_keepBallRTF = keepBallRTF;
    // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(IndexerSubsystem.get());
  }
  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    //System.out.println("RUNNING INDEXER COMMAND");
    IndexerSubsystem indexerSubsystem = IndexerSubsystem.get();
    if (m_operation == Operation.CMD_ADV) {
      // System.out.println("Desired state: " + (byte)m_desiredIndexerState);
      indexerSubsystem.setPositionAdvance();
    } else if (m_operation == Operation.CMD_REV) {
      indexerSubsystem.setPositionReverse();
      //System.out.println((byte)m_desiredIndexerState);
    } else if (m_operation == Operation.CMD_SMALL_REV) {
      indexerSubsystem.setPositionSmallReverse();
    }
    m_startTime = Instant.now();
    

  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
      IndexerSubsystem indexerSubsystem = IndexerSubsystem.get();
      //set speeds and/or target states depending on desired operations
      if(m_operation == Operation.CMD_FWD_MAN){
        indexerSubsystem.setSpeed(.5);
      }else if(m_operation == Operation.CMD_REV_MAN){
        indexerSubsystem.setSpeed(-.5);
      }else if(m_operation == Operation.CMD_STOP){
        indexerSubsystem.setSpeed(0);
      }
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    //stop the motor when the command finishes
    if(!(m_operation == Operation.CMD_FWD_MAN || m_operation == Operation.CMD_REV_MAN || m_operation == Operation.CMD_SMALL_REV)){
      //IndexerSubsystem.get().setSpeed(0);
    }
  }
 
  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    if(m_operation == Operation.CMD_ADV || m_operation == Operation.CMD_REV){
      //amount of time elapsed since we started the command
      double elapsed = Duration.between(m_startTime, Instant.now()).toMillis();
          
      //max time to run to get to a state(will be ignored if controlled manually)
      double max_duration = 2000;

      //finish when we reach our target state or timeout
      if(elapsed > max_duration){
        return true;
      }
      return IndexerSubsystem.get().atTargetPosition();
    } else if(m_operation == Operation.CMD_FWD_MAN || m_operation == Operation.CMD_REV_MAN){
      return true;
    } else if(m_operation == Operation.CMD_WAIT_RTF){
      return IndexerSubsystem.get().gamePieceRTF();
    } 
    return true;
    
  }

}

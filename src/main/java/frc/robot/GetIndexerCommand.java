// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.IndexerCommands.IndexerCommand;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.IndexerSubsystem;

/** Add your docs here. */
public class GetIndexerCommand {
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
    public static Command getShootCommand(IndexerSubsystem indexerSubsystem, FlywheelSubsystem flywheelSubsystem){
        if(indexerSubsystem.gamePieceRTS()){
            return new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV);
        }else if(indexerSubsystem.gamePieceAtCenter()){
            return new SequentialCommandGroup(new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV), new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV));
        }else{
            return new SequentialCommandGroup(new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV), new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV), new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_ADV));
        }
    }
}

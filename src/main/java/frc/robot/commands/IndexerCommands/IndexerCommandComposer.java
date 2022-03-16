// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.IndexerCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.subsystems.IndexerSubsystem;

/** Add your docs here. */
public class IndexerCommandComposer {
    /**
     * Based on the current indexer sensor states, decide what commands are needed
     * in order to load a ball and return them
     * 
     * @param indexerSubsystem
     * @return command or command group to load a ball into the indexer
     */
    public static Command getLoadCommand() {
        
            return new IndexerCommand(IndexerCommand.Operation.CMD_ADV);
    }

    /**
     * Based on the current indexer sensor states, decide what commands are needed
     * in order to shoot a ball and return them
     * NOTE: MUST BE USED IN SEQUENTIAL COMMAND GROUP AFTER FLYWHEEL SETTLE COMMAND
     * **NO PARALLEL COMMAND GROUPS**
     * TODO: Add flywheel settle command so that we wait until flywheel is at
     * setpoint before continuing
     * 
     * @param indexerSubsystem
     * @param flywheelSubsystem
     * @return command or command group to shoot a ball on the indexer side of
     *         things
     */
    public static Command getShootCommand() {
        // if (IndexerSubsystem.get().gamePieceRTS() && !IndexerSubsystem.get().gamePieceAtCenter()) {
        //     return new IndexerCommand(IndexerCommand.Operation.CMD_ADV);
        // } else if (IndexerSubsystem.get().gamePieceAtCenter()) {
        //     return new SequentialCommandGroup(new IndexerCommand(IndexerCommand.Operation.CMD_ADV),
        //             new IndexerCommand(IndexerCommand.Operation.CMD_ADV));
        // } else {
            return new SequentialCommandGroup(new IndexerCommand(IndexerCommand.Operation.CMD_ADV),
                    new IndexerCommand(IndexerCommand.Operation.CMD_ADV),
                    new IndexerCommand(IndexerCommand.Operation.CMD_ADV));
        //}
    }
    //public static Command prep(IndexerSubsystem indexerSubsystem){
    //    return new IndexerCommand(indexerSubsystem, IndexerCommand.Operation.CMD_TO_EXPECTED_POSITION);
    //}
    //public static Command getReadyToShoot(){
        //if (IndexerSubsystem.get().gamePieceRTS()) {
    //        return new IndexerCommand(null);
        //} else if (IndexerSubsystem.get().gamePieceAtCenter()) {
        //    return new IndexerCommand(IndexerCommand.Operation.CMD_ADV);
        //} else {
        //    return new SequentialCommandGroup(new IndexerCommand(IndexerCommand.Operation.CMD_ADV),
        //            new IndexerCommand(IndexerCommand.Operation.CMD_ADV));
        //}
    //}
}

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.IndexerCommands.AdvanceIndexerCommand;
import frc.robot.commands.IndexerCommands.ReverseIndexerCommand;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.IndexerSubsystem;

/** Add your docs here. */
public class IndexerCommand {
    public static Command getLoadCommand(IndexerSubsystem indexerSubsystem){
        if(indexerSubsystem.gamePieceRTF() && indexerSubsystem.gamePieceRTS()){
            return new SequentialCommandGroup(new ReverseIndexerCommand(indexerSubsystem, true), new AdvanceIndexerCommand(indexerSubsystem));
        }else{
            return new AdvanceIndexerCommand(indexerSubsystem);
        }
    }
    public static Command getShootCommand(IndexerSubsystem indexerSubsystem, FlywheelSubsystem flywheelSubsystem){
        if(indexerSubsystem.gamePieceRTS()){
            return new AdvanceIndexerCommand(indexerSubsystem, flywheelSubsystem::atSetpoint);
        }else if(indexerSubsystem.gamePieceAtCenter()){
            return new SequentialCommandGroup(new AdvanceIndexerCommand(indexerSubsystem), new AdvanceIndexerCommand(indexerSubsystem, flywheelSubsystem::atSetpoint));
        }else{
            return new SequentialCommandGroup(new AdvanceIndexerCommand(indexerSubsystem), new AdvanceIndexerCommand(indexerSubsystem), new AdvanceIndexerCommand(indexerSubsystem, flywheelSubsystem::atSetpoint));
        }
    }
}

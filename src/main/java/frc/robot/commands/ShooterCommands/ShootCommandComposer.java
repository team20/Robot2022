// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.ShooterCommands;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.LinearRangeFinder;
import frc.robot.RangeFinder;
import frc.robot.RegressionRangeFinder;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.HoodSubsystem;

public class ShootCommandComposer{
  public static Command getShootCommand(FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem, double distance, String shootClass){
    RangeFinder distanceClass;
    if (shootClass.equals("LINEAR")) {
      distanceClass = new LinearRangeFinder();
    } else {
      //shootClass.equals("REGRESSION")
      distanceClass = new RegressionRangeFinder();
    }

    double hoodSetpoint = distanceClass.getAngleAndRPM(distance)[0];
    double flywheelSetpoint = distanceClass.getAngleAndRPM(distance)[1];

    //set the setpoints
    ParallelCommandGroup setGroup = new ParallelCommandGroup(new FlywheelCommand(flywheelSubsystem, FlywheelCommand.Operation.CMD_SET_VELOCITY, flywheelSetpoint),
                                                              new HoodCommand(hoodSubsystem, HoodCommand.Operation.CMD_SET_POSITION, hoodSetpoint));     
    //wait for the setpoints                                                             
    ParallelCommandGroup settleGroup = new ParallelCommandGroup(new FlywheelCommand(flywheelSubsystem, FlywheelCommand.Operation.CMD_SETTLE, 0),
                                                                new HoodCommand(hoodSubsystem, HoodCommand.Operation.CMD_SETTLE, 0));    
    //first set the setpoints, then wait for them to settle
    SequentialCommandGroup actionGroup = new SequentialCommandGroup(setGroup,settleGroup);
    
    return actionGroup;
  }
  public static Command getShootStopCommand(FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem){

    ParallelCommandGroup stopOnInterruptGroup = new ParallelCommandGroup(new FlywheelCommand(flywheelSubsystem, FlywheelCommand.Operation.CMD_STOP_ON_INTERRUPT, 0), 
                                                                          new HoodCommand(hoodSubsystem, HoodCommand.Operation.CMD_STOP_ON_INTERRUPT, 0));
    return stopOnInterruptGroup;
  }
}

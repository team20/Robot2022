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
public class ShootCommandComposer {

  public static enum Operation{
    LIMELIGHT_LINEAR,
    LIMELIGHT_REGRESSION,
    PRESET_LAUNCHPAD,
    PRESET_TARMAC,
    PRESET_FENDER_HIGH,
    PRESET_FENDER_LOW
  }

  public static Command getShootCommand(double distance, Operation shootClass) {

    
    RangeFinder distanceClass;
    // if (shootClass.equals("LINEAR")) {
    //   distanceClass = new LinearRangeFinder();
    // } else {
    //   // shootClass.equals("REGRESSION")
    //   distanceClass = new RegressionRangeFinder();
    double hoodSetpoint;
    double flywheelSetpoint;
    if(shootClass==Operation.PRESET_TARMAC){
      hoodSetpoint=9.5;
      flywheelSetpoint=4000;
    }
    else if(shootClass==Operation.PRESET_FENDER_LOW){
      hoodSetpoint=11;
      flywheelSetpoint=2000;
    }
    else if(shootClass==Operation.PRESET_FENDER_HIGH){
      hoodSetpoint=0.5;
      flywheelSetpoint=3550;
    }
    else{
      hoodSetpoint=0;
      flywheelSetpoint=0;
    }
    

    // double hoodSetpoint = distanceClass.getAngleAndRPM(distance)[0];
    // double flywheelSetpoint = distanceClass.getAngleAndRPM(distance)[1];

    // set the setpoints
    ParallelCommandGroup setGroup = new ParallelCommandGroup(
        new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, flywheelSetpoint),
        new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, hoodSetpoint));
    // wait for the setpoints
    ParallelCommandGroup settleGroup = new ParallelCommandGroup(
        new FlywheelCommand(FlywheelCommand.Operation.CMD_SETTLE, 0),
        new HoodCommand(HoodCommand.Operation.CMD_SETTLE, 0));
    // first set the setpoints, then wait for them to settle
    return new SequentialCommandGroup(setGroup, settleGroup);
  }

  public static Command getShootStopCommand() {

    return new ParallelCommandGroup(
        new FlywheelCommand(FlywheelCommand.Operation.CMD_SET_VELOCITY, 0),
        new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0));
  }
}

// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.ShooterCommands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.LinearRangeFinder;
import frc.robot.RangeFinder;
import frc.robot.RegressionRangeFinder;
import frc.robot.subsystems.LimelightSubsystem;
// import frc.robot.LinearRangeFinder;
// import frc.robot.RangeFinder;
// import frc.robot.RegressionRangeFinder;
public class ShootCommandComposer {

  public static enum Operation{
    LIMELIGHT_LINEAR,
    LIMELIGHT_REGRESSION,
    PRESET_LAUNCHPAD,
    PRESET_TARMAC,
    PRESET_FENDER_HIGH,
    PRESET_FENDER_LOW,
    PRESET_SAFE,
    MIN_RAMP_UP
  }

  public static Command getShootCommand(double distance, Operation shootClass) {
    
    double hoodSetpoint;
    double flywheelSetpoint;    
    
   if (shootClass==Operation.LIMELIGHT_REGRESSION) {
      LimelightSubsystem.get().turnOnLight();
      double angle = LimelightSubsystem.get().getYAngle();
      flywheelSetpoint = -17.589*angle + 2694.4;
      hoodSetpoint = -0.2671*angle + 12.545;
    }
    else if (shootClass==Operation.LIMELIGHT_LINEAR) {
      double yAngle = LimelightSubsystem.get().getYAngle();
      LinearRangeFinder finder = new LinearRangeFinder();
      double[] angleAndRPM = finder.getAngleAndRPM(yAngle);
      hoodSetpoint = angleAndRPM[0];
      flywheelSetpoint = angleAndRPM[1];
    }
    else if(shootClass==Operation.PRESET_TARMAC){
      // hoodSetpoint=8;
      //flywheelSetpoint=2400;
    //  hoodSetpoint=SmartDashboard.getNumber("Hood Setpoint", 0);
    //  flywheelSetpoint=SmartDashboard.getNumber("Flywheel RPM", 0);
      //was: hood 8.5, flywheel 4000
      hoodSetpoint = 14.5;
      flywheelSetpoint = 2900;
    }
    else if(shootClass==Operation.PRESET_FENDER_LOW){
      hoodSetpoint=9.59;
      flywheelSetpoint=1600;//2200;//was 2000
    }
    else if(shootClass==Operation.PRESET_FENDER_HIGH){
      hoodSetpoint=0;
      flywheelSetpoint=2300;//2500;//3100;//3700;
    }
    else if(shootClass==Operation.PRESET_SAFE){
      hoodSetpoint=9;
      flywheelSetpoint=2500;
    } else if (shootClass==Operation.MIN_RAMP_UP) {
      hoodSetpoint = 0;
      flywheelSetpoint = 2000;
    }



    else{
      hoodSetpoint=SmartDashboard.getNumber("Hood Setpoint", 0);
      flywheelSetpoint=SmartDashboard.getNumber("Flywheel RPM", 0);
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
        new HoodCommand(HoodCommand.Operation.CMD_SET_POSITION, 0),
        new HoodCommand(HoodCommand.Operation.CMD_POWER_ZERO, 0));
  }
}

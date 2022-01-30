// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static final class ArduinoConstants {
		public static final int kAddress = 0x1;
        public static final double kDistanceP = 0;
        public static final double kDistanceI = 0;
        public static final double kDistanceD = 0;
        public static final int kDistanceSetpoint = 0;
        
        public static final double kAngleP = 0;
        public static final double kAngleI = 0;
        public static final double kAngleD = 0;
        public static final int kAngleSetpoint = 0;

        public static final int kReadTargetInView = 0;
		public static final int[] kReadXValue = { 1, 2, 3 };
		public static final int[] kReadDistance = { 4, 5, 6 };

    }
    public static final class DriveConstants {
      public static final int kMasterLeftPort = 4;
      public static final boolean kMasterLeftInvert = false;
      public static final int kFollowerLeftPort = 3;
      public static final boolean kFollowerLeftOppose = false;
  
      public static final int kMasterRightPort = 5;
      public static final boolean kMasterRightInvert = false;
      public static final int kFollowerRightPort = 6;
      public static final boolean kFollowerRightOppose = false;
  
      public static final int kSmartCurrentLimit = 60;
      public static final double kPeakCurrentLimit = 75;
      public static final int kPeakCurrentDurationMillis = 100;
      public static final double kP = .14;//0.198;
      public static final double kI = 0;
      public static final double kD = 0;
      public static final double kIz = 0;
      public static final double kFF = 0;
      public static final double kMaxOutput = 1;
      public static final double kMinOutput = -1;
      public static final int kSlotID = 0;
  
      public static final SPI.Port kGyroPort = SPI.Port.kMXP;
      public static final boolean kGyroReversed = true;
  
      public static final double ksVolts = 0.196;
      public static final double kvVoltSecondsPerMeter = 2.15;
      public static final double kaVoltSecondsSquaredPerMeter = .53;
      public static final double kTrackwidthMeters = 0.7815245428457417;
      public static final double kMaxSpeedMetersPerSecond = 1;
      public static final double kMaxAccelerationMetersPerSecondSquared = .5;
      public static final double kMaxRotSpeedMetersPerSecond = 1;
      public static final double kWheelDiameterMeters = Units.inchesToMeters(6);
      public static final double kGearRatio = 8.18;
  
      public static final DifferentialDriveKinematics kDriveKinematics = new DifferentialDriveKinematics(
          kTrackwidthMeters);
      public static final SimpleMotorFeedforward kFeedForward = new SimpleMotorFeedforward(DriveConstants.ksVolts,
          DriveConstants.kvVoltSecondsPerMeter, DriveConstants.kaVoltSecondsSquaredPerMeter);
      public static final DifferentialDriveVoltageConstraint kVoltageConstraint = new DifferentialDriveVoltageConstraint(
          DriveConstants.kFeedForward, DriveConstants.kDriveKinematics, 10);
      public static final TrajectoryConfig kTrajectoryConfig = new TrajectoryConfig(
          DriveConstants.kMaxSpeedMetersPerSecond, DriveConstants.kMaxAccelerationMetersPerSecondSquared)
              .setKinematics(DriveConstants.kDriveKinematics)
              .addConstraint(DriveConstants.kVoltageConstraint);
  
      public static final double kTurningMultiplier = .45;
      public static final double kQuickStopThreshold = .2;
      public static final double kQuickStopAlpha = .1;
      public static final double kBackupDistance = Units.feetToMeters(2);
      public static final double kRampRate = .1;
      public static final double kSpeedLimitFactor = .75;
  
      public static final boolean kLeftSensorPhase = true; //TODO these are totally arbitrary right now and need to be checked
      public static final boolean kRightSensorPhase = false;
  
      public static final boolean kEnableVoltageComp = true;
      public static final double kVoltageComp = 12;
      public static final double kEncoderCounts = 4096;
  
      public static final double kEncoderPositionConversionFactor = (1/DriveConstants.kEncoderCounts)*
      (1 / DriveConstants.kGearRatio) * Math.PI * DriveConstants.kWheelDiameterMeters;
  
      public static final double kEncoderVelocityConversionFactor = (1/DriveConstants.kEncoderCounts)*
      (1 / DriveConstants.kGearRatio) * Math.PI * DriveConstants.kWheelDiameterMeters * 1000;
  
    }
}

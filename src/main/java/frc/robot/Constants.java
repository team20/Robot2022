package frc.robot;

import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.kinematics.DifferentialDriveKinematics;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.SPI;

public final class Constants {

	public static final class ControllerConstants {
		public static final int kDriverControllerPort = 0;
		public static final int kOperatorControllerPort = 1;
		public static final double kDeadzone = 0.1;
		public static final double kTriggerDeadzone = .05;

		public static final class Axis {
			public static final int kLeftX = 0;
			public static final int kLeftY = 1;
			public static final int kRightX = 2;
			public static final int kLeftTrigger = 3;
			public static final int kRightTrigger = 4;
			public static final int kRightY = 5;
		}

		public static final class Button {
			public static final int kSquare = 1;
			public static final int kX = 2;
			public static final int kCircle = 3;
			public static final int kTriangle = 4;
			public static final int kLeftBumper = 5;
			public static final int kRightBumper = 6;
			public static final int kShare = 9;
			public static final int kOptions = 10;
			public static final int kLeftStick = 11;
			public static final int kRightStick = 12;
			public static final int kPS = 13;
			public static final int kTrackpad = 14;
		}

		public static final class DPad {
			public static final int kUp = 0;
			public static final int kRight = 90;
			public static final int kDown = 180;
			public static final int kLeft = 270;
		}
	}

	public static final class DriveConstants {
		public static final int kFrontLeftPort = 4;
		public static final boolean kFrontLeftInvert = true;
		public static final int kBackLeftPort = 3;
		public static final boolean kBackLeftOppose = false;

		public static final int kFrontRightPort = 5;
		public static final boolean kFrontRightInvert = false;
		public static final int kBackRightPort = 6;
		public static final boolean kBackRightOppose = false;

		public static final int kSmartCurrentLimit = 60;
		public static final double kPeakCurrentLimit = 75;
		public static final int kPeakCurrentDurationMillis = 100;
		public static final double kP = .14;// 0.198;
		public static final double kI = 0;
		public static final double kD = 0;
		public static final double kIz = 0;
		public static final double kFF = 0;
		public static final double kMaxOutput = 1;
		public static final double kMinOutput = -1;
		public static final int kSlotID = 0;

		// navX stuff
		public static final SPI.Port kGyroPort = SPI.Port.kMXP;
		public static final boolean kGyroReversed = true;

		// TODO these need to be updated to reflect this year's robot
		public static final double ksVolts = 0.196;
		public static final double kvVoltSecondsPerMeter = 2.15;
		public static final double kaVoltSecondsSquaredPerMeter = .53;

		// TODO irrelevant if we use the SPARK MAX PID controller
		public static final double kPDriveVel = 8.5;

		// TODO change the trackwidth to match our robot - trackwidth = horizontal
		// distance between the wheels
		public static final double kTrackwidthMeters = 0.7815245428457417;
		public static final DifferentialDriveKinematics kDriveKinematics = new DifferentialDriveKinematics(
				kTrackwidthMeters);

		// TODO Ramsete controller parameters, should work for robots
		public static final double kRamseteB = 2;
		public static final double kRamseteZeta = 0.7;

		//TODO change
		public static final double kMaxSpeedMetersPerSecond = 1;
		public static final double kMaxAccelerationMetersPerSecondSquared = .5;

		public static final double kMaxRotSpeedMetersPerSecond = 1;
		public static final double kWheelDiameterMeters = Units.inchesToMeters(6);
		public static final double kGearRatio = 8.18;

		public static final double kTurningMultiplier = .45;
		public static final double kQuickStopThreshold = .2;
		public static final double kQuickStopAlpha = .1;
		public static final double kBackupDistance = Units.feetToMeters(2);
		public static final double kRampRate = .1;
		public static final double kSpeedLimitFactor = .75;

		public static final boolean kLeftSensorPhase = true; // TODO these are totally arbitrary right now and need to
																// be checked
		public static final boolean kRightSensorPhase = false;

		public static final boolean kEnableVoltageComp = true;
		public static final double kVoltageComp = 12;
		public static final double kEncoderCounts = 4096;

		//for Talons, don't use
			// public static final double kEncoderPositionConversionFactor = (1 / DriveConstants.kEncoderCounts) *
			// 		(1 / DriveConstants.kGearRatio) * Math.PI * DriveConstants.kWheelDiameterMeters;

			// public static final double kEncoderVelocityConversionFactor = (1 / DriveConstants.kEncoderCounts) *
			// 		(1 / DriveConstants.kGearRatio) * Math.PI * DriveConstants.kWheelDiameterMeters * 1000;

	}

	public static final class HoodConstants {
		public static final int kMotorPort = 12;
		public static final boolean kInvert = true;
		public static final int kSmartCurrentLimit = 60;
		public static final double kP = 0.000_1;
		public static final double kI = 0.0;
		public static final double kD = 0.0;
		public static final double kIz = 0;
		public static final double kFF = 0;
		public static final double kMaxOutput = 1;
		public static final double kMinOutput = -1;
		public static final int kSlotID = 0;
		public static final double kMinVelocity = 0;
		public static final double kMaxAcel = 20_000;
		public static final double kMaxVelocity = 10_000;
		public static final double kAllowedError = 0.2;

		public static final double kMinEncoderValue = 0.0;
		public static final double kMaxEncoderValue = 42.0;
		public static final double kMinAngle = 24.36;
		public static final double kMaxAngle = 77.64;
	}

	public static final class FeederConstants {
		public static final boolean kInvert = false;
		public static final int kMotorPort = 1;
		public static final double kSpeed = .9;
	}

	public static final class CarouselConstants {
		public static final int kMotorPort = 14;
		public static final boolean kInvert = true;
		public static final int kSmartCurrentLimit = 20;
		public static final double kP = 0.01; // .00001
		public static final double kI = 0;
		public static final double kD = 0.01;
		public static final double kIz = 0;
		public static final double kFF = 0.0138;// 000095;
		public static final double kMaxOutput = 1;
		public static final double kMinOutput = -1;

		public static final double kPositionP = 0.0;// TODO - tune
		public static final double kPositionI = 0.0;
		public static final double kPositionD = 0.0;
		public static final double kPositionIz = 0;
		public static final double kPositionFF = 0;
		public static final int kSlotID = 0;
		public static final double kMinVelocity = 0;
		public static final double kMaxAcel = 100;
		public static final double kMaxVelocity = 20;
		public static final double kAllowedError = 0.001;

		public static final int kMagSensorPort = 0;

		public static final double kVelocity = 20;
		public static final double kIntakeVelocity = 30;
		public static final double kJostleVelocity = -65;
		public static final double kGearRatio = 141.0;
		public static final double kStartPositionTolerance = .1; // 5
	}

	public static final class FlywheelConstants {
		public static final int kMasterPort = 11;
		public static final int kFollowerPort = 13;
		public static final boolean kMasterInvert = false;
		public static final boolean kFollowerOppose = true;
		public static final int kSmartCurrentLimit = 50;
		public static final double kPeakCurrentLimit = 60;
		public static final int kPeakCurrentDurationMillis = 100;
		public static final double kP = 0.000_167; // 0.000_375; then .0004
		public static final double kI = 0;
		public static final double kD = 0.000_0125;// 0.000_03;
		public static final double kIz = 0.0;
		public static final double kFF = .000_0804;// 0.000_193;
		public static final double kMaxOutput = 1;
		public static final double kMinOutput = -1;
		public static final double kGearRatio = 1 / 2.4;
		public static final double kAllowedErrorPercent = 5;
	}

	public static final class LimelightConstants { // TODO: tune PID loop
		public static final double kDisP = 0.02;
		public static final double kDisI = 0;
		public static final double kDisD = 0;
		public static final double kTurnP = 0.03;
		public static final double kTurnI = 0.0000;
		public static final double kTurnD = 0.0;
		public static final double kTurnTolerance = 1; // TODO: this is the amount of error that is considered okay
														// because obviously we can't get perfectly to the setpoint
		public static final double kDistanceTolerance = 0.5;
		public static final double kCameraHeight = 22.5; // TODO: get height once mounted
		public static final double kCameraAngle = 25.453;// 29.8394991; //TODO: get angle once mounted
		public static final double kTargetHeight = 104; // TODO: get height of target (inches)
		public static final double kRefreshRate = 0.01111; // matches with the max of 90 frames/second from the
															// limelight
		public static final int kRollingAverageSize = 10; // TODO: change, experiment
	}

	public enum FieldLocation {

		WALL(3700, 6.5, 20, 0, 0), TWOFEET(2850, 7, 25, 0, 0), INITLINE(3500, 23, 30, 0, 0),
		CLOSETRENCH(/* 5500 */4700, /* 37.4 */33.5, 20, 0, 0), FARTWRENCH(6500, 40, 20, 0, 0);

		// for over 113, rpm 4100, angle 15
		// for under 113, rpm 3300, angle 4

		public final double flywheelSetpoint, hoodSetpoint, carouselSetpoint, distanceGoal, turnGoal;

		private FieldLocation(double flywheelSetpoint, double hoodSetpoint, double carouselSetpoint,
				double distanceGoal, double turnGoal) {
			this.flywheelSetpoint = flywheelSetpoint;
			this.hoodSetpoint = hoodSetpoint;
			this.carouselSetpoint = carouselSetpoint;
			this.distanceGoal = distanceGoal;
			this.turnGoal = turnGoal;
		}

		public static final FieldLocation fromDistance(double distance) {
			FieldLocation closestDistance = WALL;
			for (FieldLocation fieldLocation : FieldLocation.values()) {
				if (Math.abs(distance - fieldLocation.distanceGoal) < Math
						.abs(distance - closestDistance.distanceGoal)) {
					closestDistance = fieldLocation;
				}
			}
			return closestDistance;
		}

		public static final FieldLocation fromFlywheelSetpoint(double flywheelSetpoint) {
			FieldLocation closestSetpoint = WALL;
			for (FieldLocation fieldLocation : FieldLocation.values()) {
				if (Math.abs(flywheelSetpoint - fieldLocation.flywheelSetpoint) < Math
						.abs(flywheelSetpoint - closestSetpoint.flywheelSetpoint)) {
					closestSetpoint = fieldLocation;
				}
			}
			return closestSetpoint;
		}
	}

}

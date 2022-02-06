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

		// TODO change
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

		public static final double kEncoderPositionConversionFactor = (1 / DriveConstants.kGearRatio) * Math.PI
				* DriveConstants.kWheelDiameterMeters;

		public static final double kEncoderVelocityConversionFactor = (1 / DriveConstants.kGearRatio) * Math.PI
				* DriveConstants.kWheelDiameterMeters * 60;

		public static final double toBarPosition=2;//inches
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

	public static final class IndexerConstants {
		public static final int kMotorPort = 0;
		public static final boolean kInvert = false;
		public static final int kSmartCurrentLimit = 60;
		public static final double kP = 0; // TODO: tune PID
		public static final double kI = 0;
		public static final double kD = 0.0000;// 0.000_03;
		public static final double kIz = 0.0;
		public static final double kFF = .0;// 0.000_193;
		public static final double kMaxOutput = 1;
		public static final double kMinOutput = -1;
		public static final int kSlotID = 0;
		public static final double kMinVelocity = 0;
		public static final double kMaxAcel = 20_000;
		public static final double kMaxVelocity = 10_000;
		public static final double kGearRatio = 1 / 1; // TODO: change this
		public static final double kAllowedErrorPercent = .2;
		public static final double kMinPosition = 0;
		public static final int kStartProximitySensorPort = 0;
		public static final int kCenterProximitySensorPort = 1;
	}

	public static final class IntakeArmConstants {
		public static final int kMotorPort = 0;
		public static final boolean kInvert = false;
		public static final int kSmartCurrentLimit = 60;
		public static final double kP = 0; // TODO: tune PID
		public static final double kI = 0;
		public static final double kD = 0.0000;// 0.000_03;
		public static final double kIz = 0.0;
		public static final double kFF = .0;// 0.000_193;
		public static final double kMaxOutput = 1;
		public static final double kMinOutput = -1;
		public static final int kSlotID = 0;
		public static final double kMinVelocity = 0;
		public static final double kMaxAcel = 20_000;
		public static final double kMaxVelocity = 10_000;
		public static final double kGearRatio = 1 / 1; // TODO: change this
		public static final double kAllowedError = .2;
		public static final double kMinPosition = 0;
	}

	public static final class IntakeConstants {
		public static final int kMotorPort = 0;
		public static final boolean kInvert = false;
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

	public static final class SlideHookConstants {
		public static final int kMasterPort = 10;
		public static final int kFollowerPort = 20;
		public static final boolean kMasterInvert = false;
		public static final boolean kFollowerOppose = false;
		public static final int kSmartCurrentLimit = 60;// TODO: change
		public static final double kP = 0; // TODO: tune PID
		public static final double kI = 0;
		public static final double kD = 0.0000;// 0.000_03;
		public static final double kIz = 0.0;
		public static final double kFF = .0;// 0.000_193;
		public static final double kMaxOutput = 1;
		public static final double kMinOutput = -1;
		public static final int kSlotID = 0;
		public static final double kMinVelocity = 0;
		public static final double kMaxAcel = 20_000;
		public static final double kMaxVelocity = 10_000;
		public static final double kGearRatio = 1 / 1; // TODO: change this
		public static final double kAllowedError = .2;

		public static final double kInchesPerQuarterTurn = .125; // .125 inches of travel per quarter turn (unit position)
		public static final double kStartPosition = 0;// inches
		public static final double kToTelescopePosition = 2;// inches

		public static final double kTelescopeBehindRung=24;//inches
		public static final double kTelescopeTouchingRung=20;//inches

		public static final double kControlled=13;//inches

	}

	public static final class TelescopeHookConstants {
		public static final int kMasterPort = 10;
		public static final int kFollowerPort = 20;
		public static final boolean kMasterInvert = false;
		public static final boolean kFollowerOppose = false;
		public static final int kSmartCurrentLimit = 60;// TODO: change
		public static final double kP = 0; // TODO: tune PID
		public static final double kI = 0;
		public static final double kD = 0.0000;// 0.000_03;
		public static final double kIz = 0.0;
		public static final double kFF = .0;// 0.000_193;
		public static final double kMaxOutput = 1;
		public static final double kMinOutput = -1;
		public static final int kSlotID = 0;
		public static final double kMinVelocity = 0;
		public static final double kMaxAcel = 20_000;
		public static final double kMaxVelocity = 10_000;
		public static final double kGearRatio = 1 / 2.4; // TODO: change this
		public static final double kAllowedError = .2;

		public static final double kExtendedPosition = 40;// inches
		public static final double kRetractedPosition = 0;// inches
		public static final double kDisengageFromRetractedPosition = 5;// inches
		
		public static final double kControlled=35;//inches
		public static final double kDisengageFromControlledPosition = 30;// inches

	}

	public enum FieldLocation {

		TARMACHIGH(0.0, 0.0), TARMACLOW(0, 0),
		RINGHIGH(0, 0), TERMINALHIGH(0, 0);

		public final double flywheelSetpoint, hoodSetpoint;

		private FieldLocation(double flywheelSetpoint, double hoodSetpoint) {
			this.flywheelSetpoint = flywheelSetpoint;
			this.hoodSetpoint = hoodSetpoint;
		}
	}
}

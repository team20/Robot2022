package frc.robot;

public final class Constants {

    public static final class DriveConstants {
      //  public static final double kMaxRotSpeedMetersPerSecond = 0;
      //  public static final double kMaxAccelerationMetersPerSecondSquared = 0;
        public static final double kSpeedLimitFactor = 1;

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

    public static final class LimelightConstants { //TODO: tune PID loop
        public static final double kDisP = 0.02; 
        public static final double kDisI = 0;
        public static final double kDisD = 0;
        public static final double kTurnP = 0.03;
        public static final double kTurnI = 0.0000;
        public static final double kTurnD = 0.0;
        public static final double kTurnTolerance = 0.15; //TODO: this is the amount of error that is considered okay because obviously we can't get perfectly to the setpoint
        public static final double kDistanceTolerance = 0.5;
        public static final double kCameraHeight = 22.5; //TODO: get height once mounted
        public static final double kCameraAngle = 38.595540447462469140696601487777; //TODO: get angle once mounted
        public static final double kTargetHeight = 104; //TODO: get height of target (inches)
        public static final double kRefreshRate = 0.01111; //matches with the max of 90 frames/second from the limelight
        public static final int kRollingAverageSize = 10; //TODO: change, experiment
    }

    public enum FieldLocation {  
		WALL(3300, 4, 20, 0, 0), TWOFEET(2850, 7, 25, 0, 0), INITLINE(3500, 23, 30, 0, 0),
		CLOSETRENCH(/* 5500 */4700, /* 37.4 */33.5, 20, 0, 0), FARTWRENCH(6500, 40, 20, 0, 0);

		//for over 113, rpm 4100, angle 15
		//for under 113, rpm 3300, angle 4

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

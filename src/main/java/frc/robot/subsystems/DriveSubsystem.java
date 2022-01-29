package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {

        private final CANSparkMax m_frontLeft = new CANSparkMax(DriveConstants.kFrontLeftPort, MotorType.kBrushless);
        private final CANSparkMax m_frontRight = new CANSparkMax(DriveConstants.kFrontRightPort, MotorType.kBrushless);
        private final CANSparkMax m_backLeft = new CANSparkMax(DriveConstants.kBackLeftPort, MotorType.kBrushless);
        private final CANSparkMax m_backRight = new CANSparkMax(DriveConstants.kBackRightPort, MotorType.kBrushless);

        private final RelativeEncoder m_leftEncoder = m_frontLeft.getEncoder();
        private final RelativeEncoder m_rightEncoder = m_frontRight.getEncoder();
        // private final SparkMaxPIDController m_leftPIDController = m_frontLeft.getPIDController();
        // private final SparkMaxPIDController m_rightPIDController = m_frontRight.getPIDController();

        private final AHRS m_gyro = new AHRS(DriveConstants.kGyroPort);

        private final DifferentialDriveOdometry m_odometry;

        private final MotorControllerGroup m_leftMotors = new MotorControllerGroup(m_frontLeft, m_backLeft);
        private final MotorControllerGroup m_rightMotors = new MotorControllerGroup(m_frontRight, m_backRight);

        private final DifferentialDrive m_drive = new DifferentialDrive(m_leftMotors, m_rightMotors);

        public DriveSubsystem() {
                m_frontLeft.restoreFactoryDefaults();
                m_frontLeft.setInverted(DriveConstants.kFrontLeftInvert);
                m_frontLeft.setIdleMode(IdleMode.kBrake);
                m_frontLeft.enableVoltageCompensation(12);
                m_frontLeft.setSmartCurrentLimit(DriveConstants.kSmartCurrentLimit);
                m_frontLeft.setSecondaryCurrentLimit(DriveConstants.kPeakCurrentLimit,
                                DriveConstants.kPeakCurrentDurationMillis);
                m_frontLeft.setOpenLoopRampRate(DriveConstants.kRampRate);

                m_backLeft.restoreFactoryDefaults();
                m_backLeft.setIdleMode(IdleMode.kCoast);
                m_backLeft.enableVoltageCompensation(12);
                m_backLeft.setSmartCurrentLimit(DriveConstants.kSmartCurrentLimit);
                m_backLeft.setSecondaryCurrentLimit(DriveConstants.kPeakCurrentLimit,
                                DriveConstants.kPeakCurrentDurationMillis);
                m_backLeft.setOpenLoopRampRate(DriveConstants.kRampRate);
                m_backLeft.follow(m_frontLeft, DriveConstants.kBackLeftOppose);

                m_frontRight.restoreFactoryDefaults();
                m_frontRight.setInverted(DriveConstants.kFrontRightInvert);
                m_frontRight.setIdleMode(IdleMode.kBrake);
                m_frontRight.enableVoltageCompensation(12);
                m_frontRight.setSmartCurrentLimit(DriveConstants.kSmartCurrentLimit);
                m_frontRight.setSecondaryCurrentLimit(DriveConstants.kPeakCurrentLimit,
                                DriveConstants.kPeakCurrentDurationMillis);
                m_frontRight.setOpenLoopRampRate(DriveConstants.kRampRate);

                m_backRight.restoreFactoryDefaults();
                m_backRight.setIdleMode(IdleMode.kCoast);
                m_backRight.enableVoltageCompensation(12);
                m_backRight.setSmartCurrentLimit(DriveConstants.kSmartCurrentLimit);
                m_backRight.setSecondaryCurrentLimit(DriveConstants.kPeakCurrentLimit,
                                DriveConstants.kPeakCurrentDurationMillis);
                m_backRight.setOpenLoopRampRate(DriveConstants.kRampRate);
                m_backRight.follow(m_frontRight, DriveConstants.kBackRightOppose);

                m_leftEncoder.setPositionConversionFactor(
                                (1 / DriveConstants.kGearRatio) * Math.PI * DriveConstants.kWheelDiameterMeters);
                m_leftEncoder.setVelocityConversionFactor(
                                (1 / DriveConstants.kGearRatio) * Math.PI * DriveConstants.kWheelDiameterMeters / 60.0);

                m_rightEncoder.setPositionConversionFactor(
                                (1 / DriveConstants.kGearRatio) * Math.PI * DriveConstants.kWheelDiameterMeters);
                m_rightEncoder.setVelocityConversionFactor(
                                (1 / DriveConstants.kGearRatio) * Math.PI * DriveConstants.kWheelDiameterMeters / 60.0);

                // m_leftPIDController.setP(DriveConstants.kP);
                // m_leftPIDController.setI(DriveConstants.kI);
                // m_leftPIDController.setIZone(DriveConstants.kIz);
                // m_leftPIDController.setD(DriveConstants.kD);
                // m_leftPIDController.setFF(DriveConstants.kFF);
                // m_leftPIDController.setOutputRange(DriveConstants.kMinOutput, DriveConstants.kMaxOutput);
                // m_leftPIDController.setFeedbackDevice(m_leftEncoder);

                // m_rightPIDController.setP(DriveConstants.kP);
                // m_rightPIDController.setI(DriveConstants.kI);
                // m_rightPIDController.setIZone(DriveConstants.kIz);
                // m_rightPIDController.setD(DriveConstants.kD);
                // m_rightPIDController.setFF(DriveConstants.kFF);
                // m_rightPIDController.setOutputRange(DriveConstants.kMinOutput, DriveConstants.kMaxOutput);
                // m_rightPIDController.setFeedbackDevice(m_rightEncoder);

                m_odometry = new DifferentialDriveOdometry(m_gyro.getRotation2d());
                // this is what they did in 2020 with the navX:
                // Rotation2d.fromDegrees(getHeading()));
                resetEncoders();
                // from 2020: resetOdometry(new Pose2d(0, 0, new Rotation2d()));

        }

        public void periodic() {
                m_odometry.update(m_gyro.getRotation2d(), getLeftEncoderPosition(),
                                getRightEncoderPosition());
        }

        /**
         * @return The left encoder position (meters)
         */
        public double getLeftEncoderPosition() {
                return m_leftEncoder.getPosition();
        }

        /**
         * @return The right encoder position (meters)
         */
        public double getRightEncoderPosition() {
                return m_rightEncoder.getPosition();
        }

        /**
         * @return The average encoder distance of both encoders (meters)
         */
        public double getAverageEncoderDistance() {
                return (getLeftEncoderPosition() + getRightEncoderPosition()) / 2.0;
        }

        /**
         * @return The velocity of the left encoder (meters/s)
         */
        public double getLeftEncoderVelocity() {
                return m_leftEncoder.getVelocity();
        }

        /**
         * @return The velocity of the right encoder (meters/s)
         */
        public double getRightEncoderVelocity() {
                return m_rightEncoder.getVelocity();
        }

        /**
         * @return Pose of the robot
         */
        public Pose2d getPose() {
                return m_odometry.getPoseMeters();
        }

        /**
         * @return Wheel speeds of the robot
         */
        public DifferentialDriveWheelSpeeds getWheelSpeeds() {
                return new DifferentialDriveWheelSpeeds(getLeftEncoderVelocity(), getRightEncoderVelocity());
        }

        /**
         * @return The heading of the gyro (degrees)
         */
        public double getHeading() {
                return m_gyro.getYaw() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
        }


        /**
         * @return The rate of the gyro turn (deg/s)
         */
        public double getTurnRate() {
                return m_gyro.getRate() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
        }

        /**
         * Resets gyro position to 0
         */
        public void zeroHeading() {
                m_gyro.reset();
        }

        /**
         * Sets both encoders to 0
         */
        public void resetEncoders() {
                m_leftEncoder.setPosition(0);
                m_rightEncoder.setPosition(0);
        }

        /**
         * @param pose Pose to set the robot to
         */
        public void resetOdometry(Pose2d pose) {
                resetEncoders();
                m_odometry.resetPosition(pose, m_gyro.getRotation2d());
        }

        /**
         * Drives the robot using arcade controls.
         *
         * @param fwd the commanded forward movement
         * @param rot the commanded rotation
         */
        public void arcadeDrive(double fwd, double rot) {
                m_drive.arcadeDrive(fwd, rot);
        }

        /**
         * Controls the left and right sides of the drive directly with voltages.
         *
         * @param leftVolts  the commanded left output
         * @param rightVolts the commanded right output
         */
        public void tankDriveVolts(double leftVolts, double rightVolts) {
                m_leftMotors.setVoltage(leftVolts);
                m_rightMotors.setVoltage(rightVolts);
                m_drive.feed();
        }

        /**
         * Sets the max output of the drive. Useful for scaling the drive to drive more
         * slowly.
         *
         * @param maxOutput the maximum output to which the drive will be constrained
         */
        public void setMaxOutput(double maxOutput) {
                m_drive.setMaxOutput(maxOutput);
        }

        public void configureShuffleboard() {
                ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Drive");
                shuffleboardTab.addNumber("Left speed", () -> getWheelSpeeds().leftMetersPerSecond).withSize(4, 2)
                        .withPosition(0, 0).withWidget(BuiltInWidgets.kGraph);
                shuffleboardTab.addNumber("Right speed", () -> getWheelSpeeds().rightMetersPerSecond).withSize(4, 2)
                        .withPosition(4, 0).withWidget(BuiltInWidgets.kGraph);
                shuffleboardTab.addNumber("Left motor speed", () -> getLeftEncoderPosition()).withSize(1, 1)
                        .withPosition(0, 2).withWidget(BuiltInWidgets.kTextView);
                shuffleboardTab.addNumber("Right motor speed", () -> getRightEncoderPosition()).withSize(1, 1)
                        .withPosition(1, 2).withWidget(BuiltInWidgets.kTextView);
                shuffleboardTab.addNumber("Heading", () -> getHeading()).withSize(1, 1).withPosition(2, 2)
                        .withWidget(BuiltInWidgets.kTextView);

}

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController.AccelStrategy;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeArmConstants;
import frc.robot.util.ShuffleboardLogging;

public class IntakeArmSubsystem extends SubsystemBase implements ShuffleboardLogging {

    private final CANSparkMax m_motor = new CANSparkMax(IntakeArmConstants.kMotorPort, MotorType.kBrushless);
    private final RelativeEncoder m_encoder = m_motor.getEncoder();
    private final SparkMaxPIDController m_pidController = m_motor.getPIDController();
    //private final DigitalInput m_bumpSwitch = new DigitalInput(4); // IntakeArmConstants.kBumpSwitchPort
    private double m_setPosition = 0;

    public enum Position {
        DOWN_POSITION,
        UP_POSITION
    }

    private final double downPositionEncoderPosition = 36.75; // TODO find encoder position
    private final double upPositionEncoderPosition = 0; // TODO find encoder position

    private static IntakeArmSubsystem s_system;

    public static IntakeArmSubsystem get() {
        return s_system;
    }

    /**
     * Initializes a new instance of the {@link ArmSubsystem} class.
     */
    public IntakeArmSubsystem() {
        s_system = this;
        m_motor.restoreFactoryDefaults();
        m_motor.setInverted(IntakeArmConstants.kInvert);
        m_motor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        m_motor.enableVoltageCompensation(12);
        m_motor.setSmartCurrentLimit(IntakeArmConstants.kSmartCurrentLimit);
        // m_motor.setSecondaryCurrentLimit(IntakeArmConstants.kPeakCurrentLimit,  IntakeArmConstants.kPeakCurrentDurationMillis);

        m_pidController.setP(IntakeArmConstants.kP);
        m_pidController.setI(IntakeArmConstants.kI);
        m_pidController.setIZone(IntakeArmConstants.kIz);
        m_pidController.setD(IntakeArmConstants.kD);
        m_pidController.setFF(IntakeArmConstants.kFF);
        m_pidController.setOutputRange(IntakeArmConstants.kMinOutput, IntakeArmConstants.kMaxOutput);

        m_pidController.setSmartMotionAccelStrategy(AccelStrategy.kTrapezoidal, IntakeArmConstants.kSlotID);
        m_pidController.setSmartMotionMaxAccel(IntakeArmConstants.kMaxAcel, IntakeArmConstants.kSlotID);
        m_pidController.setSmartMotionMaxVelocity(IntakeArmConstants.kMaxVelocity, IntakeArmConstants.kSlotID);
        m_pidController.setSmartMotionAllowedClosedLoopError(IntakeArmConstants.kAllowedError,
                IntakeArmConstants.kSlotID);
        m_pidController.setSmartMotionMinOutputVelocity(IntakeArmConstants.kMinVelocity, IntakeArmConstants.kSlotID);

        resetEncoder();
    }

    public void periodic() {
        //SmartDashboard.putNumber("Arm Position", getPosition());
        // double currCurrent = m_motor.getOutputCurrent();
        // if (currCurrent > 40){
        //     m_pidController.setReference(m_encoder.getPosition(), ControlType.kPosition, 0);
        //     m_motor.stopMotor();
        // }else 
        // if (atSetpoint()) {
        //     m_pidController.setReference(m_encoder.getPosition(), ControlType.kPosition, 0);
        //     m_motor.stopMotor();
        // } 
        if(atSetpoint() && Math.abs(m_encoder.getVelocity()) > 0.05){
            // m_pidController.setReference(m_encoder.getPosition(), ControlType.kPosition, 0);
            //m_motor.stopMotor();
        }
    }

    /**
     * @return Current arm position (motor rotations)
     */
    public double getPosition() {
        return m_encoder.getPosition();
    }
    /**
     * @return Current velocity (motor rotations/s)
     */
    public double getVelocity() {
        return m_encoder.getVelocity();
    }

    /**
     * @return Whether the arm is at the setpoint
     */
    public boolean atSetpoint() {
        return (Math.abs(m_setPosition - getPosition()) <= IntakeArmConstants.kAllowedError);
    }

    /**
     * @param speed Percent output of the arm
     */
    public void setPercentOutput(double speed) {
        // if (speed < 0 && getPosition() < IntakeArmConstants.kMinPosition)
        //     m_motor.set(0);
        // else
            m_motor.set(speed);
    }

    /**
     * @param position Setpoint (motor rotations)
     */
    public void setPosition(double position) {
        m_setPosition = position;
        //System.out.println("setPosition:" + position);
        m_pidController.setReference(position, ControlType.kPosition, IntakeArmConstants.kSlotID);
    }

    /**
     * @param position Setpoint (position)
     */
    public void setPosition(Position position) {
        if (position == Position.DOWN_POSITION) {
            m_setPosition = downPositionEncoderPosition;
        } else {
            m_setPosition = upPositionEncoderPosition;
            //System.out.println("JJJJJJJJJ subsystem - val is "+m_setPosition);
        }
        m_pidController.setReference(m_setPosition, ControlType.kPosition, IntakeArmConstants.kSlotID);
    }

    public boolean armDown() {
        if (m_setPosition == downPositionEncoderPosition) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Zero the encoder position
     */
    public void resetEncoder() {
        m_encoder.setPosition(0);
        setPosition(0);
    }

    public void setBrakeMode() {
        m_motor.setIdleMode(CANSparkMax.IdleMode.kBrake);
    }

    public void setCoastMode() {
        m_motor.setIdleMode(CANSparkMax.IdleMode.kCoast);
    }

    public void zeroTheArm() {
        // while (!m_bumpSwitch.get()) {
        //     m_motor.set(0.2); // TODO might need to flip this the other way
        // }
        m_encoder.setPosition(0);
        setPosition(0);
    }

    public void configureShuffleboard(boolean inCompetitionMode) {
        if (!inCompetitionMode) {
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Arm");
        shuffleboardTab.addNumber("Encoder Position", () -> getPosition()).withSize(4, 2).withPosition(0, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addNumber("Encoder Velocity", () -> getVelocity()).withSize(4, 2).withPosition(4, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addBoolean("At setpoint", () -> atSetpoint()).withSize(1, 1).withPosition(0, 2)
                .withWidget(BuiltInWidgets.kBooleanBox);
    }
}
}
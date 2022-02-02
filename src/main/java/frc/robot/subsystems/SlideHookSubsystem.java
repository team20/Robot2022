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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.SlideHookConstants;
import frc.robot.ShuffleboardLogging;

public class SlideHookSubsystem extends SubsystemBase implements ShuffleboardLogging {

    private final CANSparkMax m_motor = new CANSparkMax(SlideHookConstants.kMotorPort, MotorType.kBrushless);
    private final RelativeEncoder m_encoder = m_motor.getEncoder();
    private final SparkMaxPIDController m_pidController = m_motor.getPIDController();
    private double m_setPosition = 0;

    /**
     * Initializes a new instance of the {@link SlideHookSubsystem} class.
     */
    public SlideHookSubsystem() {
        m_motor.restoreFactoryDefaults();
        m_motor.setInverted(SlideHookConstants.kInvert);
        m_motor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        m_motor.enableVoltageCompensation(12);
        m_motor.setSmartCurrentLimit(SlideHookConstants.kSmartCurrentLimit);

        m_pidController.setP(SlideHookConstants.kP);
        m_pidController.setI(SlideHookConstants.kI);
        m_pidController.setIZone(SlideHookConstants.kIz);
        m_pidController.setD(SlideHookConstants.kD);
        m_pidController.setFF(SlideHookConstants.kFF);
        m_pidController.setOutputRange(SlideHookConstants.kMinOutput, SlideHookConstants.kMaxOutput);

        m_pidController.setSmartMotionAccelStrategy(AccelStrategy.kTrapezoidal, SlideHookConstants.kSlotID);
        m_pidController.setSmartMotionMaxAccel(SlideHookConstants.kMaxAcel, SlideHookConstants.kSlotID);
        m_pidController.setSmartMotionMaxVelocity(SlideHookConstants.kMaxVelocity, SlideHookConstants.kSlotID);
        m_pidController.setSmartMotionAllowedClosedLoopError(SlideHookConstants.kAllowedError, SlideHookConstants.kSlotID);
        m_pidController.setSmartMotionMinOutputVelocity(SlideHookConstants.kMinVelocity, SlideHookConstants.kSlotID);

        resetEncoder();
    }

    public void periodic() {
        SmartDashboard.putNumber("Slide Hook Position", getPosition());
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
        return (Math.abs(m_setPosition - getPosition()) <= SlideHookConstants.kAllowedError);
    }

    /**
     * @param speed Percent output of the hook
     */
    public void setPercentOutput(double speed) {
        if (speed < 0 && getPosition() < SlideHookConstants.kRetractedPosition)
            m_motor.set(0);
        else
            m_motor.set(speed);
    }

    /**
     * @param position Setpoint (motor rotations)
     */
    public void setPosition(double position) {
        m_setPosition = position;
        m_pidController.setReference(position, ControlType.kSmartMotion, SlideHookConstants.kSlotID);
    }

    /**
     * Zero the encoder position
     */
    public void resetEncoder() {
        m_encoder.setPosition(0);
        setPosition(0);
    }

    public void configureShuffleboard() {
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Slide Hook");
        shuffleboardTab.addNumber("Encoder Position", () -> getPosition()).withSize(4, 2).withPosition(0, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addNumber("Encoder Velocity", () -> getVelocity()).withSize(4, 2).withPosition(4, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addBoolean("At setpoint", () -> atSetpoint()).withSize(1, 1).withPosition(0, 2)
                .withWidget(BuiltInWidgets.kBooleanBox);
    }
}
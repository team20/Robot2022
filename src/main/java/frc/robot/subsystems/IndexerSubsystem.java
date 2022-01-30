package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IndexerConstants;
import frc.robot.ShuffleboardLogging;

public class IndexerSubsystem extends SubsystemBase implements ShuffleboardLogging {

    private final CANSparkMax m_motor = new CANSparkMax(IndexerConstants.kMotorPort, MotorType.kBrushless);
    private final RelativeEncoder m_encoder = m_motor.getEncoder();
    private final SparkMaxPIDController m_pidController = m_motor.getPIDController();
    private double m_setPosition = 0;

    /**
     * Initializes a new instance of the {@link ArmSubsystem} class.
     */
    public IndexerSubsystem() {
        m_motor.restoreFactoryDefaults();
        m_motor.setInverted(IndexerConstants.kInvert);
        m_motor.setIdleMode(CANSparkMax.IdleMode.kCoast); //TODO coast or brake??
        m_motor.enableVoltageCompensation(12);
        m_motor.setSmartCurrentLimit(IndexerConstants.kSmartCurrentLimit);

        m_pidController.setP(IndexerConstants.kP);
        m_pidController.setI(IndexerConstants.kI);
        m_pidController.setIZone(IndexerConstants.kIz);
        m_pidController.setD(IndexerConstants.kD);
        m_pidController.setFF(IndexerConstants.kFF);
        m_pidController.setOutputRange(IndexerConstants.kMinOutput, IndexerConstants.kMaxOutput);

        // m_pidController.setSmartMotionAccelStrategy(IndexerConstants.kTrapezoidal, IndexerConstants.kSlotID);
        // m_pidController.setSmartMotionMaxAccel(IndexerConstants.kMaxAcel, IndexerConstants.kSlotID);
        // m_pidController.setSmartMotionMaxVelocity(IndexerConstants.kMaxVelocity, IndexerConstants.kSlotID);
        // m_pidController.setSmartMotionAllowedClosedLoopError(IndexerConstants.kAllowedError, IndexerConstants.kSlotID);
        // m_pidController.setSmartMotionMinOutputVelocity(IndexerConstants.kMinVelocity, IndexerConstants.kSlotID);

        resetEncoder();
    }

    public void periodic() {
        SmartDashboard.putNumber("Arm Position", getPosition());
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
        return (Math.abs(m_setPosition - getPosition()) <= IndexerConstants.kAllowedError);
    }

    /**
     * @param speed Percent output of the arm
     */
    public void setSpeed(double speed) {
        m_motor.set(speed);
    }

    /**
     * @param position Setpoint (motor rotations)
     */
    public void setPosition(double position) {
        m_setPosition = position;
        m_pidController.setReference(position, ControlType.kPosition, IndexerConstants.kSlotID);
    }

    /**
     * Zero the encoder position
     */
    public void resetEncoder() {
        m_encoder.setPosition(0);
        setPosition(0);
    }

    public void configureShuffleboard() {
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Arm");
        shuffleboardTab.addNumber("Encoder Position", () -> getPosition()).withSize(4, 2).withPosition(0, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addNumber("Encoder Velocity", () -> getVelocity()).withSize(4, 2).withPosition(4, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addBoolean("At setpoint", () -> atSetpoint()).withSize(1, 1).withPosition(0, 2)
                .withWidget(BuiltInWidgets.kBooleanBox);
    }
}
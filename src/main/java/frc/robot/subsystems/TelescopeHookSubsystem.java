package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
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
import frc.robot.Constants.TelescopeHookConstants;
import frc.robot.ShuffleboardLogging;

public class TelescopeHookSubsystem extends SubsystemBase implements ShuffleboardLogging {

    private final CANSparkMax m_masterMotor = new CANSparkMax(TelescopeHookConstants.kMasterPort, MotorType.kBrushless);
    private final CANSparkMax m_followerMotor = new CANSparkMax(TelescopeHookConstants.kFollowerPort, MotorType.kBrushless);

    private final RelativeEncoder m_encoder = m_masterMotor.getEncoder();
    private final SparkMaxPIDController m_pidController = m_masterMotor.getPIDController();
    private double m_setPosition = 0;

    /**
     * Initializes a new instance of the {@link TelescopeHookSubsystem} class.
     */
    public TelescopeHookSubsystem() {
        m_masterMotor.restoreFactoryDefaults();
        m_masterMotor.setInverted(TelescopeHookConstants.kMasterInvert);
        m_masterMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        m_masterMotor.enableVoltageCompensation(12);
        m_masterMotor.setSmartCurrentLimit(TelescopeHookConstants.kSmartCurrentLimit);

        m_followerMotor.restoreFactoryDefaults();
        m_followerMotor.setInverted(TelescopeHookConstants.kMasterInvert);
        m_followerMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        m_followerMotor.enableVoltageCompensation(12);
        m_followerMotor.setSmartCurrentLimit(TelescopeHookConstants.kSmartCurrentLimit);
        m_followerMotor.follow(m_masterMotor, TelescopeHookConstants.kFollowerOppose);

        m_pidController.setP(TelescopeHookConstants.kP);
        m_pidController.setI(TelescopeHookConstants.kI);
        m_pidController.setIZone(TelescopeHookConstants.kIz);
        m_pidController.setD(TelescopeHookConstants.kD);
        m_pidController.setFF(TelescopeHookConstants.kFF);
        m_pidController.setOutputRange(TelescopeHookConstants.kMinOutput, TelescopeHookConstants.kMaxOutput);

        m_pidController.setSmartMotionAccelStrategy(AccelStrategy.kTrapezoidal, TelescopeHookConstants.kSlotID);
        m_pidController.setSmartMotionMaxAccel(TelescopeHookConstants.kMaxAcel, TelescopeHookConstants.kSlotID);
        m_pidController.setSmartMotionMaxVelocity(TelescopeHookConstants.kMaxVelocity, TelescopeHookConstants.kSlotID);
        m_pidController.setSmartMotionAllowedClosedLoopError(TelescopeHookConstants.kAllowedError, TelescopeHookConstants.kSlotID);
        m_pidController.setSmartMotionMinOutputVelocity(TelescopeHookConstants.kMinVelocity, TelescopeHookConstants.kSlotID);

        resetEncoder();
    }

    public void periodic() {
        SmartDashboard.putNumber("Telescope Hook Position", getPosition());
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
        return (Math.abs(m_setPosition - getPosition()) <= TelescopeHookConstants.kAllowedError);
    }

    /**
     * @param speed Percent output of the hook
     */
    public void setPercentOutput(double speed) {
        if (speed < 0 && getPosition() < TelescopeHookConstants.kRetractedPosition)
            m_masterMotor.set(0);
        else
            m_masterMotor.set(speed);
    }

    /**
     * @param position Setpoint (motor rotations)
     */
    public void setPosition(double position) {
        m_setPosition = position;
        m_pidController.setReference(position, ControlType.kPosition, TelescopeHookConstants.kSlotID);
    }

    /**
     * Zero the encoder position
     */
    public void resetEncoder() {
        m_encoder.setPosition(0);
        setPosition(0);
    }

    /**
     * 
     * @param speed Percentage output of telescope hook motor
     */
    public void setSpeed(double speed){
        m_masterMotor.set(speed);
    }
    public double getOutputCurrent(){
        return m_masterMotor.getOutputCurrent();
    }

    public void configureShuffleboard() {
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Telescope Hook");
        shuffleboardTab.addNumber("Encoder Position", () -> getPosition()).withSize(4, 2).withPosition(0, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addNumber("Encoder Velocity", () -> getVelocity()).withSize(4, 2).withPosition(4, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addBoolean("At setpoint", () -> atSetpoint()).withSize(1, 1).withPosition(0, 2)
                .withWidget(BuiltInWidgets.kBooleanBox);
    }
}
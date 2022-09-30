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
import frc.robot.Constants.TelescopeHookConstants;
import frc.robot.util.ShuffleboardLogging;

public class TelescopeHookSubsystem extends SubsystemBase implements ShuffleboardLogging {
    private static TelescopeHookSubsystem s_system;
    public static TelescopeHookSubsystem get() { return s_system; }

    private final CANSparkMax m_leftMotor = new CANSparkMax(TelescopeHookConstants.kLeftPort, MotorType.kBrushless);
    private final CANSparkMax m_rightMotor = new CANSparkMax(TelescopeHookConstants.kRightPort, MotorType.kBrushless);

    private final RelativeEncoder m_leftEncoder = m_leftMotor.getEncoder();
    private final SparkMaxPIDController m_leftPidController = m_leftMotor.getPIDController();

    private final RelativeEncoder m_rightEncoder = m_rightMotor.getEncoder();
    private final SparkMaxPIDController m_rightPidController = m_rightMotor.getPIDController();
    private double m_setPosition = 0;

    /**
     * Initializes a new instance of the {@link TelescopeHookSubsystem} class.
     */
    public TelescopeHookSubsystem() {
        s_system = this;

        m_leftMotor.restoreFactoryDefaults();
        m_leftMotor.setInverted(TelescopeHookConstants.kLeftInvert);
        m_leftMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        m_leftMotor.enableVoltageCompensation(12);
        m_leftMotor.setSmartCurrentLimit(TelescopeHookConstants.kSmartCurrentLimit);

        m_rightMotor.restoreFactoryDefaults();
        m_rightMotor.setInverted(TelescopeHookConstants.kRightInvert);
        m_rightMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        m_rightMotor.enableVoltageCompensation(12);
        m_rightMotor.setSmartCurrentLimit(TelescopeHookConstants.kSmartCurrentLimit);

        m_leftPidController.setP(TelescopeHookConstants.kP);
        m_leftPidController.setI(TelescopeHookConstants.kI);
        m_leftPidController.setIZone(TelescopeHookConstants.kIz);
        m_leftPidController.setD(TelescopeHookConstants.kD);
        m_leftPidController.setFF(TelescopeHookConstants.kFF);
        m_leftPidController.setOutputRange(TelescopeHookConstants.kMinOutput, TelescopeHookConstants.kMaxOutput);

        m_rightPidController.setP(TelescopeHookConstants.kP);
        m_rightPidController.setI(TelescopeHookConstants.kI);
        m_rightPidController.setIZone(TelescopeHookConstants.kIz);
        m_rightPidController.setD(TelescopeHookConstants.kD);
        m_rightPidController.setFF(TelescopeHookConstants.kFF);
        m_rightPidController.setOutputRange(TelescopeHookConstants.kMinOutput, TelescopeHookConstants.kMaxOutput);

        resetEncoder();
    }

    public void periodic() {
        SmartDashboard.putNumber("Left Telescope Hook Position", getleftPosition());
        SmartDashboard.putNumber("Right Telescope Hook Position", getrightPosition());
    }

    /**
     * @return Current arm position (motor rotations)
     */
    public double getleftPosition() {
        return m_leftEncoder.getPosition();
    }

    /**
     * @return Current arm position (motor rotations)
     */
    public double getrightPosition() {
        return m_rightEncoder.getPosition();
    }

    /**
     * @return Current velocity (motor rotations/s)
     */
    public double getleftVelocity() {
        return m_leftEncoder.getVelocity();
    }

    public double getrightVelocity() {
        return m_rightEncoder.getVelocity();
    }
    /**
     * @return Whether the arm is at the setpoint
     */
    public boolean atleftSetpoint() {
        return (Math.abs(m_setPosition - getleftPosition()) <= TelescopeHookConstants.kAllowedError);
    }

    public boolean atrightSetpoint() {
        return (Math.abs(m_setPosition - getrightPosition()) <= TelescopeHookConstants.kAllowedError);
    }
    /**
     * @param speed Percent output of the hook
     */
    public void setleftPercentOutput(double speed) {
        if (speed < 0 && getleftPosition() < TelescopeHookConstants.kRetractedPosition)
            m_leftMotor.set(0);
        else
            m_leftMotor.set(speed);
    }

    /**
     * @param position Setpoint (motor rotations)
     */
    public void setPosition(double position) {
        m_setPosition = position;
        m_leftPidController.setReference(position, ControlType.kPosition, TelescopeHookConstants.kSlotID);
        m_rightPidController.setReference(position, ControlType.kPosition, TelescopeHookConstants.kSlotID);
    }

    /**
     * Zero the encoder position
     */
    public void resetEncoder() {
        m_leftEncoder.setPosition(0);
        m_rightEncoder.setPosition(0);
        setPosition(0);
    }
    /**
     * 
     * @param speed Percentage output of telescope hook motor
     */
    public void setSpeed(double speed){
        m_leftMotor.set(speed);
        m_rightMotor.set(speed);
    }
    
    public double getOutputCurrent(){
        return m_leftMotor.getOutputCurrent();
    }

    public void configureShuffleboard(boolean inCompetitionMode) {
        if (!inCompetitionMode) {
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Telescope Hook");
        shuffleboardTab.addNumber("Encoder Position", () -> getleftPosition()).withSize(4, 2).withPosition(0, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addNumber("Encoder Velocity", () -> getleftVelocity()).withSize(4, 2).withPosition(4, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addBoolean("At setpoint", () -> atleftSetpoint()).withSize(1, 1).withPosition(0, 2)
                .withWidget(BuiltInWidgets.kBooleanBox);
    }
}
}
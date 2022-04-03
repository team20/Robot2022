package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;
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
import frc.robot.Constants.DriveConstants;
import frc.robot.ShuffleboardLogging;

public class SlideHookSubsystem extends SubsystemBase implements ShuffleboardLogging {

    private final CANSparkMax m_masterMotor = new CANSparkMax(SlideHookConstants.kMasterPort, MotorType.kBrushless);
    private final CANSparkMax m_followerMotor = new CANSparkMax(SlideHookConstants.kFollowerPort, MotorType.kBrushless);

    private final AHRS m_gyro = new AHRS(DriveConstants.kGyroPort);

    private final RelativeEncoder m_encoder = m_masterMotor.getEncoder();
    private final SparkMaxPIDController m_pidController = m_masterMotor.getPIDController();
    private double m_setPosition = 0;

    private static SlideHookSubsystem s_system;
    public static SlideHookSubsystem get() { return s_system; }
    /**
     * Initializes a new instance of the {@link SlideHookSubsystem} class.
     */
    public SlideHookSubsystem() {
        s_system = this;
        m_masterMotor.restoreFactoryDefaults();
        m_masterMotor.setInverted(SlideHookConstants.kMasterInvert);
        m_masterMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        m_masterMotor.enableVoltageCompensation(12);
        m_masterMotor.setSmartCurrentLimit(SlideHookConstants.kSmartCurrentLimit);

        m_followerMotor.restoreFactoryDefaults();
        m_followerMotor.setInverted(SlideHookConstants.kMasterInvert);
        m_followerMotor.setIdleMode(CANSparkMax.IdleMode.kBrake);
        m_followerMotor.enableVoltageCompensation(12);
        m_followerMotor.setSmartCurrentLimit(SlideHookConstants.kSmartCurrentLimit);
        m_followerMotor.follow(m_masterMotor, SlideHookConstants.kFollowerOppose);
        
        m_pidController.setP(SlideHookConstants.kP);
        m_pidController.setI(SlideHookConstants.kI);
        m_pidController.setIZone(SlideHookConstants.kIz);
        m_pidController.setD(SlideHookConstants.kD);
        m_pidController.setFF(SlideHookConstants.kFF);
        // m_pidController.setOutputRange(SlideHookConstants.kMinOutput, SlideHookConstants.kMaxOutput);

        resetEncoder();
    }

    public void periodic() {
        //SmartDashboard.putNumber("Slide Hook Position", getPosition());
        // System.out.println("Slide Hook Position is "+getPosition());
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
     * @return The heading of the gyro (degrees)
     */
    public double getHeading() {
        return m_gyro.getYaw() * (DriveConstants.kGyroReversed ? -1.0 : 1.0);
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
        if (speed < 0 && getPosition() < SlideHookConstants.kStartPosition)
            m_masterMotor.set(0);
        else
            m_masterMotor.set(speed);
    }

    /**
     * @param position Setpoint (motor rotations)
     */
    public void setPosition(double position) {
        m_setPosition = position;
        m_pidController.setReference(position, ControlType.kPosition, SlideHookConstants.kSlotID);
    }
    public void incrementPosition(){
        m_pidController.setReference(m_encoder.getPosition()+.5, ControlType.kPosition, SlideHookConstants.kSlotID);
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
     * @param speed Percentage output of slide hook motor
     */
    public void setSpeed(double speed){
      m_masterMotor.set(speed);
  }

    public void configureShuffleboard(boolean inCompetitionMode) {
        if (!inCompetitionMode) {
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Slide Hook");
        shuffleboardTab.addNumber("Encoder Position", () -> getPosition()).withSize(4, 2).withPosition(0, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addNumber("Encoder Velocity", () -> getVelocity()).withSize(4, 2).withPosition(4, 0)
                .withWidget(BuiltInWidgets.kGraph);
        shuffleboardTab.addBoolean("At setpoint", () -> atSetpoint()).withSize(1, 1).withPosition(0, 2)
                .withWidget(BuiltInWidgets.kBooleanBox);
    }
}
}
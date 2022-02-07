package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.robot.ShuffleboardLogging;
import frc.robot.Constants.IndexerConstants;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IndexerSubsystem extends SubsystemBase implements ShuffleboardLogging{
    // private final VictorSPX m_motor = new VictorSPX(Constants.IndexerConstants.kMotorPort);
	private final CANSparkMax m_motor=new CANSparkMax(IndexerConstants.kMotorPort, MotorType.kBrushless);
	private final SparkMaxPIDController m_neoController=m_motor.getPIDController();
	private final RelativeEncoder m_neoEncoder=m_motor.getEncoder();
	private double m_setPosition;
    private I2C.Port i2cPort;
    private ColorSensorV3 m_colorSensor;
    private ColorMatch m_colorMatcher;
    private Color kBlueTarget;
    private Color kRedTarget;
    private Color m_colorSensed;
    private String m_colorString = "\0";
    private ColorMatchResult m_match;
    private double m_colorSensorProximity;

    private DigitalInput m_proximitySensorStart;
    private DigitalInput m_proximitySensorCenter;

    private boolean m_proximitySensorStartState;
    private boolean m_proximitySensorCenterState;

    private byte m_stateSet;
    
    public IndexerSubsystem() {
		// m_motor.setNeutralMode(NeutralMode.Coast);
		// m_motor.enableVoltageCompensation(true);
		// m_motor.setInverted(true);
		m_motor.setIdleMode(IdleMode.kBrake);
		m_motor.enableVoltageCompensation(12);
		m_motor.setInverted(false);

		m_neoController.setP(IndexerConstants.kP);
        m_neoController.setI(IndexerConstants.kI);
        m_neoController.setD(IndexerConstants.kD);
        m_neoController.setIZone(IndexerConstants.kIz);
        m_neoController.setFF(IndexerConstants.kFF);
        m_neoController.setOutputRange(IndexerConstants.kMinOutput,IndexerConstants.kMaxOutput);
        
        i2cPort = I2C.Port.kMXP; //52

        m_colorSensor = new ColorSensorV3(i2cPort);
        m_colorMatcher = new ColorMatch();

        kBlueTarget = Color.kFirstBlue;
        kRedTarget = Color.kFirstRed;
        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kRedTarget);

        m_proximitySensorStart = new DigitalInput(IndexerConstants.kStartProximitySensorPort);
        m_proximitySensorCenter = new DigitalInput(IndexerConstants.kCenterProximitySensorPort);

	}
	
	public void periodic(){
        updateSensors();
		if (m_setPosition == 0) {
            m_motor.stopMotor();
        } else {
            m_neoController.setReference(m_setPosition, ControlType.kPosition, 0);
        }
	}
    public void updateSensors(){
        m_colorSensorProximity = m_colorSensor.getProximity();
        m_colorSensed = m_colorSensor.getColor(); // get the color seen by sensor

        m_match = m_colorMatcher.matchClosestColor(m_colorSensed);
        if(m_colorSensorProximity < 100){
            m_colorString = "Null";
        }else if(m_match.color == kBlueTarget){
            m_colorString = "Blue";
        }else if(m_match.color == kRedTarget){
            m_colorString = "Red";
        }
        m_proximitySensorStartState = !m_proximitySensorStart.get();
        m_proximitySensorCenterState = !m_proximitySensorCenter.get();
    }

    public void incrementPosition() {
        setPosition(m_setPosition + 50);
    }

    public void decrementPosition() {
        setPosition(m_setPosition - 50);
    }

    /**
     * @return Current setpoint.
     */
    public double getSetpoint() {
        return m_setPosition;
    }

    /**
     * @return Measured velocity.
     */
    public double getPosition() {
        return m_neoEncoder.getPosition();
    }

    /**
     * Sets target speed for flywheel.
     * 
     * @param velocity Target velocity (rpm).
     */
    public void setPosition(double position) {
        m_setPosition = position;
    }
    public void setSpeed(double speed){
        m_motor.set(speed);
    }
    /**
     * @return Whether the flywheel is at its setpoint ABOVE 0
     */
    public boolean atSetpoint() {
        return getSetpoint() > 0
                ? (Math.abs(getPosition() - getSetpoint()) / getSetpoint())
                        * 100 < IndexerConstants.kAllowedErrorPercent
                : false;
    }
    public void reset(){
        m_neoEncoder.setPosition(0);
    }

	public boolean gamePieceRTF(){
        return m_proximitySensorStartState;
    }
    public boolean gamePieceAtCenter(){
        return m_proximitySensorCenterState;
    }
    public boolean gamePieceRTS(){
        if(m_colorSensorProximity > 100){
            return true;
        }
        return false;
    }

    public boolean atSetState(){
        return m_stateSet == getCurrState();
    }
    public byte getCurrState(){
        return (byte)((byte)(gamePieceRTF()?1<<2:0) + (byte)(gamePieceAtCenter()?1<<1:0) + (byte)(gamePieceRTS()?1:0)); 
    }
    public void setState(byte state){
        m_stateSet = state;
    }
    public void configureShuffleboard(){

    }
}

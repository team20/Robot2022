package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import frc.robot.ShuffleboardLogging;
import frc.robot.Constants.IndexerConstants;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
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

    private byte m_targetState;
    
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

        //get updated sensor states so we always know where balls are and what color the ball that is ready to shoot is
        updateSensors();
		
	}
    public void updateSensors(){
        
        //get the proximity sensed by the color sensor
        m_colorSensorProximity = m_colorSensor.getProximity();
        
        // get the color seen by sensor
        m_colorSensed = m_colorSensor.getColor();

        //find the closest match for the color of the ball, will return null if nothing is there
        m_match = m_colorMatcher.matchClosestColor(m_colorSensed);
        if(m_colorSensorProximity < 100){
            m_colorString = "Null";
        }else if(m_match.color == kBlueTarget){
            m_colorString = "Blue";
        }else if(m_match.color == kRedTarget){
            m_colorString = "Red";
        }

        //save if either the center or start proximity sensors sense a ball
        m_proximitySensorStartState = !m_proximitySensorStart.get();
        m_proximitySensorCenterState = !m_proximitySensorCenter.get();
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

    public void setSpeed(double speed){
        m_motor.set(speed);
    }
    
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

    /**
     * Am I at where I want to be?
     * @return
     */
    public boolean atTargetState(){
        return m_targetState == getCurrStateSubsystem();
    }

    /**
     * 
     * @return return closest position to where I am without going above
     */
    public byte getCurrTargetState(){
        return m_targetState;
    }

    /**
     * Returns sensor values as bytes **FOR SUBSYSTEM ONLY**
     * @return sensor values
     */
    private byte getCurrStateSubsystem(){
        return (byte)((byte)(gamePieceRTF()?1<<2:0) + (byte)(gamePieceAtCenter()?1<<1:0) + (byte)(gamePieceRTS()?1:0)); 
    }

    /**
     * Set target sensor state **Will not move or stop on its own, you must also set speed and stop when it's finished** 
     * @param state target sensor state
     */
    public void setTargetState(byte state){
        m_targetState = state;
    }
    public String getColorString(){
        return m_colorString;
    }
    public void configureShuffleboard(){
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Color");
        shuffleboardTab.addBoolean("isBlue", () -> (m_colorString.equals("Blue"))).withSize(2, 2).withPosition(0, 0).withWidget(BuiltInWidgets.kBooleanBox);
        shuffleboardTab.addBoolean("isRed", () -> (m_colorString.equals("Red"))).withSize(2, 2).withPosition(2, 0).withWidget(BuiltInWidgets.kBooleanBox);
    }
}

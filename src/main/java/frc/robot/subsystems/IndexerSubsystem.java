package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
// import com.revrobotics.ColorMatch;
// import com.revrobotics.ColorMatchResult;
// import com.revrobotics.ColorSensorV3;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import frc.robot.ShuffleboardLogging;
import frc.robot.Constants.IndexerConstants;
import edu.wpi.first.wpilibj.DigitalInput;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IndexerSubsystem extends SubsystemBase implements ShuffleboardLogging{
    // private final VictorSPX m_motor = new VictorSPX(Constants.IndexerConstants.kMotorPort);
	private final CANSparkMax m_motor=new CANSparkMax(IndexerConstants.kMotorPort, MotorType.kBrushless);
	private final SparkMaxPIDController m_neoController=m_motor.getPIDController();
	private final RelativeEncoder m_neoEncoder=m_motor.getEncoder();
	private double m_setPosition;
    //private I2C.Port i2cPort;
    //private ColorSensorV3 m_colorSensor;
    //private ColorMatch m_colorMatcher;
    //private static final Color kBlueTarget = Color.kFirstBlue;
    //private static final Color kRedTarget = Color.kFirstRed;
    //private Color m_colorSensed;
    //private String m_colorString = "\0";
   // private ColorMatchResult m_match;
    //private double m_colorSensorProximity;

    private DigitalInput m_proximitySensorStart;
    private DigitalInput m_proximitySensorCenter;

    private boolean m_proximitySensorStartState;
    private boolean m_proximitySensorCenterState;

    //private boolean m_rtsState;
    //private byte m_targetState;
    
    //used to mask out all but the last three bits when calculating sensor states
    // private byte andState = 0x07;
    
    //used to add a bit back in at the RTF position if that is required
    // private byte orState = 0x04;
    

    // private double lastSpeed = 0;
    private static IndexerSubsystem s_indexerSubsystem;
    public static IndexerSubsystem get(){return s_indexerSubsystem;}
    public IndexerSubsystem() {
        s_indexerSubsystem = this;
        configureShuffleboard();
		//  m_motor.setIdleMode(CANSparkMax.IdleMode.kBrake);
		// m_motor.enableVoltageCompensation(true);
		// m_motor.setInverted(true);
		m_motor.setIdleMode(IdleMode.kBrake);
		m_motor.enableVoltageCompensation(12);
		m_motor.setInverted(true);

		m_neoController.setP(IndexerConstants.kP);
        m_neoController.setI(IndexerConstants.kI);
        m_neoController.setD(IndexerConstants.kD);
        m_neoController.setIZone(IndexerConstants.kIz);
        m_neoController.setFF(IndexerConstants.kFF);
        m_neoController.setOutputRange(IndexerConstants.kMinOutput,IndexerConstants.kMaxOutput);
        
        //i2cPort = I2C.Port.kMXP; //52

        //m_colorSensor = new ColorSensorV3(i2cPort);
        
        //m_colorMatcher = new ColorMatch();

        //m_colorMatcher.addColorMatch(kBlueTarget);
        //m_colorMatcher.addColorMatch(kRedTarget);

        m_proximitySensorStart = new DigitalInput(IndexerConstants.kStartProximitySensorPort);
        m_proximitySensorCenter = new DigitalInput(IndexerConstants.kCenterProximitySensorPort);

        m_neoEncoder.setPosition(0);
	}
	
	public void periodic(){

        //get updated sensor states so we always know where balls are and what color the ball that is ready to shoot is
        updateSensors();
		
	}
    public void updateSensors(){
        
        //get the proximity sensed by the color sensor
        //m_colorSensorProximity = m_colorSensor.getProximity();
        
        // get the color seen by sensor
        //m_colorSensed = m_colorSensor.getColor();
        //m_rtsState = m_colorSensorProximity >=800;
        //find the closest match for the color of the ball, will return null if nothing is there
        //m_match = m_colorMatcher.matchClosestColor(m_colorSensed);
        //if(!m_rtsState){
        //    m_colorString = "Null";
        //}else if(m_match.color == kBlueTarget){
        //    m_colorString = "Blue";
        //}else if(m_match.color == kRedTarget){
        //    m_colorString = "Red";
        //}else{
        //    m_colorString = "Null";
        //}
        
        //save if either the center or start proximity sensors sense a ball
        m_proximitySensorStartState = !m_proximitySensorStart.get();
        m_proximitySensorCenterState = !m_proximitySensorCenter.get();
        SmartDashboard.putBoolean("Indexer RTF", m_proximitySensorStartState);
        SmartDashboard.putBoolean("Indexer BIC", m_proximitySensorCenterState);
        SmartDashboard.putNumber("Indexer Encoder Value", m_neoEncoder.getPosition());

        //SmartDashboard.putString("Color String", m_colorString);
    }

    /**
     * @return Current setpoint.
     */
    public double getSetpoint() {
        return m_setPosition;
    }

    public void setSpeed(double speed){
        // m_neoController.setReference(speed, ControlType.kVelocity, 0);
        m_motor.set(speed);
        // if(speed !=0){
        //     lastSpeed = speed;
        // }
        // else{}
    }
    
    public void setPositionAdvance(){
        //System.out.println("SETTING FORWARD");
        m_setPosition = m_neoEncoder.getPosition() + 30;
        m_neoController.setReference(m_setPosition, ControlType.kPosition, 0);
    }
    public void setPositionReverse(){
        //System.out.println("SETTING REVERSE");
        m_setPosition = m_neoEncoder.getPosition() + 30;
        m_neoController.setReference(m_setPosition, ControlType.kPosition, 0);
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
    //public boolean gamePieceRTS(){
    //    return m_rtsState;
    //}

    /**
     * Am I at where I want to be?
     * @return whether or not I am currently at my target state
     */
    //public boolean atTargetState(){
    //    return m_targetState == getCurrStateSubsystem();
    //}

    public boolean atTargetPosition() {
        //System.out.println("PERCENT ERROR: " + Math.abs(m_neoEncoder.getPosition() - getSetpoint() / getSetpoint()));
        return Math.abs(m_neoEncoder.getPosition() - getSetpoint()) < IndexerConstants.kAllowedErrorPercent;
    }
    /**
     * 
     * @return return closest position to where I am without going above
     */
    //public byte getCurrTargetState(){
    //    return getCurrStateSubsystem();
   // }

    /**
     * Returns sensor values as bytes **FOR SUBSYSTEM ONLY**
     * 
     * We can describe our state as a 3-bit number where the bits are:
     * RTF   BIC   RTS
     * (Ready to Feed, Ball in Center, Ready to Shoot)
     * So with a ball in center we have bits 010 or state 2.
     * 
     * @return sensor values as a 3 bit number
     */
    // private byte getCurrStateSubsystem(){

    //     return (byte)((byte)(gamePieceRTF()?1<<2:0) + (byte)(gamePieceAtCenter()?1<<1:0) + (byte)(gamePieceRTS()?1:0)); 
    // }

    // public byte getAdvanceTargetState(){
    //     // System.out.println("CURR SUBSYSTEM STATE: " + Integer.toBinaryString(getCurrStateSubsystem()));
    //     // System.out.println("RETURNING STATE(FWD): " + Integer.toBinaryString((byte)( getCurrStateSubsystem()>> 1)));
    //     //find sensor states if moved forward one position
    //     return (byte)( getCurrStateSubsystem()>> 1);
    // }
    // public byte getReverseTargetState(boolean preserveRTF){
        
    //     if(preserveRTF){
    //         // System.out.println("CURR SUBSYSTEM STATE: " + Integer.toBinaryString(getCurrStateSubsystem()));
    //         // System.out.println("RETURNING STATE(REV): " + Integer.toBinaryString((byte)((getCurrStateSubsystem() << 1) & andState | (getCurrStateSubsystem() & orState))));
            
    //         //find sensor states if moved backward one position, preserving ball RTF and removing everything but the last 3 bits
    //         return (byte)((getCurrStateSubsystem() << 1) & andState | (getCurrStateSubsystem() & orState));
    //     }else{
    //         // System.out.println("CURR SUBSYSTEM STATE: " + Integer.toBinaryString(getCurrStateSubsystem()));
    //         // System.out.println("RETURNING STATE(REV): " + Integer.toBinaryString((byte)((getCurrStateSubsystem() << 1) & andState)));
            
    //         //find sensor states if moved backward one position, WITHOUT preserving ball RTF and removing everything but the last 3 bits
    //         return (byte)((getCurrStateSubsystem() << 1) & andState);
    //     }
    // }

    // /**
    //  * Set target sensor state **Will not move or stop on its own, you must also set speed and stop when it's finished** 
    //  * @param state target sensor state
    //  */
    // public void setTargetState(byte state){
    //     m_targetState = state;
    // }
    // public String getColorString(){
    //     return m_colorString;
    // }
    // public double getLastSpeed(){
    //     return lastSpeed;
    // }
    public void configureShuffleboard(){
        
        ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Color");
        // shuffleboardTab.addBoolean("isBlue", () -> (m_colorString.equals("Blue"))).withSize(2, 2).withPosition(0, 0).withWidget(BuiltInWidgets.kBooleanBox);
        // shuffleboardTab.addBoolean("isRed", ()->(m_colorString.equals("Null") ? new Boolean(null) : m_colorString.equals("Red"))).withSize(2, 2).withPosition(2, 0).withWidget(BuiltInWidgets.kBooleanBox);
        shuffleboardTab.addBoolean("rtf", () -> (m_proximitySensorStartState)).withSize(2, 2).withPosition(4, 0).withWidget(BuiltInWidgets.kBooleanBox);
        shuffleboardTab.addBoolean("bic", () -> (m_proximitySensorCenterState)).withSize(2, 2).withPosition(6, 0).withWidget(BuiltInWidgets.kBooleanBox);
        //shuffleboardTab.addBoolean("rts", () -> (m_rtsState)).withSize(2, 2).withPosition(8, 0).withWidget(BuiltInWidgets.kBooleanBox);
    }
}

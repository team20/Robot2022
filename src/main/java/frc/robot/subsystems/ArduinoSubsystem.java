package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArduinoConstants;
import frc.robot.Constants.ArduinoConstants.LEDColors;
import frc.robot.Constants.ArduinoConstants.LEDModes;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class ArduinoSubsystem extends SubsystemBase {
	// PIDs
	private final PIDController m_anglePid = new PIDController(ArduinoConstants.kAngleP, ArduinoConstants.kAngleI,
			ArduinoConstants.kAngleD);
	private final PIDController m_distancePid = new PIDController(ArduinoConstants.kDistanceP,
			ArduinoConstants.kDistanceI, ArduinoConstants.kDistanceD);
	// I2C communication
	private final I2C m_wire = new I2C(Port.kOnboard, ArduinoConstants.kAddress);
	// data read from Arduino
	private byte[] m_readData = new byte[7];
	private byte[] m_writeData = new byte[4];
	private boolean m_targetInView;
	private int m_xValue;
	private int m_distance;
	// PID outputs
	private double m_turnSpeed;
	private double m_driveSpeed;
	private byte m_mainLEDMode = 0;
	private byte m_mainLEDValue = 0;
	private byte m_shooterLEDMode = 0;
	private byte m_shooterLEDValue = 0;

	/**
	 * Initializes a new instance of the {@link ArduinoSubsystem} class.
	 */
	public ArduinoSubsystem() {
		m_anglePid.setSetpoint(ArduinoConstants.kAngleSetpoint);
		//m_anglePid.setTolerance(ArduinoConstants.kAngleTolerance);
		m_distancePid.setSetpoint(ArduinoConstants.kDistanceSetpoint);
		//m_distancePid.setTolerance(ArduinoConstants.kDistanceTolerance);
	}

	public void periodic() {
		update();

	}

	public void write() {
		m_writeData[0] = m_mainLEDMode;
		m_writeData[1] = m_mainLEDValue;
		m_writeData[2] = m_shooterLEDMode;
		m_writeData[3] = m_shooterLEDValue;

		System.out.println("the main led MODE: " + m_mainLEDMode);
		System.out.println("the main led COLOR: " + m_mainLEDValue);
		System.out.println("the shooter led MODE: " + m_shooterLEDMode);
		System.out.println("the shooter led COLOR: " + m_shooterLEDValue);

		//m_wire.writeBulk(m_writeData);
		m_wire.writeBulk(m_writeData, m_writeData.length);
		
		System.out.println("aborted?: " + m_wire.writeBulk(m_writeData, m_writeData.length));
	}

	public void setMainLEDMode(byte mode) {
		m_mainLEDMode = mode;
	}

	public void setShooterLEDMode(byte mode) {
		m_shooterLEDMode = mode;
	}

	public void setMainLEDValue(byte value) {
		m_mainLEDValue = value;
	}
	
	public void setShooterLEDValue(byte value) {
		m_shooterLEDValue = value;
	}

	/**
	 * @return Speed to turn to face target.
	 */
	public double getTurnSpeed() {
		return m_turnSpeed;
	}

	/**
	 * @return Speed to turn to drive towards target.
	 */
	public double getDriveSpeed() {
		return m_driveSpeed;
	}

	// /**
	// * @return Whether both PIDs are at their setpoints.
	// */
	public boolean atSetpoint() {
		return m_anglePid.atSetpoint() && m_distancePid.atSetpoint();
	}

	/**
	 * Updates I2C stuff.
	 */

	public void update() {
		if (DriverStation.isDisabled()) {
			setMainLEDMode(LEDModes.kOff);
			setMainLEDValue(LEDColors.kOff);
			setShooterLEDMode(LEDModes.kOff);
			setShooterLEDValue(LEDColors.kOff);
		}

		// if (Timer.getFPGATimestamp() >= 120) {
		// 	setMainLEDMode(LEDModes.kBackForthTimer);
		// 	setMainLEDValue(LEDColors.kBlue);
		// 	setShooterLEDMode(LEDModes.kBackForthTimer);
		// 	setShooterLEDMode(LEDColors.kBlue);
		// }

		read();
		m_turnSpeed = -m_anglePid.calculate(m_xValue);
		m_driveSpeed = -m_distancePid.calculate(m_distance);
		write();
	}

	/**
	 * Reads data sent from Arduino.
	 */
	public void read() {
		// read byte array
		m_wire.read(ArduinoConstants.kAddress, m_readData.length, m_readData);
		// set values from array to variables
		m_targetInView = m_readData[ArduinoConstants.kReadTargetInView] == 1;
		m_xValue = 0;
		for (int i : ArduinoConstants.kReadXValue)
			m_xValue += m_readData[i];
		m_distance = 0;
		for (int i : ArduinoConstants.kReadDistance)
			m_distance += m_readData[i];
	}

	/**
	 * @return Whether or not a target is in the camera's view.
	 */
	public boolean getTargetInView() {
		return m_targetInView;
	}

	/**
	 * @return X-value of target in pixels.
	 */
	public int getXValue() {
		return m_xValue;
	}

	/**
	 * @return Distance to the target in inches.
	 */
	public int getDistance() {
		return m_distance;
	}

	public void resetLEDs() {
		setMainLEDMode(LEDModes.kReset);
		setMainLEDValue(LEDColors.kOff);
		setShooterLEDMode(LEDModes.kReset);
		setShooterLEDValue(LEDColors.kOff);
	}

}
package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalOutput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArduinoConstants;

public class ArduinoSubsystem extends SubsystemBase {

	private static ArduinoSubsystem s_subsystem;
	public static ArduinoSubsystem get(){return s_subsystem;}

	private final DigitalOutput m_arduino = new DigitalOutput(ArduinoConstants.kAddress);
	//private boolean m_flywheelAtSetpoint = false;
	private boolean m_ballSeen;
	private boolean m_limelightShooting;

	/**
	 * Initializes a new instance of the {@link ArduinoSubsystem} class.
	 */
	public ArduinoSubsystem() {
		s_subsystem = this;
	}

	public void periodic() {
		// if (FlywheelSubsystem.get().atSetpoint() == true) {
		// 	m_flywheelAtSetpoint = true;
		// } else {
		// 	m_flywheelAtSetpoint = false;
		// }		
		//System.out.println("flywheel at setpoint: " + m_flywheelAtSetpoint);
		if (IndexerSubsystem.get().gamePieceRTF()) {
			m_ballSeen = true;
		} else {
			m_ballSeen = false;
		}

		// if () {
			
		// } else {

		// }

		m_arduino.set(m_ballSeen);
	}

	public void write() {
		//m_arduino.set(m_flywheelAtSetpoint);
		m_arduino.set(m_ballSeen);
	}

	public void setFlywheelState(boolean ballSeen) {
		//m_flywheelAtSetpoint = flywheelAtSetpoint;
		m_ballSeen = ballSeen;
	}

}
package frc.robot.subsystems;


import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeArmConstants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.ShuffleboardLogging;

public class IntakeSubsystem extends SubsystemBase implements ShuffleboardLogging {
	private static IntakeSubsystem s_intakeSubsystem;
	public static IntakeSubsystem get(){return s_intakeSubsystem;} 

    private final CANSparkMax m_motor = new CANSparkMax(IntakeConstants.kMotorPort, MotorType.kBrushless);

	/**
	 * Initializes a new instance of the {@link IntakeSubsystem} class.
	 */
	public IntakeSubsystem() {
		s_intakeSubsystem = this;
		m_motor.restoreFactoryDefaults();
		m_motor.setIdleMode(CANSparkMax.IdleMode.kBrake);
		m_motor.enableVoltageCompensation(12);
		m_motor.setInverted(IntakeArmConstants.kInvert);
		m_motor.setSmartCurrentLimit(IntakeArmConstants.kSmartCurrentLimit);
	}

	/**
	 * Sets new speed for the intake wheel to spin at.
	 * 
	 * @param speed Percent output.
	 */
	public void setSpeed(double speed) {
		//System.out.println("Setting % output: " + speed);
		m_motor.set(speed);
	}

	public void configureShuffleboard(boolean inCompetitionMode) {
		if (!inCompetitionMode) {
		//ShuffleboardTab shuffleboardTab = Shuffleboard.getTab("Intake");
		//shuffleboardTab.addNumber("Motor output", () -> m_motor.getMotorOutputPercent()).withSize(4, 2)
		//		.withPosition(0, 0).withWidget(BuiltInWidgets.kGraph);
	}
}
}

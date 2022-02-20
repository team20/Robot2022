package frc.robot.commands.DriveCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.ArduinoConstants.LEDColors;
import frc.robot.Constants.ArduinoConstants.LEDModes;
import frc.robot.commands.ArduinoCommands.UpdateLEDsCommand;
import frc.robot.subsystems.ArduinoSubsystem;
import frc.robot.subsystems.DriveSubsystem;

public class ArcadeDriveCommand extends CommandBase {

	private final DriveSubsystem m_driveSubsystem;
	private final Supplier<Double> m_speedStraight, m_speedLeft, m_speedRight;
	private final ArduinoSubsystem m_arduinoSubsystem;

	/**
	 * Drive using speed inputs as a percentage output of the motor
	 * 
	 * @param driveSubsystem The subsystem to be used
	 * @param speedStraight  Supplier of straight speed
	 * @param speedLeft      Supplier of left speed
	 * @param speedRight     Supplier of right speed
	 */
	public ArcadeDriveCommand(DriveSubsystem driveSubsystem, ArduinoSubsystem arduinoSubsystem,
		Supplier<Double> speedStraight, Supplier<Double> speedLeft, Supplier<Double> speedRight) {
		m_driveSubsystem = driveSubsystem;
		m_arduinoSubsystem = arduinoSubsystem;
		m_speedStraight = speedStraight;
		m_speedLeft = speedLeft;
		m_speedRight = speedRight;
		addRequirements(m_driveSubsystem);
	}

	/**
	 * Update the motor outputs
	 */
	public void execute() {
		double speedStraight = Math.abs(m_speedStraight.get()) > .1 ? m_speedStraight.get()
				: 0;

		double speedLeft = Math.abs(m_speedLeft.get()) > 0.05 ? m_speedLeft.get() : 0;
		double speedRight = Math.abs(m_speedRight.get()) > 0.05 ? m_speedRight.get()
				: 0;
		if (speedStraight != 0) {
			speedLeft *= .45;
			speedRight *= .45;
		}

		if (speedStraight != 0) {
		speedLeft *= DriveConstants.kTurningMultiplier;
		speedRight *= DriveConstants.kTurningMultiplier;
		}

		m_driveSubsystem.arcadeDrive(speedStraight, speedLeft, speedRight);
		
		// m_arduinoSubsystem.resetLEDs();
		// if (speedStraight >= 0.25) {
		// 	new UpdateLEDsCommand(m_arduinoSubsystem, () -> LEDModes.kSolid,
		// 	() -> LEDColors.kPurple, () -> LEDModes.kOff, () -> LEDColors.kOff).execute();
		// } else {
		// 	new UpdateLEDsCommand(m_arduinoSubsystem, () -> LEDModes.kChasing,
		// 	() -> LEDColors.kGreen, () -> LEDModes.kOff, () -> LEDColors.kOff).execute();
		// }

	}
}
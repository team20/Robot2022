package frc.robot.commands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.IndexerSubsystem;
import frc.robot.Constants.IndexerConstants;

public class AutoIndexCommand extends CommandBase {

	private final IndexerSubsystem m_indexerSubsystem;
	private final Supplier<Boolean> m_flywheelReady;

	/**
	 * Begin the feeder when the carousel is at the feeder opening and the flywheel
	 * is at speed
	 * 
	 * @param feederSubsystem  {@link FeederSubsystem} to be used.
	 * @param carouselPosition The current position of the carousel
	 * @param flywheelReady    Whether the flywheel is at speed
	 */
	public AutoIndexCommand(IndexerSubsystem feederSubsystem, Supplier<Boolean> flywheelReady) {
		m_indexerSubsystem = feederSubsystem;
		m_flywheelReady = flywheelReady;
		addRequirements(m_indexerSubsystem);
	}

	/**
	 * Run feeder motor at correct carousel position
	 */
	public void execute() {
		if (m_flywheelReady.get()) {
			m_indexerSubsystem.setPercentOutput(IndexerConstants.kSpeed);
		}
	}

	/**
	 * Stop the feeder at the end of the command
	 */
	public void end(boolean interrupted) {
		m_indexerSubsystem.setSpeed(0.0);
	}
}
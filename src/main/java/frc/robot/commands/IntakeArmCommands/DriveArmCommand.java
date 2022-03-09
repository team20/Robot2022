package frc.robot.commands.IntakeArmCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.ControllerConstants;
import frc.robot.subsystems.IntakeArmSubsystem;

public class DriveArmCommand extends CommandBase {

    private final IntakeArmSubsystem m_intakeArmSubsystem;
    private final Supplier<Double> m_speed;

    /**
     * Drive the arm using percent output
     * 
     * @param armSubsystem The hood subsystem to be used
     * @param speed Supplier of speed
     */
    public DriveArmCommand(IntakeArmSubsystem intakeArmSubsystem, Supplier<Double> speed) {
        m_intakeArmSubsystem = intakeArmSubsystem;
        m_speed = speed;
        addRequirements(m_intakeArmSubsystem);
    }

    /**
     * Update the motor output
     */
    public void execute() {
        double speed = .3 * Math.abs(m_speed.get()) > ControllerConstants.kTriggerDeadzone ? m_speed.get() : 0;
        m_intakeArmSubsystem.setPercentOutput(speed);
        System.out.println("Arm encoder position: " + m_intakeArmSubsystem.getPosition());
    }

    /**
     * Stop the arm when the command ends
     */
    public void end(boolean interrupted) {
        m_intakeArmSubsystem.setPercentOutput(0);
    }
}
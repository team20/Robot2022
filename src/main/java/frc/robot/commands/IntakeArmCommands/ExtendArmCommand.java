package frc.robot.commands.IntakeArmCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.IntakeArmConstants;
import frc.robot.subsystems.IntakeArmSubsystem;

public class ExtendArmCommand extends CommandBase {

    private final IntakeArmSubsystem  m_intakeArmSubsystem;

    /**
     * Extend the arm
     * 
     * @param armSubsystem {@link ArmSubsystem} to be used.
     */
    public ExtendArmCommand(IntakeArmSubsystem intakeArmSubsystem) {
        m_intakeArmSubsystem = intakeArmSubsystem;
        addRequirements(m_intakeArmSubsystem);
    }

    /**
     * Update arm setpoint
     */
    public void initialize() {
        //m_intakeArmSubsystem.zeroTheArm();
        m_intakeArmSubsystem.setPosition(IntakeArmConstants.kOutPosition);
    }
}
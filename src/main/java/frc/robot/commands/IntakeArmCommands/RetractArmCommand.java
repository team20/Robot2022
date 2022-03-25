package frc.robot.commands.IntakeArmCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.IntakeArmConstants;
import frc.robot.subsystems.IntakeArmSubsystem;

public class RetractArmCommand extends CommandBase {

    private final IntakeArmSubsystem m_armSubsystem;

    /**
     * Retract the arm
     * 
     * @param armSubsystem {@link ArmSubsystem} to be used.
     */
    public RetractArmCommand(IntakeArmSubsystem armSubsystem) {
        m_armSubsystem = armSubsystem;
        addRequirements(m_armSubsystem);
    }

    /**
     * Update arm setpoint
     */
    public void initialize() {
        //m_armSubsystem.zeroTheArm();
        m_armSubsystem.setPosition(IntakeArmConstants.kInPosition);
    }
}
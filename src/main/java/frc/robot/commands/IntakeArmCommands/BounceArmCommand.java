package frc.robot.commands.IntakeArmCommands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.IntakeArmConstants;
import frc.robot.subsystems.IntakeArmSubsystem;

public class BounceArmCommand extends CommandBase {

    private final IntakeArmSubsystem m_intakeArmSubsystem;
    private double lastBounceTime;
    private boolean armDown;

    /**
     * Bounce the arm to improve intaking
     * 
     * @param armSubsystem {@link ArmSubsystem} to be used.
     */
    public BounceArmCommand(IntakeArmSubsystem armSubsystem) {
        m_intakeArmSubsystem = armSubsystem;
        addRequirements(m_intakeArmSubsystem);
    }

    public void initialize() {
        lastBounceTime = Timer.getFPGATimestamp();
        m_intakeArmSubsystem.setPosition(IntakeArmConstants.kBounceDownPosition);
        armDown = true;
    }

    public void execute() {
        if (Timer.getFPGATimestamp() - lastBounceTime >= IntakeArmConstants.kBounceTime) {
            if (armDown) {
                m_intakeArmSubsystem.setPosition(IntakeArmConstants.kBounceUpPosition);
            } else {
                m_intakeArmSubsystem.setPosition(IntakeArmConstants.kBounceDownPosition);
            }
            armDown = !armDown;
            lastBounceTime = Timer.getFPGATimestamp();
        }
    }

    public void end(boolean interrupted) {
        m_intakeArmSubsystem.setPosition(IntakeArmConstants.kOutPosition);
    }
}
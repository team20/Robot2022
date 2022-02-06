package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.SlideHookConstants;
import frc.robot.subsystems.SlideHookSubsystem;

public class SlideHookPositionCommand extends CommandBase {

    private final SlideHookSubsystem m_slideHookSubsystem;
    private final double m_inches;

    /**
     * Drive the hood using setpoints
     * 
     * @param hoodSubsystem The hood subsystem to be used
     * @param inches      The desired encoder position in inches
     */
    public SlideHookPositionCommand(SlideHookSubsystem slideHookSubsystem, double inches) {
        m_slideHookSubsystem = slideHookSubsystem;
        m_inches = inches;
        addRequirements(m_slideHookSubsystem);
    }

    /**
     * Update the setpoint
     */
    public void execute() {
        m_slideHookSubsystem.setPosition(m_inches/SlideHookConstants.kInchesPerQuarterTurn);
        System.out.println("Setting slide hook position to "+m_inches+ " inches");
    }
}
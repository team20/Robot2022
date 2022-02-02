package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SlideHookSubsystem;

public class SlideHookPositionCommand extends CommandBase {

    private final SlideHookSubsystem m_slideHookSubsystem;
    private final double m_setpoint;

    /**
     * Drive the hood using setpoints
     * 
     * @param hoodSubsystem The hood subsystem to be used
     * @param setpoint      The desired encoder position
     */
    public SlideHookPositionCommand(SlideHookSubsystem slideHookSubsystem, double setpoint) {
        m_slideHookSubsystem = slideHookSubsystem;
        m_setpoint = setpoint;
        addRequirements(m_slideHookSubsystem);
    }

    /**
     * Update the setpoint
     */
    public void execute() {
        m_slideHookSubsystem.setPosition(m_setpoint);
    }
}
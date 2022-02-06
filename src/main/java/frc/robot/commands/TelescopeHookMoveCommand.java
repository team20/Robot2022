package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.TelescopeHookSubsystem;

public class TelescopeHookMoveCommand extends CommandBase {

    private final TelescopeHookSubsystem m_telescopeHookSubsystem;
    private final double m_speed;

    /**
     * 
     * @param telescopeHookSubsystem
     * @param speed percent speed of telescope hook
     */
    public TelescopeHookMoveCommand(TelescopeHookSubsystem telescopeHookSubsystem, double speed) {
        m_telescopeHookSubsystem = telescopeHookSubsystem;
        m_speed = speed;
        addRequirements(m_telescopeHookSubsystem);
    }

    /**
     * Run hook at speed
     */
    public void execute() {
        m_telescopeHookSubsystem.setSpeed(m_speed);
    }
}
package frc.robot.commands.ClimberCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.SlideHookSubsystem;

public class SlideHookMoveCommand extends CommandBase {

    private final SlideHookSubsystem m_slideHookSubsystem;
    private final double m_speed;

    /**
     * 
     * @param slideHookSubsystem
     * @param speed percent speed of slide hook
     */
    public SlideHookMoveCommand(SlideHookSubsystem slideHookSubsystem, double speed) {
        m_slideHookSubsystem = slideHookSubsystem;
        m_speed = speed;
        addRequirements(m_slideHookSubsystem);
    }

    /**
     * Run hook at speed
     */
    public void execute() {
        m_slideHookSubsystem.setSpeed(m_speed);
    }
}
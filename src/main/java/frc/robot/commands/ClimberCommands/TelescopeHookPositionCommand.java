package frc.robot.commands.ClimberCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.TelescopeHookSubsystem;

public class TelescopeHookPositionCommand extends CommandBase {

    private final TelescopeHookSubsystem m_telescopeHookSubsystem;
    private final double m_setpoint;

    /**
     * Drive the hood using setpoints
     * 
     * @param hoodSubsystem The hood subsystem to be used
     * @param setpoint      The desired encoder position
     */
    public TelescopeHookPositionCommand(TelescopeHookSubsystem telescopeHookSubsystem, double setpoint) {
        m_telescopeHookSubsystem = telescopeHookSubsystem;
        m_setpoint = setpoint;
        addRequirements(m_telescopeHookSubsystem);
    }

    /**
     * Update the setpoint
     */
    public void execute() {
        m_telescopeHookSubsystem.setPosition(m_setpoint);
        System.out.println("running telescope tto pos "+m_setpoint);
        System.out.println("Motor current is "+m_telescopeHookSubsystem.getOutputCurrent());
    }
    public boolean isFinished(){
        return m_telescopeHookSubsystem.atSetpoint();
    }
    public void end(boolean interruped){
        System.out.println("stopping ttelescope");
        m_telescopeHookSubsystem.setSpeed(0.0);
    }
}
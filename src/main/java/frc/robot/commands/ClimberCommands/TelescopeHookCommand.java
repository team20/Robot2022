package frc.robot.commands.ClimberCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.TelescopeHookSubsystem;

public class TelescopeHookCommand extends CommandBase {

    private final TelescopeHookSubsystem s_telescopeHookSubsystem;
    private final double m_param;

    public enum Operation{
        CMD_POSITION,
        CMD_MOVE
    }
    private final Operation m_operation;
    /**
     * Drive the hood using setpoints
     * 
     * @param hoodSubsystem The hood subsystem to be used
     * @param setpoint      The desired encoder position
     */
    public TelescopeHookCommand(Operation operation, double param) {
        s_telescopeHookSubsystem = TelescopeHookSubsystem.get();
        m_operation = operation;
        m_param = param;
        addRequirements(s_telescopeHookSubsystem);
    }

    /**
     * Update the setpoint
     */
    public void execute() {
        if(m_operation == Operation.CMD_POSITION){
            s_telescopeHookSubsystem.setPosition(m_param);
            System.out.println("Motor current is "+s_telescopeHookSubsystem.getOutputCurrent());
        }else if(m_operation == Operation.CMD_MOVE){
            s_telescopeHookSubsystem.setSpeed(m_param);
        }
        
    }
}
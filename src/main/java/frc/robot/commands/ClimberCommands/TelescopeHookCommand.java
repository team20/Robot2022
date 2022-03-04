package frc.robot.commands.ClimberCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.TelescopeHookSubsystem;

public class TelescopeHookCommand extends CommandBase {

    private final double m_param;

    public enum Operation{
        CMD_POSITION,
        CMD_MOVE,
        CMD_POSITION_SETTLE
    }
    private final Operation m_operation;
    
    public TelescopeHookCommand(Operation operation, double param) {
        m_operation = operation;
        m_param = param;
        addRequirements(TelescopeHookSubsystem.get());
    }

    /**
     * Update the setpoint
     */
    public void execute() {
        TelescopeHookSubsystem subsystem = TelescopeHookSubsystem.get();
        if(m_operation == Operation.CMD_POSITION){
            System.out.println("setting position to "+m_param);
            subsystem.setPosition(m_param);
            System.out.println("Motor current is "+subsystem.getOutputCurrent());
        }else if(m_operation == Operation.CMD_MOVE){
            subsystem.setSpeed(m_param);
        }
        
    }

    @Override
    public boolean isFinished(){
        if(m_operation == Operation.CMD_POSITION){
            return TelescopeHookSubsystem.get().atSetpoint();
        }
        return true;
    }
    public void end(boolean interrupted){
        TelescopeHookSubsystem.get().setSpeed(0.0);
    }
}
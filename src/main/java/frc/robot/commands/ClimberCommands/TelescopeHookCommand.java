package frc.robot.commands.ClimberCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.TelescopeHookConstants;
import frc.robot.subsystems.TelescopeHookSubsystem;

public class TelescopeHookCommand extends CommandBase {

    private double m_param;

    public enum Operation{
        CMD_POSITION,
        CMD_MOVE,
        CMD_JOYSTICK,
        CMD_JOYSTICK_POSITION,
        CMD_ZERO_ENCODERS,
        CMD_STOP,
    }
    private final Operation m_operation;
    private Supplier<Double> m_paramSup;

    
    public TelescopeHookCommand(Operation operation, double param) {
        m_operation = operation;
        m_param = param;
        addRequirements(TelescopeHookSubsystem.get());
    }

    public TelescopeHookCommand(Operation operation, Supplier<Double> param) {
        m_operation = operation;
        m_paramSup = param;
        addRequirements(TelescopeHookSubsystem.get());

    }
    public void execute() {
        TelescopeHookSubsystem subsystem = TelescopeHookSubsystem.get();
        if(m_operation == Operation.CMD_POSITION){
            subsystem.setPosition(m_param);
        }else if(m_operation == Operation.CMD_MOVE){
            subsystem.setSpeed(m_param);
        }else if(m_operation == Operation.CMD_JOYSTICK){
            subsystem.setSpeed(Math.abs(m_paramSup.get()) > 0.05 ? m_paramSup.get() : 0);
        }else if(m_operation == Operation.CMD_JOYSTICK_POSITION){
            if(m_paramSup.get()>.1){
                subsystem.setPosition(TelescopeHookConstants.kRetractedPosition);
            }else if(m_paramSup.get()<-.1){
                subsystem.setPosition(TelescopeHookConstants.kExtendedPosition);
            }else{
                subsystem.setSpeed(0);
            }
        }else if (m_operation == Operation.CMD_ZERO_ENCODERS) {
            subsystem.resetEncoder();
        }else if(m_operation == Operation.CMD_STOP){
            subsystem.setSpeed(0);
        }
        
    }

    @Override
    public boolean isFinished(){
        if(m_operation == Operation.CMD_POSITION){
            return TelescopeHookSubsystem.get().atleftSetpoint() && TelescopeHookSubsystem.get().atrightSetpoint();
        }else if(m_operation==Operation.CMD_STOP || m_operation==Operation.CMD_ZERO_ENCODERS){
            return true;
        }
        return false;
    }
    public void end(boolean interrupted){
            TelescopeHookSubsystem.get().setSpeed(0.0);
    }
}
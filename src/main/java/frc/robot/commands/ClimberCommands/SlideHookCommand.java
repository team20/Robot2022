package frc.robot.commands.ClimberCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.SlideHookConstants;
import frc.robot.subsystems.SlideHookSubsystem;

public class SlideHookCommand extends CommandBase {

    private double m_param;
    public enum Operation{
        CMD_MOVE,
        CMD_POSITION,
        CMD_POSITION_SETTLE,
        CMD_JOYSTICK,
        CMD_JOYSTICK_POSITION,
        CMD_ZERO_ENCODERS,
        CMD_STOP,
    }

    private Operation m_operation;
    private Supplier<Double> m_paramSup;

    public SlideHookCommand(Operation operation, double param) {
        m_operation = operation;
        m_param = param;
        addRequirements(SlideHookSubsystem.get());
    }
    public SlideHookCommand(Operation operation, Supplier<Double> param) {
        m_operation = operation;
        m_paramSup = param;
        addRequirements(SlideHookSubsystem.get());

    }

    /**
     * Update the setpoint
     */
    public void execute() {
        SlideHookSubsystem subsystem = SlideHookSubsystem.get();
        if(m_operation == Operation.CMD_POSITION){
            subsystem.setPosition(m_param);
        }else if (m_operation == Operation.CMD_MOVE){
            subsystem.setSpeed(m_param);
        }else if(m_operation == Operation.CMD_JOYSTICK){
            subsystem.setSpeed(Math.abs(m_paramSup.get()) > 0.05 ? m_paramSup.get() : 0);
        }else if(m_operation == Operation.CMD_JOYSTICK_POSITION){
            if(m_paramSup.get()>.1){
                subsystem.setPosition(SlideHookConstants.kStartPosition);
            }else if(m_paramSup.get()<-.1){
                subsystem.setPosition(SlideHookConstants.kMaxPosition);
            }else{
                subsystem.setPercentOutput(0);
            }
        }else if(m_operation==Operation.CMD_ZERO_ENCODERS){
            subsystem.resetEncoder();
        }else if(m_operation == Operation.CMD_STOP){
            subsystem.setSpeed(0);
        }
        
    }

    @Override
    public boolean isFinished(){
        if(m_operation == Operation.CMD_POSITION){
            return SlideHookSubsystem.get().atSetpoint();
        }else if(m_operation==Operation.CMD_STOP || m_operation==Operation.CMD_ZERO_ENCODERS){
            return true;
        }
        return false;
    }
    public void end(boolean interrupted){
      //  if(m_operation != Operation.CMD_POSITION){
            SlideHookSubsystem.get().setSpeed(0.0);
    //    }
    }
}
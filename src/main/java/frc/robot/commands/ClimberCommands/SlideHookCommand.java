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
        CMD_TO_ANGLE,
        CMD_POSITION_SETTLE,
        CMD_JOYSTICK,
        CMD_JOYSTICK_POSITION,
        CMD_ZERO_ENCODERS,
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
        }
        else if (m_operation == Operation.CMD_MOVE){
            subsystem.setSpeed(m_param);
        }
        else if(m_operation == Operation.CMD_TO_ANGLE){
            // System.out.println("NAVX ANGLE IS "+subsystem.getHeading());//TODO: might not be yaw depending on orientation of navx
            subsystem.setSpeed(m_param);
        }
        else if(m_operation == Operation.CMD_JOYSTICK){
            // System.out.println("NAVX ANGLE IS "+subsystem.getHeading());//TODO: might not be yaw depending on orientation of navx
            subsystem.setSpeed(Math.abs(m_paramSup.get()) > 0.05 ? m_paramSup.get() : 0);
        }else if(m_operation == Operation.CMD_JOYSTICK_POSITION){
            if(m_paramSup.get()>.1){
                subsystem.setPosition(0);//85
            }
            else if(m_paramSup.get()<-.1){
                subsystem.setPosition(SlideHookConstants.kMaxPosition);
            //    System.out.println("Running to start position");
            }
            else{
                subsystem.setPercentOutput(0);
            }
        }else if(m_operation==Operation.CMD_ZERO_ENCODERS){
            subsystem.resetEncoder();
        }
        
    }

    @Override
    public boolean isFinished(){
        if(m_operation == Operation.CMD_POSITION){
            return SlideHookSubsystem.get().atSetpoint();
        }else if(m_operation == Operation.CMD_TO_ANGLE){
            return SlideHookSubsystem.get().getHeading()>= m_param;//TODO: see if it is yaw
        }else if(m_operation == Operation.CMD_MOVE){
            return false;
        }else if(m_operation == Operation.CMD_JOYSTICK){
            return false;
        }else if(m_operation == Operation.CMD_JOYSTICK_POSITION){
            return false;
        }else if(m_operation == Operation.CMD_ZERO_ENCODERS){
            return false;
        }
        return true;
    }
    public void end(boolean interrupted){
        // if(m_operation != Operation.CMD_MOVE){
            SlideHookSubsystem.get().setSpeed(0.0);        
        // }
    }
}
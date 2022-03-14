package frc.robot.commands.ClimberCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.SlideHookConstants;
import frc.robot.subsystems.SlideHookSubsystem;

public class SlideHookCommand extends CommandBase {

    private final double m_param;
    public enum Operation{
        CMD_MOVE,
        CMD_POSITION,
        CMD_TO_ANGLE,
        CMD_POSITION_SETTLE
    }

    private Operation m_operation;

    public SlideHookCommand(Operation operation, double param) {
        m_operation = operation;
        m_param = param;
        addRequirements(SlideHookSubsystem.get());
    }

    /**
     * Update the setpoint
     */
    public void execute() {
        SlideHookSubsystem subsystem = SlideHookSubsystem.get();
        if(m_operation == Operation.CMD_POSITION){
            subsystem.setPosition(m_param/SlideHookConstants.kInchesPerQuarterTurn);
            // System.out.println("Setting slide hook position to "+m_param+ " inches");
        }
        else if (m_operation == Operation.CMD_MOVE){
            subsystem.setSpeed(m_param);
        }
        else if(m_operation == Operation.CMD_TO_ANGLE){
            // System.out.println("NAVX ANGLE IS "+subsystem.getHeading());//TODO: might not be yaw depending on orientation of navx
            subsystem.setSpeed(m_param);
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
        }
        return true;
    }
    public void end(boolean interrupted){
        SlideHookSubsystem.get().setSpeed(0.0);        
    }
}
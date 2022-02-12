package frc.robot.commands.ClimberCommands;

import com.kauailabs.navx.frc.AHRS;

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
    private final AHRS m_gyro = new AHRS(DriveConstants.kGyroPort);

    /**
     * Drive the hood using setpoints
     * 
     * @param hoodSubsystem The hood subsystem to be used
     * @param inches      The desired encoder position in inches
     */
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
            System.out.println("Setting slide hook position to "+m_param+ " inches");
        }else if (m_operation == Operation.CMD_MOVE){
            subsystem.setSpeed(m_param);
        }else if(m_operation == Operation.CMD_TO_ANGLE){
            System.out.println("NAVX ANGLE IS "+m_gyro.getYaw());//TODO: might not be yaw depending on orientation of navx
            if(m_gyro.getYaw()<m_param){
                subsystem.setSpeed(SlideHookConstants.kHookVelocity);
            }
            else{
                subsystem.setSpeed(0.0);
            }
        }
        
    }

    @Override
    public boolean isFinished(){
        if(m_operation == Operation.CMD_POSITION){
            return true;
        }else if(m_operation == Operation.CMD_MOVE){
            return true;
        }else if(m_operation == Operation.CMD_TO_ANGLE){
            return m_gyro.getYaw() > m_param;
        }else if(m_operation == Operation.CMD_POSITION_SETTLE){
            return SlideHookSubsystem.get().atSetpoint();
        }
        return true;
    }
}
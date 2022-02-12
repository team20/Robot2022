package frc.robot.commands.ClimberCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.SlideHookConstants;
import frc.robot.subsystems.SlideHookSubsystem;

public class SlideHookCommand extends CommandBase {

    private final SlideHookSubsystem m_slideHookSubsystem;
    private final double m_param;

    public enum Operation{
        CMD_MOVE,
        CMD_POSITION
    }

    private Operation m_operation;
    /**
     * Drive the hood using setpoints
     * 
     * @param hoodSubsystem The hood subsystem to be used
     * @param inches      The desired encoder position in inches
     */
    public SlideHookCommand(SlideHookSubsystem slideHookSubsystem, Operation operation, double param) {
        m_slideHookSubsystem = slideHookSubsystem;
        m_operation = operation;
        
        m_param = param;
        addRequirements(m_slideHookSubsystem);
    }

    /**
     * Update the setpoint
     */
    public void initialize() {
        if(m_operation == Operation.CMD_POSITION){
            m_slideHookSubsystem.setPosition(m_param/SlideHookConstants.kInchesPerQuarterTurn);
            System.out.println("Setting slide hook position to "+m_param+ " inches");
        }else if (m_operation == Operation.CMD_MOVE){
            m_slideHookSubsystem.setSpeed(m_param);
        }
        
    }
}
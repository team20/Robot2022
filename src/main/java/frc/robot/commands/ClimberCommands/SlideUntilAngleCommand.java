package frc.robot.commands.ClimberCommands;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.SlideHookConstants;
import frc.robot.subsystems.SlideHookSubsystem;

public class SlideUntilAngleCommand extends CommandBase{
    private final SlideHookSubsystem m_slideHookSubsystem;
    private final AHRS m_gyro = new AHRS(DriveConstants.kGyroPort);
    private final double m_targetAngle;
/**
 * 
 * @param slideHookSubsystem Slide hook subsystem to be used
 * @param targetAngle angle to stop slide hook at
 */
    public SlideUntilAngleCommand(SlideHookSubsystem slideHookSubsystem, double targetAngle){
        m_slideHookSubsystem=slideHookSubsystem;
        m_targetAngle=targetAngle;
        addRequirements(slideHookSubsystem);
    }
    public void execute(){
        System.out.println("NAVX ANGLE IS "+m_gyro.getYaw());//TODO: might not be yaw depending on orientation of navx
        if(m_gyro.getYaw()<m_targetAngle){
            m_slideHookSubsystem.setSpeed(SlideHookConstants.kHookVelocity);
        }
        else{
            m_slideHookSubsystem.setSpeed(0.0);
        }
    }
}

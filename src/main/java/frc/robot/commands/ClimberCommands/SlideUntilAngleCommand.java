package frc.robot.commands.ClimberCommands;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.Constants.DriveConstants;
import frc.robot.Constants.SlideHookConstants;
import frc.robot.subsystems.SlideHookSubsystem;

public class SlideUntilAngleCommand extends CommandBase{
    private final SlideHookSubsystem m_slideHookSubsystem;
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
    public void initialize(){

    }
    public void execute(){
        m_slideHookSubsystem.setSpeed(SlideHookConstants.kRunningSpeed);
        System.out.println("angle is "+m_slideHookSubsystem.getHeading());
    }
    public boolean isFinished(){
        if(m_slideHookSubsystem.getHeading()>m_targetAngle){
            System.out.println("DONEODNOEONDOENDONE");
            return true;
        }
        else{
            return false;
        }
    }
    public void end (boolean interrupted){
        m_slideHookSubsystem.setSpeed(0.0);
    }
}

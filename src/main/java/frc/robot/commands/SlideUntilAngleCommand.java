package frc.robot.commands;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;

import frc.robot.Constants.DriveConstants;
import frc.robot.subsystems.SlideHookSubsystem;

public class SlideUntilAngleCommand extends CommandBase{
    private final SlideHookSubsystem m_slideHookSubsystem;
    private final AHRS m_gyro = new AHRS(DriveConstants.kGyroPort);
/**
 * 
 * @param slideHookSubsystem Slide hook subsystem to be used
 * @param targetAngle angle to stop slide hook at
 */
    public SlideUntilAngleCommand(SlideHookSubsystem slideHookSubsystem, double targetAngle){
        m_slideHookSubsystem=slideHookSubsystem;

        addRequirements(slideHookSubsystem);
    }
    public void execute(){

    }
}

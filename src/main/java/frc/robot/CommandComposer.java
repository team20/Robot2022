// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.commands.ClimberCommands.*;
import frc.robot.commands.IndexerCommands.*;
import frc.robot.commands.LimelightCommands.*;
import frc.robot.commands.ShooterCommands.*;
import frc.robot.subsystems.*;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.Constants;
import frc.robot.Constants.*;
/** Add your docs here. */
public class CommandComposer {

    private ArduinoSubsystem m_arduinoSubsystem;
    private DriveSubsystem m_driveSubsystem;
    private FlywheelSubsystem m_flywheelSubsystem;
    private HoodSubsystem m_hoodSubsystem;
    private IndexerSubsystem m_indexerSubsystem;
    private IntakeArmSubsystem m_intakeArmSubsystem;
    private IntakeSubsystem m_intakeSubsytem;
    private LimelightSubsystem m_limelightSubsystem;
    private SlideHookSubsystem m_slideHookSubsystem;
    private TelescopeHookSubsystem m_telescopeHookSubsystem;
    public CommandComposer(ArduinoSubsystem arduinoSubsystem, DriveSubsystem driveSubsystem, FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem, IndexerSubsystem indexerSubsystem, IntakeArmSubsystem intakeArmSubsystem, IntakeSubsystem intakeSubsystem, LimelightSubsystem limelightSubsystem, SlideHookSubsystem slideHookSubsystem, TelescopeHookSubsystem telescopeHookSubsystem){
        m_arduinoSubsystem = arduinoSubsystem;
        m_driveSubsystem = driveSubsystem;
        m_flywheelSubsystem = flywheelSubsystem;
        m_hoodSubsystem = hoodSubsystem;
        m_indexerSubsystem = indexerSubsystem;
        m_intakeArmSubsystem = intakeArmSubsystem;
        m_intakeSubsytem = intakeSubsystem;
        m_limelightSubsystem = limelightSubsystem;
        m_slideHookSubsystem = slideHookSubsystem;
        m_telescopeHookSubsystem = telescopeHookSubsystem;
    }
    public Command getAimAndShootCommand(String shootClass){

        //base of the hub is 8.75" offset from the tape at the top 
        double distanceBase = (m_limelightSubsystem.getDistance() - 8.75) / 12.0;
        
        Command aimCommand = new LimelightTurnCommand(m_limelightSubsystem, m_driveSubsystem);

        Command shootCommand = new SequentialCommandGroup(ShootCommandComposer.getShootCommand(m_flywheelSubsystem, m_hoodSubsystem, distanceBase, shootClass), 
                                                                    IndexerCommandComposer.getShootCommand(m_indexerSubsystem));

        return new SequentialCommandGroup(aimCommand, shootCommand);
    }

    public Command getClimbCommand(){
        SlideHookCommand SlideToStart = new SlideHookCommand(m_slideHookSubsystem, SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kStartPosition);
        TelescopeHookCommand TelescopeExtend= new TelescopeHookCommand(m_telescopeHookSubsystem, TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kExtendedPosition);
        DriveDistanceCommand DriveToBar=new DriveDistanceCommand(m_driveSubsystem,DriveConstants.toBarPosition); 
        TelescopeHookCommand TelescopeRetract = new TelescopeHookCommand(m_telescopeHookSubsystem, TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kRetractedPosition);
        SlideHookCommand SlideToTelescope=new SlideHookCommand(m_slideHookSubsystem, SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kToTelescopePosition);
        TelescopeHookCommand TelescopeRelease= new TelescopeHookCommand(m_telescopeHookSubsystem, TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromRetractedPosition);
        SlideUntilAngleCommand SlideToTelescopeBehind=new SlideUntilAngleCommand(m_slideHookSubsystem, SlideHookConstants.kTelescopeBehindRung);
        SlideUntilAngleCommand SlideToTelescopeTouching=new SlideUntilAngleCommand(m_slideHookSubsystem, SlideHookConstants.kTelescopeTouchingRung);
        TelescopeHookCommand TelescopeControlledRetract = new TelescopeHookCommand(m_telescopeHookSubsystem, TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kControlled);
        SlideHookCommand SlideControlledExtend=new SlideHookCommand(m_slideHookSubsystem, SlideHookCommand.Operation.CMD_POSITION, SlideHookConstants.kControlled);
        ParallelCommandGroup ControlledMove=new ParallelCommandGroup(TelescopeControlledRetract, SlideControlledExtend);
        TelescopeHookCommand TelescopeRetractFromControlled = new TelescopeHookCommand(m_telescopeHookSubsystem, TelescopeHookCommand.Operation.CMD_POSITION, TelescopeHookConstants.kDisengageFromControlledPosition);

        return new SequentialCommandGroup(SlideToStart, TelescopeExtend, DriveToBar, TelescopeRetract, SlideToTelescope, TelescopeRelease, SlideToTelescopeBehind, TelescopeExtend, SlideToTelescopeTouching, 
        TelescopeControlledRetract, SlideControlledExtend, ControlledMove, TelescopeRetractFromControlled, SlideToStart, TelescopeExtend, SlideToTelescope, TelescopeRelease, 
        SlideToTelescopeBehind, TelescopeExtend, SlideToTelescopeTouching, ControlledMove, TelescopeRetract);
    }
}

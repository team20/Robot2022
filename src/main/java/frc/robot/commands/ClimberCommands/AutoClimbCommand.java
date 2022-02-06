package frc.robot.commands.ClimberCommands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

import frc.robot.subsystems.SlideHookSubsystem;
import frc.robot.subsystems.TelescopeHookSubsystem;
import frc.robot.subsystems.DriveSubsystem;

import frc.robot.Constants;
import frc.robot.Constants.SlideHookConstants;
import frc.robot.Constants.TelescopeHookConstants;
import frc.robot.Constants.DriveConstants;


public class AutoClimbCommand extends SequentialCommandGroup {
    private final SlideHookSubsystem m_slideHookSubsystem;
	private final TelescopeHookSubsystem m_telescopeHookSubsystem;
    private final DriveSubsystem m_driveSubsystem;

    public AutoClimbCommand(SlideHookSubsystem slideHookSubsystem, TelescopeHookSubsystem telescopeHookSubsystem, DriveSubsystem driveSubsystem){
        m_slideHookSubsystem=slideHookSubsystem;
        m_telescopeHookSubsystem=telescopeHookSubsystem;
        m_driveSubsystem=driveSubsystem;
        
        //add navx and driving stuff
        addRequirements(slideHookSubsystem, telescopeHookSubsystem);

        SlideHookPositionCommand SlideToStart = new SlideHookPositionCommand(m_slideHookSubsystem, SlideHookConstants.kStartPosition);
        TelescopeHookPositionCommand TelescopeExtend= new TelescopeHookPositionCommand(m_telescopeHookSubsystem, TelescopeHookConstants.kExtendedPosition);
        DriveDistanceCommand DriveToBar=new DriveDistanceCommand(m_driveSubsystem,DriveConstants.toBarPosition); 
        TelescopeHookPositionCommand TelescopeRetract = new TelescopeHookPositionCommand(m_telescopeHookSubsystem, TelescopeHookConstants.kRetractedPosition);
        SlideHookPositionCommand SlideToTelescope=new SlideHookPositionCommand(m_slideHookSubsystem, SlideHookConstants.kToTelescopePosition);
        TelescopeHookPositionCommand TelescopeRelease= new TelescopeHookPositionCommand(m_telescopeHookSubsystem, TelescopeHookConstants.kDisengageFromRetractedPosition);
        SlideUntilAngleCommand SlideToTelescopeBehind=new SlideUntilAngleCommand(m_slideHookSubsystem, SlideHookConstants.kTelescopeBehindRung);
        SlideUntilAngleCommand SlideToTelescopeTouching=new SlideUntilAngleCommand(m_slideHookSubsystem, SlideHookConstants.kTelescopeTouchingRung);
        TelescopeHookPositionCommand TelescopeControlledRetract = new TelescopeHookPositionCommand(m_telescopeHookSubsystem, TelescopeHookConstants.kControlled);
        SlideHookPositionCommand SlideControlledExtend=new SlideHookPositionCommand(m_slideHookSubsystem, SlideHookConstants.kControlled);
        ParallelCommandGroup ControlledMove=new ParallelCommandGroup(TelescopeControlledRetract, SlideControlledExtend);
        TelescopeHookPositionCommand TelescopeRetractFromControlled = new TelescopeHookPositionCommand(m_telescopeHookSubsystem, TelescopeHookConstants.kDisengageFromControlledPosition);

        addCommands(SlideToStart, TelescopeExtend, DriveToBar, TelescopeRetract, SlideToTelescope, TelescopeRelease, SlideToTelescopeBehind, TelescopeExtend, SlideToTelescopeTouching, 
        TelescopeControlledRetract, SlideControlledExtend, ControlledMove, TelescopeRetractFromControlled, SlideToStart, TelescopeExtend, SlideToTelescope, TelescopeRelease, 
        SlideToTelescopeBehind, TelescopeExtend, SlideToTelescopeTouching, ControlledMove, TelescopeRetract);
        //TODO: make first slidetostart happen at the start of the match???
        //TODO: make DriveToDistanceCommand
        //TODO: make SlideUntilAngleCommand
    }
    //X slide hook starts at b inches _kStartPosition_ (a couple inches back from telescope)
    //X telescope up 40 _kExtendedPosition_ inches
    //X drive forward y inches
    //X telescope down 40 inches/to 0 _kRetractedPosition_
    //X slide hook forwards x inches _kToTelescopePosition_
    //X telescope hook up (5?) _kDisengageFromRetractedPosition_ inches to release from bar
    //X slide hook forwards until navx angle is (36?) _kTelescopeBehindRung_ degrees, then stop
    //X telescope hook up to reach 40 inches _kExtendedPosition_
    //X slide hook back z inches until navx angle is (32.6?) _kTelescopeTouchingRung_ degrees and telescope hook is touching bar
    //X telescope down AND slide hook back together (????????) _kControlled_
    //X telescope hook down a inches to disengage slide hook _kDisengageFromControlledPosition_
    //X slide hook to b _kStartPosition_
    //X telescope hook all the way up _kExtendedPosition_
    //X slide hook forwards to x inches _kToTelescopePosition_
    //X telescope hook up (5?) inches to release from bar _kDisengageFromRetractedPosition_
    //X slide hook forwards until navx angle is (36?) degrees, then stop _kTelescopeBehindRung_
    //X telescope hook up to reach 40 inches _kExtendedPosition_
    //X slide hook back z inches until navx angle is (32.6?) degrees and telescope hook is touching bar _kTelescopeTouchingRung_
    //X telescope down AND slide hook back together (????????) _kControlled_
    //X telescope hook down to 0 _kRetractedPosition_

}

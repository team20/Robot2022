package frc.robot.commands.LimelightCommands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.LimelightSubsystem;

public class LimelightOnCommand extends CommandBase {

    public LimelightOnCommand() {
        addRequirements(LimelightSubsystem.get());
    }

    public void initialize() {
        LimelightSubsystem.get().turnOnLight();
    }

    /**
     * Update the motor outputs
     */
    public void execute() {
    }

    /**
     * Stop the drivetrain at the end of the command
     */
    public void end(boolean interupted) {
    }

    public boolean isFinished() { // TODO: assumes you would only press the button once, no holding down
        return true;
        //return !LimelightSubsystem.get().isLightOn();
    }

}
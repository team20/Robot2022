package frc.robot.commands.ArduinoCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.ArduinoConstants;
import frc.robot.subsystems.ArduinoSubsystem;

public class UpdateShooterLEDsCommand extends CommandBase {
    // private ArduinoSubsystem m_arduinoSubsystem;
    private Supplier<Byte> m_shooterLEDMode;
    private Supplier<Byte> m_shooterLEDColor;

    public UpdateShooterLEDsCommand(Supplier<Byte> shooterLEDMode, Supplier<Byte> shooterLEDColor) {
        //m_arduinoSubsystem = arduinoSubsystem;
        m_shooterLEDMode = shooterLEDMode;
        m_shooterLEDColor = shooterLEDColor;
        addRequirements(ArduinoSubsystem.get());
    }

	@Override
    public void execute() {
        
        ArduinoSubsystem.get().setShooterLEDMode(m_shooterLEDMode.get());
        ArduinoSubsystem.get().setShooterLEDColor(m_shooterLEDColor.get());
        // System.out.println("shooter led mode: " + m_shooterLEDMode.get());
        // System.out.println("shooter led color: " + m_shooterLEDValue.get());
    }

    @Override
    public void end(boolean interrupted) { //TODO could cause problems....
        ArduinoSubsystem.get().setShooterLEDMode(ArduinoConstants.LEDModes.kOff);
    }
}
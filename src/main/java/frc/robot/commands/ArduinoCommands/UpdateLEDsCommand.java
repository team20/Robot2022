package frc.robot.commands.ArduinoCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.ArduinoConstants;
import frc.robot.subsystems.ArduinoSubsystem;

public class UpdateLEDsCommand extends CommandBase {
    private ArduinoSubsystem m_arduinoSubsystem;
    private Supplier<Byte> m_mainLEDMode;
    private Supplier<Byte> m_mainLEDValue;
    private Supplier<Byte> m_shooterLEDMode;
    private Supplier<Byte> m_shooterLEDValue;

    public UpdateLEDsCommand(ArduinoSubsystem arduinoSubsystem, Supplier<Byte> mainLEDMode,
    Supplier<Byte> mainLEDValue, Supplier<Byte> shooterLEDMode, Supplier<Byte> shooterLEDValue) {
        m_arduinoSubsystem = arduinoSubsystem;
        m_mainLEDMode = mainLEDMode;
        m_mainLEDValue = mainLEDValue;
        m_shooterLEDMode = shooterLEDMode;
        m_shooterLEDValue = shooterLEDValue;
        addRequirements(m_arduinoSubsystem);
    }

	@Override
    public void execute() {
        
        m_arduinoSubsystem.setMainLEDMode(m_mainLEDMode.get());
        m_arduinoSubsystem.setMainLEDValue(m_mainLEDValue.get());
        m_arduinoSubsystem.setShooterLEDMode(m_shooterLEDMode.get());
        m_arduinoSubsystem.setShooterLEDValue(m_shooterLEDValue.get());
        // System.out.println("main led mode: " + m_mainLEDMode.get());
        // System.out.println("main led color: " + m_mainLEDValue.get());
        // System.out.println("shooter led mode: " + m_shooterLEDMode.get());
        // System.out.println("shooter led color: " + m_shooterLEDValue.get());
    }

    @Override
    public void end(boolean interrupted) {
        m_arduinoSubsystem.setMainLEDMode(ArduinoConstants.LEDModes.kOff);
        m_arduinoSubsystem.setShooterLEDMode(ArduinoConstants.LEDModes.kOff);
    }
}
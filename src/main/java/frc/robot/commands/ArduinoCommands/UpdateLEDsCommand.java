package frc.robot.commands.ArduinoCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.ArduinoConstants;
import frc.robot.subsystems.ArduinoSubsystem;

public class UpdateLEDsCommand extends CommandBase {
    // private ArduinoSubsystem m_arduinoSubsystem;
    //private Supplier<Byte> m_mainLEDMode;
    private Supplier<Byte> m_mainLEDColor;
    //private Supplier<Byte> m_shooterLEDMode;
    private Supplier<Byte> m_shooterLEDColor;
    private Supplier<Byte> m_climberLEDMode;
    private Supplier<Byte> m_climberLEDColor;

    public UpdateLEDsCommand(Supplier<Byte> mainLEDMode,
    Supplier<Byte> mainLEDColor, Supplier<Byte> shooterLEDMode, Supplier<Byte> shooterLEDColor,
    Supplier<Byte> climberLEDMode, Supplier<Byte> climberLEDColor) {
        //m_arduinoSubsystem = arduinoSubsystem;
        //m_mainLEDMode = mainLEDMode;
        m_mainLEDColor = mainLEDColor;
        //m_shooterLEDMode = shooterLEDMode;
        m_shooterLEDColor = shooterLEDColor;
        m_climberLEDMode = climberLEDMode;
        m_climberLEDColor = climberLEDColor;
        addRequirements(ArduinoSubsystem.get());
    }

	@Override
    public void execute() {
        
        //ArduinoSubsystem.get().setMainLEDMode(m_mainLEDMode.get());
        ArduinoSubsystem.get().setMainLEDColor(m_mainLEDColor.get());
        //ArduinoSubsystem.get().setShooterLEDMode(m_shooterLEDMode.get());
        ArduinoSubsystem.get().setShooterLEDColor(m_shooterLEDColor.get());
        ArduinoSubsystem.get().setClimberLEDMode(m_climberLEDMode.get());
        ArduinoSubsystem.get().setClimberLEDColor(m_climberLEDColor.get());
        // System.out.println("main led mode: " + m_mainLEDMode.get());
        // System.out.println("main led color: " + m_mainLEDValue.get());
        // System.out.println("shooter led mode: " + m_shooterLEDMode.get());
        // System.out.println("shooter led color: " + m_shooterLEDValue.get());
        // System.out.println("climber led mode: " + m_climberLEDMode.get());
        // System.out.println("climber led color: " + m_climberLEDValue.get());
    }

    @Override
    public void end(boolean interrupted) { //TODO could cause problems....
        //ArduinoSubsystem.get().setMainLEDMode(ArduinoConstants.LEDModes.kOff);
        //ArduinoSubsystem.get().setShooterLEDMode(ArduinoConstants.LEDModes.kOff);
        ArduinoSubsystem.get().setClimberLEDMode(ArduinoConstants.LEDModes.kOff);
    }
}
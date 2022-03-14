package frc.robot.commands.ArduinoCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.ArduinoConstants;
import frc.robot.subsystems.ArduinoSubsystem;

public class UpdateClimberLEDsCommand extends CommandBase {
    // private ArduinoSubsystem m_arduinoSubsystem;
    private Supplier<Byte> m_climberLEDMode;
    private Supplier<Byte> m_climberLEDColor;

    public UpdateClimberLEDsCommand(Supplier<Byte> climberLEDMode, Supplier<Byte> climberLEDColor) {
        //m_arduinoSubsystem = arduinoSubsystem;
        m_climberLEDMode = climberLEDMode;
        m_climberLEDColor = climberLEDColor;
        addRequirements(ArduinoSubsystem.get());
    }

	@Override
    public void execute() {
        
        ArduinoSubsystem.get().setClimberLEDMode(m_climberLEDMode.get());
        ArduinoSubsystem.get().setClimberLEDColor(m_climberLEDColor.get());
        // System.out.println("climber led mode: " + m_climberLEDMode.get());
        // System.out.println("climber led color: " + m_climberLEDValue.get());
    }

    @Override
    public void end(boolean interrupted) { //TODO could cause problems....
        ArduinoSubsystem.get().setClimberLEDMode(ArduinoConstants.LEDModes.kOff);
    }
}
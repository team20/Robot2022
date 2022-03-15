package frc.robot.commands.ArduinoCommands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.ArduinoConstants;
import frc.robot.subsystems.ArduinoSubsystem;

public class UpdateMainLEDsCommand extends CommandBase {
    // private ArduinoSubsystem m_arduinoSubsystem;
    private Supplier<Byte> m_mainLEDMode;
    private Supplier<Byte> m_mainLEDColor;

    public UpdateMainLEDsCommand(Supplier<Byte> mainLEDMode, Supplier<Byte> mainLEDColor) {
        //m_arduinoSubsystem = arduinoSubsystem;
        m_mainLEDMode = mainLEDMode;
        m_mainLEDColor = mainLEDColor;
        addRequirements(ArduinoSubsystem.get());
    }

	@Override
    public void execute() {
        
        //ArduinoSubsystem.get().setMainLEDMode(m_mainLEDMode.get());
        ArduinoSubsystem.get().setMainLEDColor(m_mainLEDColor.get());
        // System.out.println("main led mode: " + m_mainLEDMode.get());
        // System.out.println("main led color: " + m_mainLEDValue.get());
    }

    @Override
    public void end(boolean interrupted) { //TODO could cause problems....
        //ArduinoSubsystem.get().setMainLEDMode(ArduinoConstants.LEDModes.kOff);
    }
}
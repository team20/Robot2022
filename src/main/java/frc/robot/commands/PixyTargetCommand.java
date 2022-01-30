package frc.robot.commands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.ArduinoSubsystem;
import frc.robot.subsystems.DriveSubsystem;

public class PixyTargetCommand extends CommandBase {

    private final DriveSubsystem m_driveSubsystem;
    private final ArduinoSubsystem m_arduinoSubsystem;
    private Supplier<Double> m_speed;

    /**
     * Initializes a new instance of the {@link TargetCommand} class.
     * 
     * @param driveSubsystem   {@link DriveSubsystem} to be used.
     * @param arduinoSubsystem {@link ArduinoSubsystem} to be used.
     */
    public PixyTargetCommand(DriveSubsystem driveSubsystem, ArduinoSubsystem arduinoSubsystem, Supplier<Double> speed) {
        m_driveSubsystem = driveSubsystem;
        m_arduinoSubsystem = arduinoSubsystem;
        m_speed = speed;
        addRequirements(m_driveSubsystem, m_arduinoSubsystem);
    }

    public void execute() {
        // read and write Arduino data
        m_arduinoSubsystem.update();
        // if target is in the camera's view
        System.out.println("BALL BOOLEAN: "+m_arduinoSubsystem.getTargetInView() );
        if (m_arduinoSubsystem.getTargetInView()){
            // drive based on Arduino data
            System.out.println("SEEN A BALL");
            m_driveSubsystem.arcadeDrive(((m_arduinoSubsystem.getDistance()-10)/10)*Math.max(m_speed.get(), 0.4), -m_arduinoSubsystem.getTurnSpeed(),
                    m_arduinoSubsystem.getTurnSpeed());
        }
        else{
            m_driveSubsystem.tankDrive(0, 0);
            System.out.println("NO BALL SEEN");
        }
        m_arduinoSubsystem.setMainLEDMode((byte)1);
    }
}
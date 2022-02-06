package frc.robot.commands.ShooterCommands;

import java.util.function.Supplier;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.LinearRangeFinder;
import frc.robot.RangeFinder;
import frc.robot.RegressionRangeFinder;
import frc.robot.Constants.FieldLocation;
import frc.robot.subsystems.FlywheelSubsystem;
import frc.robot.subsystems.HoodSubsystem;

public class ShootSetupCommand extends CommandBase {

    private final FlywheelSubsystem m_flywheelSubsystem;
    private final HoodSubsystem m_hoodSubsystem;
    private double m_flywheelSetpoint, m_hoodSetpoint;
    private String m_name;
    private RangeFinder m_distanceClass;
    private double m_distance;
    private boolean m_preset;

    /**
     * Setup the flywheel and hood
     * 
     * @param flywheelSubsystem The flywheel subsystem to be used
     * @param hoodSubsystem     The hood subsystem to be used
     * @param flywheelSetpoint  The flywheel setpoint
     * @param hoodSetpoint      The hood setpoint
     */
    public ShootSetupCommand(FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem, double flywheelSetpoint,
            double hoodSetpoint) {
        m_flywheelSubsystem = flywheelSubsystem;
        m_hoodSubsystem = hoodSubsystem;
        m_flywheelSetpoint = flywheelSetpoint;
        m_hoodSetpoint = hoodSetpoint;
        m_preset = true;
        addRequirements(m_flywheelSubsystem, m_hoodSubsystem);
    }

    /**
     * Setup the flywheel and hood
     * 
     * @param flywheelSubsystem The flywheel subsystem to be used
     * @param hoodSubsystem     The hood subsystem to be used
     * @param fieldLocation     The field location to get other setpoints from
     */
    public ShootSetupCommand(FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem,
            Supplier<FieldLocation> fieldLocation) {
        m_flywheelSubsystem = flywheelSubsystem;
        m_hoodSubsystem = hoodSubsystem;
        m_flywheelSetpoint = fieldLocation.get().flywheelSetpoint;
        m_hoodSetpoint = fieldLocation.get().hoodSetpoint;
        m_preset = true;
        addRequirements(m_flywheelSubsystem, m_hoodSubsystem);
    }

    public ShootSetupCommand(FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem, double distance,
            String shootClass) {
        m_flywheelSubsystem = flywheelSubsystem;
        m_hoodSubsystem = hoodSubsystem;
        m_distance = distance;
        m_name = shootClass;
        m_preset = false;
        addRequirements(m_flywheelSubsystem, m_hoodSubsystem);
    }

    /**
     * Set the setpoints of the flywheel and hood
     */
    public void initialize() {
        if (m_preset) {
            m_flywheelSubsystem.setVelocity(m_flywheelSetpoint);
            m_hoodSubsystem.setPosition(m_hoodSetpoint);
        } else {
            if (m_name.equals("LINEAR")) {
                m_distanceClass = new LinearRangeFinder();
            } else if (m_name.equals("REGRESSION")) {
                m_distanceClass = new RegressionRangeFinder();
            }

            m_hoodSetpoint = m_distanceClass.getAngleAndRPM(m_distance)[0];
            m_flywheelSetpoint = m_distanceClass.getAngleAndRPM(m_distance)[1];

            m_flywheelSubsystem.setVelocity(m_flywheelSetpoint);
            m_hoodSubsystem.setPosition(m_hoodSetpoint);
        }
    }

    /**
     * Stop the flywheel and reset the hood at the end of the command
     */
    public void end(boolean interrupted) {
        m_flywheelSubsystem.setVelocity(0);
        m_hoodSubsystem.setPosition(0);
    }
}

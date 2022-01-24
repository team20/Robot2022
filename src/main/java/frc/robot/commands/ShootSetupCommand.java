package frc.robot.commands;

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
    // private NetworkTable m_shooterTest =
    // NetworkTableInstance.getDefault().getTable("TEST_LIMELIGHT");
    private String m_name;
    private RangeFinder m_distanceClass;
    private double m_distance;

    // public ShootSetupCommand(FlywheelSubsystem flywheelSubsystem, HoodSubsystem
    // hoodSubsystem, double distance) {
    // m_flywheelSubsystem = flywheelSubsystem;
    // m_hoodSubsystem = hoodSubsystem;

    // if (distance > 113) { //change based on min ability to see target
    // m_flywheelSetpoint = 4000;
    // m_hoodSetpoint = 15;
    // } else {
    // m_flywheelSetpoint = 3300;
    // m_hoodSetpoint = 4;
    // }

    // //for over 113, rpm 4100, angle 15
    // //for under 113, rpm 3300, angle 4

    // addRequirements(m_flywheelSubsystem, m_hoodSubsystem);
    // }

    /**
     * Setup the flywheel and hood
     * 
     * @param flywheelSubsystem The flywheel subsystem to be used
     * @param hoodSubsystem     The hood subsystem to be used
     * @param flywheelSetpoint  The flywheel setpoint
     * @param hoodSetpoint      The hood setpoint
     */
    // public ShootSetupCommand(FlywheelSubsystem flywheelSubsystem, HoodSubsystem
    // hoodSubsystem, double flywheelSetpoint,
    // double hoodSetpoint) {
    // m_flywheelSubsystem = flywheelSubsystem;
    // m_hoodSubsystem = hoodSubsystem;
    // m_flywheelSetpoint = flywheelSetpoint;
    // m_hoodSetpoint = hoodSetpoint;
    // addRequirements(m_flywheelSubsystem, m_hoodSubsystem);
    // }

    /**
     * Setup the flywheel and hood
     * 
     * @param flywheelSubsystem The flywheel subsystem to be used
     * @param hoodSubsystem     The hood subsystem to be used
     * @param fieldLocation     The field location to get other setpoints from
     */
    // public ShootSetupCommand(FlywheelSubsystem flywheelSubsystem, HoodSubsystem
    // hoodSubsystem,
    // Supplier<FieldLocation> fieldLocation) {
    // m_flywheelSubsystem = flywheelSubsystem;
    // m_hoodSubsystem = hoodSubsystem;
    // //m_flywheelSetpoint = fieldLocation.get().flywheelSetpoint;
    // //m_hoodSetpoint = fieldLocation.get().hoodSetpoint;
    // addRequirements(m_flywheelSubsystem, m_hoodSubsystem);
    // }

    public ShootSetupCommand(FlywheelSubsystem flywheelSubsystem, HoodSubsystem hoodSubsystem, double distance,
            String shootClass) {
        m_flywheelSubsystem = flywheelSubsystem;
        m_hoodSubsystem = hoodSubsystem;
        m_distance = distance;
       // System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
       // System.out.println("Distance: " + m_distance);
        m_name = shootClass;
        addRequirements(m_flywheelSubsystem, m_hoodSubsystem);
    }

    /**
     * Set the setpoints of the flywheel and hood
     */
    public void initialize() {
        // m_hoodSetpoint = m_shooterTest.getEntry("Angle").getDouble(0);
        // m_flywheelSetpoint = m_shooterTest.getEntry("Velocity").getDouble(0);
        // System.out.println("Flywheelsetpoint: " + m_flywheelSetpoint);
        // System.out.println("Hoodsetpoint: " + m_hoodSetpoint);
        // m_flywheelSubsystem.setVelocity(m_flywheelSetpoint);
        // m_hoodSubsystem.setPosition(m_hoodSetpoint);
        if (m_name.equals("LINEAR")) {
            m_distanceClass = new LinearRangeFinder();
        } else if (m_name.equals("REGRESSION")) {
            m_distanceClass = new RegressionRangeFinder();
        }
        for (int i = 0; i <1000; i++) {        
            System.out.println("This is the hood setpoint: " + m_distanceClass.getAngleAndRPM(m_distance)[0]);
     }
        m_hoodSetpoint = m_distanceClass.getAngleAndRPM(m_distance)[0];
        m_flywheelSetpoint = m_distanceClass.getAngleAndRPM(m_distance)[1];
       
        // if (m_hoodSetpoint > 8) {
        //     m_hoodSetpoint = 0;
        // }
        m_flywheelSubsystem.setVelocity(m_flywheelSetpoint);
        m_hoodSubsystem.setPosition(m_hoodSetpoint);
    }

    /**
     * Stop the flywheel and reset the hood at the end of the command
     */
    public void end(boolean interrupted) {
        m_flywheelSubsystem.setVelocity(0);
        m_hoodSubsystem.setPosition(0);
    }
}

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.DriveConstants;

public class DriveSubsystem extends SubsystemBase {

        private final TalonSRX m_frontLeft = new TalonSRX(4);
        private final TalonSRX m_frontRight = new TalonSRX(5); 

        public DriveSubsystem() {
                m_frontLeft.setInverted(true);
        }

        public void periodic() {
               
        }

        public void arcadeDrive(double straight, double left, double right) {
                tankDrive(DriveConstants.kSpeedLimitFactor * (straight + left - right),
                                DriveConstants.kSpeedLimitFactor * (straight - left + right));
        }

        public void tankDrive(double leftSpeed, double rightSpeed) {
               System.out.println("left speed: " + leftSpeed);
               System.out.println("right speed: " + rightSpeed);

                m_frontLeft.set(ControlMode.PercentOutput, leftSpeed);
                m_frontRight.set(ControlMode.PercentOutput, rightSpeed);

        }

}

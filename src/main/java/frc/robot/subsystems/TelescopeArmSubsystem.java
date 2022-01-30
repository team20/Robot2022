// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardContainer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.TelescopeArmConstants;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

public class TelescopeArmSubsystem extends SubsystemBase{
  private final CANSparkMax m_motor=new CANSparkMax(TelescopeArmConstants.kMotorPort, MotorType.kBrushless);
	private final SparkMaxPIDController m_neoController=m_motor.getPIDController();
	private final RelativeEncoder m_neoEncoder=m_motor.getEncoder();
  private double m_setPosition;

  public TelescopeArmSubsystem() {
    m_motor.setIdleMode(IdleMode.kBrake);
    m_motor.enableVoltageCompensation(12);
    m_motor.setInverted(TelescopeArmConstants.kInvert);

    m_neoController.setP(TelescopeArmConstants.kP);
    m_neoController.setI(TelescopeArmConstants.kI);
    m_neoController.setD(TelescopeArmConstants.kD);
    m_neoController.setIZone(TelescopeArmConstants.kIz);
    m_neoController.setFF(TelescopeArmConstants.kFF);
    m_neoController.setOutputRange(TelescopeArmConstants.kMinOutput,TelescopeArmConstants.kMaxOutput);
  }

  @Override
  public void periodic(){
		if (m_setPosition == 0) {
            m_motor.stopMotor();
        } else {
            m_neoController.setReference(m_setPosition, ControlType.kPosition, 0);
        }
	}
    
	
    public void incrementPosition() {
        setPosition(m_setPosition + 50);
    }

    public void decrementPosition() {
        setPosition(m_setPosition - 50);
    }

    /**
     * @return Current setpoint.
     */
    public double getSetpoint() {
        return m_setPosition;
    }

    /**
     * @return Measured velocity.
     */
    public double getPosition() {
        return m_neoEncoder.getPosition();
    }

    /**
     * Sets target speed for flywheel.
     * 
     * @param velocity Target velocity (rpm).
     */
    public void setPosition(double position) {
        m_setPosition = position;
    }

    /**
     * @return Whether the flywheel is at its setpoint ABOVE 0
     */
    public boolean atSetpoint() {
        return getSetpoint() > 0
                ? (Math.abs(getPosition() - getSetpoint()) / getSetpoint())
                        * 100 < TelescopeConstants.kAllowedErrorPercent
                : false;
    }
    public void reset(){
        m_neoEncoder.setPosition(0);
    }

  @Override
  public void simulationPeriodic() {
    // This method will be called once per scheduler run during simulation
  }
}

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.SoftLimitDirection;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import frc.robot.Constants.IndexerConstants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class IndexerSubsystem extends SubsystemBase{
    // private final VictorSPX m_motor = new VictorSPX(Constants.IndexerConstants.kMotorPort);
	private final CANSparkMax m_motor=new CANSparkMax(IndexerConstants.kMotorPort, MotorType.kBrushless);
	private final SparkMaxPIDController m_neoController=m_motor.getPIDController();
	private final RelativeEncoder m_neoEncoder=m_motor.getEncoder();
	private double m_setPosition;

	public IndexerSubsystem() {
		// m_motor.setNeutralMode(NeutralMode.Coast);
		// m_motor.enableVoltageCompensation(true);
		// m_motor.setInverted(true);
		m_motor.setIdleMode(IdleMode.kBrake);
		m_motor.enableVoltageCompensation(12);
		m_motor.setInverted(false);

		m_neoController.setP(IndexerConstants.kP);
        m_neoController.setI(IndexerConstants.kI);
        m_neoController.setD(IndexerConstants.kD);
        m_neoController.setIZone(IndexerConstants.kIz);
        m_neoController.setFF(IndexerConstants.kFF);
        m_neoController.setOutputRange(IndexerConstants.kMinOutput,IndexerConstants.kMaxOutput);
	}
	
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
                        * 100 < IndexerConstants.kAllowedErrorPercent
                : false;
    }
    public void reset(){
        m_neoEncoder.setPosition(0);
    }
}

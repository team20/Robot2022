// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import java.time.Duration;
import java.time.Instant;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.commands.ShooterCommands.HoodCommand;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.HoodSubsystem;
import frc.robot.subsystems.IntakeArmSubsystem;
import frc.robot.subsystems.TelescopeHookSubsystem;

public class ZeroCommand extends CommandBase {

    Instant m_startTime;

    public ZeroCommand() {
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(DriveSubsystem.get(), HoodSubsystem.get(), IntakeArmSubsystem.get(),
                TelescopeHookSubsystem.get());
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
        m_startTime = Instant.now();
        DriveSubsystem.get().resetEncoders();
        DriveSubsystem.get().zeroHeading();
        HoodSubsystem.get().resetEncoder();
        IntakeArmSubsystem.get().resetEncoder();
        TelescopeHookSubsystem.get().resetEncoder();
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {

    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        double elapsed = Duration.between(m_startTime, Instant.now()).toMillis();
        if (elapsed < 250) {
            return false;
        } return true;
    }
}

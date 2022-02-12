// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

// NOTE:  Consider using this command inline, rather than writing a subclass.  For more
// information, see:
// https://docs.wpilib.org/en/stable/docs/software/commandbased/convenience-features.html
public class SequentialCommandGroupWithEnd extends SequentialCommandGroup {
  /** Creates a new SequentialCommandGroupWithEnd. */
  Command m_endCommand;
  public SequentialCommandGroupWithEnd(Command endCommand, Command...commands) {
    m_endCommand = endCommand;
    // Add your commands in the addCommands() call, e.g.
    // addCommands(new FooCommand(), new BarCommand());
    addCommands(commands);
  }
  @Override
  public void end(boolean interrupted) {
    // TODO Auto-generated method stub
    super.end(interrupted);
    m_endCommand.initialize();
    m_endCommand.execute();
    m_endCommand.end(false);
  }
}

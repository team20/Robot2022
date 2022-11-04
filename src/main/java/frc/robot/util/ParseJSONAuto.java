package frc.robot.util;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import org.codehaus.groovy.control.CompilationFailedException;

import edu.wpi.first.wpilibj2.command.Command;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;

public class ParseJSONAuto {

  private static Hashtable<String,Command> commands;

  /**
   * Run a groovy script that builds autonomous commands based on the contents of a text file.
   * Note that this method will give a warning about an illegal access operation
   * according to stack overflow this is normal for newer versions of java
   * and won't cause any problems
   */
  public static void parse() {
    
    //get the script to generate our auto commands
    File script = new File("src/main/java/frc/robot/scripts/testParse.gvy");
    
    //create variable bindings for the shell so we can get output
    Binding b = new Binding();
    commands = new Hashtable<String, Command>();
    b.setVariable("output", commands);

    //create the shell to run our script with our bindings
    GroovyShell shell = new GroovyShell(b);

    //run our script and catch and print any errors that arise
    //the output will automatically be set by the script
    //using the variable binding we gave it
    try {
      shell.evaluate(script);
    } catch (CompilationFailedException e) {
      e.printStackTrace();
    } catch(IOException e){
      e.printStackTrace();
    }
  }

  /**
   * Return the auto command associated with the name you want to run
   * @param name the name of the command/command group
   * @return the command you requested
   */
  public static Command getAutoCommand(String name){
    return commands.get(name);
  }
}

import groovy.json.JsonSlurper
import java.util.regex.Matcher
import edu.wpi.first.wpilibj2.command.Command

//get a JSON file that contains Command Bindings as well as
//all the imports necessary to build the auto command
//eg. sequentialCommandGroup, any commands you are running, etc
//make sure you use the full name (edu.wpi.first..., frc.robot...)
//each binding should be in the format "[human readable name]" : "Command to replace that name"
//Eg. "sequential" : "new SequentialCommandGroup()"

bindingsAndImportsFile = new File('src/main/java/frc/robot/scripts/bindingsandimports.json')

//Get a file which contains the autos you would like to build
//Any commands which are nested as parameters to a different command
//should be tabbed in once underneath that command
//The level with no tabs should contain the name of the command only,
//not any actual commands to be run
autoFile = new File('src/main/java/frc/robot/scripts/autos.txt')

//parse the JSON file
bindingsAndImports = new JsonSlurper().parseText(bindingsAndImportsFile.text)

//get the text of the text file
autoText = autoFile.text

//arrays to hold the text of the auto command to be run
//and the names of the auto commands
commandText = []
name = []

//keep track of number of tabs
//this is used so we know when to add end parentheses
//and to know how many we need to add
lastTabs = 0;
currInd = -1
timeoutTabs = -1
timeoutTime = 0
lineNum = 0;
//go line by line through the text file
autoText.eachLine{
	++lineNum;
	//only do this if the line isn't empty and there are no "//"(comments)
	if(it != "" && !it.contains("//")){
		//count and save the number of tab characters we currently see
		it = it.replace("    ", "\t")
		tabs = it.count("\t")

		//if there are currently no tabs, this is a new Auto Command
		if(tabs == 0){
			//start saving in the next index of the array
			currInd++
			//add the necessary end parentheses for the last command
			if(currInd > 0){
				commandText[currInd - 1] += ")".repeat(lastTabs)
			}
			
			//save the name of this auto
			name[currInd] = it

			//start off the string at this array index
			//otherwise we will get "null" at the beginning of our string
			commandText[currInd] = ""
			println(commandText[currInd])
		} else{

			//if this command is at the same level as the last command,
			//close the last command and continue forward
			//if this command is a level below the last command,
			//don't close the last command, as this command should go inside the last command
			//if this command is at a level above the last command
			//close the last command and the ones for the levels we've now moved to
			if(lastTabs == tabs){
				commandText[currInd] += ")"
				if(timeoutTabs == tabs || timeoutTabs == tabs+1){
					commandText[currInd] += ".withTimeout(" + timeoutTime + ")"
					timeoutTabs = -1
					timeoutTime = 0
				}
				commandText[currInd] += ", "
			}else if(lastTabs > tabs){
				commandText[currInd] += ")"
				if(timeoutTabs == tabs|| timeoutTabs == tabs+1){
					commandText[currInd] += ".withTimeout(" + timeoutTime + ")"
					timeoutTabs = -1
					timeoutTime = 0
				}
				commandText[currInd] += ")".repeat(lastTabs - tabs)
				commandText[currInd] += ", "
			}else{
				if(commandText[currInd].length() != 0 && commandText[currInd].charAt(commandText[currInd].length()-1) != '('){
					commandText[currInd] += ","
				}
			}

			//split the line into the command and the parameter(if applicable)
			ar = it.replace("    ","\t").split(" ")
			command = ar[0].replace("\t", "")

			//search the command bindings for the binding to your current desired command

			try{
				commandFromBinding = bindingsAndImports.CommandBindings.find{it.key == command}.value
			}catch(e){
				println("error at:" + command + ar+ " at line: " + lineNum)
			}
			
			//if we have a parameter to the command replace the param in the binding to the actual parameter we want
			//otherwise just a the command, minus its end parenthesis
			if(ar.size() > 1){
				startParam = 2;
				if(ar[1] == "operation"){
					if(commandFromBinding.substring(0,5) != "Comm"){
						commandText[currInd] += "" + commandFromBinding.substring(0,commandFromBinding.length()-1)+commandFromBinding.substring(4,commandFromBinding.length()-2)+".Operation.CMD_"+ar[2]
					} else{
						commandText[currInd] += "" + commandFromBinding.substring(0,commandFromBinding.length()-1) + ar[2]
					}
					startParam = 3
				} else if(ar[1] == "timeout"){
					commandText[currInd] += commandFromBinding.substring(0,commandFromBinding.length()-1)
					timeoutTabs = tabs
					timeoutTime = ar[2]
					startParam = 3
				}else{
					commandText[currInd] += commandFromBinding.substring(0,commandFromBinding.length()-1) + ar[1]
				}
				
				for(i = startParam; i< ar.length; ++i){
					if(ar[i] == "operation"){
						if(commandFromBinding.substring(0,5) != "Comm"){
							commandText[currInd] += "," + commandFromBinding.substring(4,commandFromBinding.length()-2)+".Operation.CMD_"+ar[++i]
						}else{
							commandText[currInd] += "," +ar[++i]
						}
						break
					}
					if(ar[i] == "timeout"){
						timeoutTabs = tabs
						timeoutTime = ar[++i]
						break;
					}
					commandText[currInd] += ", " + ar[i] 
				}
			}else{
				commandText[currInd] += commandFromBinding.substring(0,commandFromBinding.length()-1)
			}

			
		}
		lastTabs = tabs	
	}
}
println(lastTabs)
//add the necessary end parentheses for the last command
commandText[currInd] += ")".repeat(lastTabs)

//build a string containing the imports necessary to create the commands
imports = ""
bindingsAndImports.Imports.each{
	imports += "import " + it + "; "
}

ind = 0
for(i in commandText){
	//evaluate the string we built for each auto command
	//to get the actual commands we want
	//and save it in the right place
	
	//println(name[ind] + " " + commandText)
	try{
		output.put(name[ind], Eval.me(imports + i))
	} catch(e){
		println(name[ind] + " " + i)
		println(e)
		a = 0/0
	}
	++ind;
}
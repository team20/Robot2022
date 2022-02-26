import tkinter as tk
from tkinter import *
from tkinter import ttk
from networktables import NetworkTables
import time

#intialize network tables and get the smart dashboard
NetworkTables.initialize(server='10.0.20.2')
sd = NetworkTables.getTable('SmartDashboard')


def update():
	#clear the screen
	c.delete("all")

	#Get values from network tables
	colorString = sd.getEntry("Color String").getString("")
	rtf = sd.getEntry("Indexer RTF").getBoolean(False)
	bic = sd.getEntry("Indexer BIC").getBoolean(False)
	

	#3 side by side circles, colors depend on the values from the network tables
	c.create_oval(0,0,100,100, fill = "green" if rtf else "white")
	c.create_oval(100,0,200,100, fill = "green" if bic else "white")
	c.create_oval(200,0,300,100, fill = "blue" if colorString == "Blue" else "red" if colorString=="Red" else "white")
	
	#run every 100 milliseconds
	root.after(100, update)

#setup tkinter (width of the screen, 100 px tall)
root = tk.Tk()
root.geometry(str(root.winfo_screenwidth())+"x100-1+" + str(root.winfo_screenheight() -330))
c= tk.Canvas(root)
c.pack()

#start sensor update loop
update()
root.mainloop()
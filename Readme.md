# Abap Action Chain: Eclipse Plugin for ABAP Development ##

Abap Action Chain is a powerful Eclipse plugin designed to streamline ABAP development by enabling users to manage chains of actions. 
With this plugin, developers can automate repetitive tasks throughout their ABAP projects.

![Action Chain](ActionChain.PNG "Action Chain")


##  Install 
In Eclipse -> Help -> Install New Software and add Url: 

	https://pardonmycode.github.io/Abap-Action-Chain/pluginSite/



##  Install local 
### Step 0

	git clone https://github.com/pardonmycode/Abap-Action-Chain

### Step 1
In Eclipse: 
>	Go to File -> New -> Other -> Plug-in Project 
	
In the Plug-in creation menu:
>   Project name: AbapPluginName  \
>		uncheck "Use default location" \
>		and set the Location to the "git clone"-Path and Finish. 
	

### Step 2
In Eclipse: 
> 
> Create new empty Feature Project \
> go to feature.xml  \
> include your newly created Plug-in Project "AbapPluginName" under the Tab "Included Plug-ins"  

> click in upper right corner "Export deployable Plug-ins.." \
> Now you should have a jar-Files( content.jar, artifacts.jar etc..), \
> this location can be install via Help->Install New Software -> Add -> Local  

> Remember to uncheck "Group items by kategory " in the install menu  





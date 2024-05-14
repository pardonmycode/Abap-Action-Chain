## Abap Action Chain: Eclipse Plugin for ABAP Development ##

Abap Action Chain is a powerful Eclipse plugin designed to streamline ABAP development by enabling users to manage chains of actions. 
With this plugin, developers can automate repetitive tasks throughout their ABAP projects.

# Install 
In Eclipse -> Help -> Install New Software and add Url: 

https://pardonmycode.github.io/Abap-Action-Chain/pluginSite/



# Install local 

git clone https://github.com/pardonmycode/Abap-Action-Chain

In Eclipse: 
>	Go to File -> New -> Other -> Plug-in Project 
	
>	In the Plug-in creation menu:
>		uncheck "Use default location" 
>		and set the Location to the "git clone"-Path and Finish.
	
> go to pulgin.xml and click in upper right corner "Export deployable Plug-ins.."
> Now you should have a jar-File, 
> that can be install via Help->Install New Software -> Add -> Archive




![Action Chain](ActionChain.PNG "Action Chain")
Readme

JSL organization

The folder JSL contains everything related to the JSL

files:
JSLLicense.txt 
	- The GPL license for the JSL
buildJSLdocs.xml 
	- An Ant script for generating JSL java docs
	- properties of script must be modified for UMLGraph and yDoc based on installation
	- usage: run ant from directory JSL
		 ant -f buildJSLdocs.xml help
		

folders:
nbprojects - NetBeans projects
	JSLCore - The main JSL library as a Netbeans project
	JSLExamples - Examples using the JSL as a Netbeans project, requires JSLCore.jar
			project dependencies: JSLCore
	JSLTesting - Unit tests for the JSL as a Netbeans project,
			project dependencies: JSLCore, JSLExamples
			libraries: see /lib folder

documentation - Holds documentation related to the JSL
	javadoc - Generated from JSLCore


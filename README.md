# Setup with  IntelliJ
if you cannot start the app because a NullPointerException
1. File -> Settings -> Build,Execution, Deployment -> Compiler
2. Add ";!?*.css;!?*.fxml" to the resource pattern
3. Right Click on ProjectName -> Open Module Settings -> Libraries -> + -> Java
jars (with shifts pressed: 
    - lib/graph-editor-api-11.0.7.jar
    - lib/graph-editor-api-11.0.7-javadoc.jar
    - lib/graph-editor-api-11.0.7-sources.jar
    - Ok
    - Ok
4. repeat for core and model jars.
##How to run
1. View -> Tool Windows -> Maven
2. Lifecycle -> clean
2. Plugins -> javafx -> javafx:compile
3. Plugins -> javafx -> javafx:run

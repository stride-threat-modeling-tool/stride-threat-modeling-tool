# Setup with  IntelliJ
if you cannot start the app because a NullPointerException
1. File -> Settings -> Build,Execution, Deployment -> Compiler
2. Add ";!?*.css;!?*.fxml" to the resource pattern
3. Uncheck "use --release option for cross-compilation"
4. Right Click on ProjectName -> Open Module Settings -> Libraries -> + -> Java
jars (with shifts pressed): 
    - lib/graph-editor-api-11.0.7.jar
    - lib/graph-editor-api-11.0.7-javadoc.jar
    - lib/graph-editor-api-11.0.7-sources.jar
    - Ok
    - Ok
5. repeat for core and model jars.
## How to run
1. View -> Tool Windows -> Maven
2. Lifecycle -> clean
3. Plugins -> javafx -> javafx:compile
4. Plugins -> javafx -> javafx:run

## How to run test
1. Plugins -> javafx -> javafx:compile
2. Build project (Ctrl + F9)
3. Plugins -> surefire -> surefire:test

Development Quick Setup
-----------------------
````
git clone git@github.com:Thaerious/JJJRMI.git rmi
cd rmi
npm i
npm --prefix src/test/js i
source bin/setup
````

Perform Tests
-------------
Build test classes  
`mvn test-compile`

Run Java & JS tests  
`mvn test`

Build coverage reports, then browse to '.../rmi/target/site/jacoco/index.html'.    
`mvn jacoco:report`

Run a single Java test.  
`mvn -Dtest=testname test`

Run JS tests manually.  
`mvn test-compile`
`npm run test`

Run a single test class.    
`mvn -Dtest=UsersServiceImplTest test` 

Running Tests Manually
------------------
<b>Setup CLI.</b>  
`source bin/setup`

<b>Compile Source Code</b>  
`mvn clean compile`

<b>Test Java to JS Parser</b>  
Test converting Java code to Javascript code.  Checks that certain Java
code constructs create the appropriate JS code construct.
`mvn -Dtest=JSParserTest test`

<b>Test Java Translator</b>  
Check the basic functionality of the Java to JS encoder. Works by encoding
Java classes and doing a cursory check of the resulting JSON.  
`mvn -Dtest=EncoderSanityTest test`

<b>Translator Clear Test</b>
Ensure that the Translator is working.  
`mvn -Dtest=TranslatorClearTest test`  
`mvn -Dtest=TranslatorTest test`  

<b>Generate JSON from Java.</b>  
```
java -cp target/test-classes/:target/classes/:target/dependency/* ca.frar.jjjrmi.translator.GenerateJSON ./target/test-data/from-java.json
```
 
<b>Generate JSON from JS.</b>  
`node src/test/js/GenerateJSON.js target/test-data/from-js.json`

<b>Generate JS test files from Java.<b>  
`jjjrmi -d src/test/java/ -o deleteme`

Command Line Interface
----------------------
Copy dependencies to the target directory.  
`mvn compile dependency:copy-dependencies`

Run setup.  This must be run everytime a new shell is started.  It does not change
the path in .bashrc  
`source /bin/setup`

You can now run the jjjrmi script which calls the CLI.  
`jjjrmi -d src/test/java/ca/frar/jjjrmi/jsbuilder/code/JSLambdaCode.java -p deleteme`

running "jjjrmi ." will source and output to the current directory.  

-d | --dir input_root_directory  
Set input path. If the input is a directory recursively process all files in all
directories.

-o | --output output_directory  
Set output path.  The default is target/jjjrmi/package-name

-n | --name [package_name]  
set package name default "package".  Will out files to subdirectory in the output directory.
The default is 'package'.

-i | --include [class1 ... classN]  
include only specified source files (.java extension not required).

-e | --exclude [class1 ... classN]  
exclude only specified source files (.java extension not required).

-p | --package  
Generate the packageFile.js file.

-j | --json  
Generate the package.json file.

Print xml of generated files (for debugging)
--xml

Output verbosity options
-s, -ss silent, really silent
-v, -vv, -vvv, -vvvv verbose, very verbose, debug, trace

Dependent Version Management
----------------------------
see: https://www.baeldung.com/maven-dependency-latest-version

List avaialable updates  
`mvn versions:display-dependency-updates`  

Update all dependencies (use one)  
`mvn versions:use-next-releases`  
`mvn versions:use-latest-releases`  

Testing the Socket (with Node)
------------------------------
Create socket test classes  
`jjjrmi -d src/test/java/ca/frar/jjjrmi/socket/testclasses/ -o src/test/js/socket-testclasses -p`

Start the Test Server  
`java -cp target/dependency/*:target/classes/:target/test-classes/ ca.frar.jjjrmi.socket.WSTestServer &`

Run the JS Client  
`node src/test/js/DevJJJRMISocket.js`

Prepare files for npm
---------------------
Generate JS files an place them in the JS source directory.
`mvn jjjrmi:generate-js`  
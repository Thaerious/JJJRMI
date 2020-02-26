Development Quick Setup
-----------------------
````
> git clone --branch refactor-json git@github.com:Thaerious/JJJRMI.git rmi
> cd rmi
> mvn compile dependency:copy-dependencies
> source bin/setup
````

Perform Tests
-------------
Build test classes  
`> jjjrmi -p -d src/test/java/ca/frar/jjjrmi/testclasses/ -o src/test/js/testclasses`

Run java tests  
`> mvn test`

Build coverage reports, then browse to '.../rmi/target/site/jacoco/index.html'.    
`> mvn jacoco:report`

Prepare data files for JS tests (requires mvn test-compile).  
`> java -cp target/classes/:target/test-classes/:target/dependency/* ca.frar.jjjrmi.translator.GenerateJSON ./target/test-data/from-java.json`

Run JS tests, then browse to '.../rmi/target/site/nyc/index.html'.  
`> nyc --report-dir=target/site/nyc --reporter=html bash jstest.sh`

Compiling
---------
`mvn install`
`mvn jjjrmi:generate-js`
`add /target/jjjrmi/jjjrmi to any npm dependencies`

Command Line Interface
----------------------
Copy dependencies to the target directory.  
`> mvn install dependency:copy-dependencies`

Run setup.  This must be run everytime a new shell is started.  It does not change
the path in .bashrc  
`> source /bin/setup`

You can now run the jjjrmi script which calls the CLI.  
`> jjjrmi -d src/test/java/ca/frar/jjjrmi/jsbuilder/code/JSLambdaCode.java -p deleteme`

running "jjjrmi ." will source and output to the current directory.  

-d | --dir input_root_directory  
Set input path. If the input is a directory recursively process all files in all
directories.

-o | --output output_directory  
Set output path.  The default is target/jjjrmi/package-name

set package name default "package".  Will out files to subdirectory in the output directory.
The default is 'package'.
-n | --name [package_name]

include only specified source files (.java extension not required).
-i | --include [class1 ... classN]

exclude only specified source files (.java extension not required).
-e | --exclude [class1 ... classN]

Generate the packageFile.js file.
-p | --package

Generate the package.json file.
-j | --json

Print xml of generated files (for debugging)
--xml

Output verbosity options
-s, -ss silent, really silent
-v, -vv, -vvv, -vvvv verbose, very verbose, debug, trace

Running Tests
-------------
compile tests  
> mvn test-compile

run a single test class  
> mvn -Dtest=UsersServiceImplTest test

Dependent Version Management
----------------------------
see: https://www.baeldung.com/maven-dependency-latest-version

List avaialable updates  
`> mvn versions:display-dependency-updates`  

Update all dependencies (use one)  
`> mvn versions:use-next-releases`  
`> mvn versions:use-latest-releases`  


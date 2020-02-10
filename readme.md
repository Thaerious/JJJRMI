Compiling
=========
mvn install
mvn jjjrmi:generate-js

add /target/jjjrmi/jjjrmi to any npm dependencies

Command Line Interface
----------------------
Copy dependencies to the target directory.
> mvn install dependency:copy-dependencies 

Run setup.  This must be run everytime a new shell is started.  It does not change
the path in .bashrc
> source /bin/setup

You can now run the jjjrmi script which calls the CLI.
> jjjrmi -i src/test/java/ca/frar/jjjrmi/jsbuilder/code/JSLambdaCode.java -p deleteme

running "jjjrmi ." will source and output to the current directory.

-i input_path
Set input path. If the input is a directory recursively process all files in all
directories.

-o output_directory
Set output path.

set package name default "package".  Will out files to subdirectory in the output directory.
-p [package_name]

Generate the package file.
--package

Generate the json file.
--json

Running Tests
-------------
compile tests
> mvn test-compile

run a single test class
> mvn -Dtest=UsersServiceImplTest test

run a single test method
> mvn -Dtest=UsersServiceImpl#testCreateUser test

run tests by pattern
> mvn -Dtest=UsersServiceImpl#testCreate* test

generate coverage report (in target/sites/jacoco)
> mvn jacoco:report


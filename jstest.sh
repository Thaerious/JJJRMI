#!/bin/bash
node src/test/js/TranslatorTest.js
node src/test/js/GenerateJSON.js target/test-data/from-js.json
node src/test/js/TestJavaToJS.js target/test-data/from-js.json
node src/test/js/TestJavaToJS.js target/test-data/from-java.json
node src/test/js/SocketTest.js
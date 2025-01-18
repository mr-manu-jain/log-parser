## Overview
This application processes an input text file containing logs, seggregates then based on their type and generates three primary JSON files: `apm.json`, `application.json`, and `request.json` containing aggregated data of the processed logs.
It additionally also creates a JSON file containing the list of corrupt of unknown logs which the program was not able to process.

## Prerequisites
- Java 8 or higher
- Maven 3.x or higher

## Setup

To generate the aggregated JSON files from a given input file, navigate to the folder containing application and run the following command:

    mvn exec:java -Dexec.args="--file <<RELATIVE_PATH>>/<<FILE_NAME>>.txt"

Note: RELATIVE_PATH/FILE_NAME.txt, signifies the relative path of the file you want to process and to perform aggregations on. You can modify it according to the location of your file. A good example would be


    mvn exec:java -Dexec.args="--file src/main/resources/input.txt"

## Output Files: 
* *apm.json*: Contains APM-related data.  
* *application.json*: Contains application-related data.  
* *request.json*: Contains request-related data.
* *unableToProcess.json*: Contains the files which the application was not able to process.
  
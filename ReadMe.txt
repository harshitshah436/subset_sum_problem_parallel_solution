CSCI 654: Foundations of Parallel Computing
Research investigation

Topic: Subset sum problem - Parallel Solution
Team member: Harshit Shah

The archive file contains following directory structure.

Archive:  hrs8207.zip
    src/                        -   This directory contains SubsetSumSeq.java, SubsetSumSmp.java source files.
    input_files/                -   This directory contains all input files used during investigation.
    output_files/               -   This directory contains all output files corresponding to each input files.
    performance/                -   This directory contains strong scaling, weak scaling all data, plots, scripts and test cases.
    performance/Performance_Test_Cases.txt  - Test cases for strong scaling and weak scaling.
    performance/scripts.txt                 - Scripts used for each test cases.
    General_Test_Cases.txt      -   General test cases with test results.
    ReadMe.txt                  -   Read me file for directory information and program running information.

Developer's manual:
    Both sequential and parallel programs have been tested on RIT CS multi core parallel computer nessie.
    
    To compile the program first you need to set your Java class path to include PJ2 distribution. To set the java class path to current directory plus the PJ2 jar file follow the commands:
        - For the bash shell:
            export CLASSPATH=.:/var/tmp/parajava/pj2/pj2.jar
        - For the csh shell:
            setenv CLASSPATH .:/var/tmp/parajava/pj2/pj2.jar

    Now compile the program on nessie machine as below.
        javac SubsetSumSeq.java - To compile sequential program
        javac SubsetSumSmp.java - To compile parallel program


User's manual:
    To run the program on RIT CS multicore parallel computer nessie first you need to set your Java class path to include PJ2 distribution and compile the program same as above.
    
    Now run the program as below.
        java pj2 SubsetSumSeq <input_file> [p] - To compile sequential program
        java pj2 cores=<K> SubsetSumSmp <input_file> [p] - To compile parallel program

        Here,   <input_file> - file containing n objects for a set.
                <K> - Total number of cores.
                [p] - optional parameter p to print the dynamic 2D table.
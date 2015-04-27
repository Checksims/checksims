Checksims
=========

![Build Status](https://travis-ci.org/Checksims/checksims.svg?branch=master)

Checksims is a modular, cross-platform framework for software similarity
detection designed to be used for academic dishonesty detection in undergraduate
level software development classes.

To learn more, it is highly recommended to read the User's Guide and Developer's
Guide, which can be built with the instructions in the Documentation section.


Building the Code
-----------------

Checksims is a Maven project, so all standard Maven commands can be used.

If you are unfamiliar with Maven, the following will build the latest version of
the project:

`mvn compile package`


Documentation
-------------

Documentation can be built with `make docs`

Known Dependencies for building documentation:
 - make
 - texmk
 - pdflatex
 - biblatex
 - biber


Contributing
------------

Please verify all tests pass and style guidelines are followed with:

`mvn verify`

TO DO

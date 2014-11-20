SEPR-LYS-A2
===========

This project requires the JDK. You can download it from [here](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Tests
-----

Tests can be found in the directory "test". It mirrors the "src" folder.
All tests use the JUnit 4 library.

If you are using IntelliJ IDEA 14, you will need to make sure that the test folder is recognised as such.
<ol>
<li> Select File, Project Structure </li>
<li> Select Modules </li>
<li> Select the "test" folder </li>
<li> Mark the folder as a test folder in the above toolbar </li>
</ol>

In IntelliJ IDEA 14, a test configuration is required:
<ol>
<li> Select Run, Edit Configurations </li>
<li> Select Add (Plus Button), JUnit </li>
<li> Name this configuration "All Tests" </li>
<li> Change Test Kind to "All in package" </li>
<li> In the package textbox enter "lys" </li>
<li> Change Search for tests to "In whole project" </li>
</ol>
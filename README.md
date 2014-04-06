Spreadsheets
============

This application has been developed by Victor Gonçalves Elias during a remote interview with Palantir Technologies, while pursuing an internship position at the company.

It is basically a spreadsheet application with basic funcionalities like mathematical expressions with +, -, * and / operations and parentheses, and referencing other cells to make more complex expressiions.

============

Things worth noting about the development and application current state:

 - It has been developed in Java 7, with the default Swing GUI toolkit. It's been developed and tested on an OS X 10.9 computer, so there may be some differences when running it on a Windows system (probably just the general look and feel).

 - Has a similar (simplified) interface as Microsoft Office's Excel software, with a simple table view displaying all the cell's values being their parsed results, if they are an arithmetic expression, or possibly just the raw content of the cell if they aren't numeric. There's "infinite" scroll, so if the table is scrolled to its edge, we render more cells so the user can reach further.

 - Also like Excel, to make a cell be parsed as an arithmetic expression all you need to do is make it start with an equal sign ('=') so "=1+1" would be evaluated to "2.0", for example.

 - As required, the expressions can contain regular operators (+, -, * and / ) and the operands may be a literal floating point or references to other cells (following the Excel nomenclature). We also allow the expressions to contain parentheses so for example "=(1+2)*3/2" would be valid and correctly parsed.

 - There is error detection of anything that could go bad, from a malformed expression to unbalanced parentheses and cycle dependency between cells. If an error occurs while trying to parse a cell, an error string would be displayed on the cell with a tip of what has gone wrong (e.g.: "#!CYCLE" for cyclic dependency between cells, "#!UNBLN" for unbalanced parentheses). There is a more detailed message that is sent with the Exception itself, but for now we just display the small tip on the cell since it's enough to know what has gone wrong.

 - The spreadsheet constructed on the app can be saved to a file to be restored later, and the app takes care not to lose modifications that could be lost when changing the currently open file or closing the editing window. To save the spreadsheet on a file we basically convert it to a JSON object containing the map of cells' names to cells' contents so they can be also easily restored later. To have easy JSON parsing/serializing decided to import the official JSON library for Java (org.json), so this is one of the libraries that is being used on the project (the only one to be bundled with the production app JAR though).

 - For detecting spreadsheet saved files corruption, developed a thin security layer where we include a custom generated hash value of the JSON on the beginning of the file so we can check it's still the same after we restore the JSON back. It could be easily maliciously hacked, but for detecting file corruptions it's pretty good.

 - Most "logic-ful" classes are being covered on tests. Specially the arithmetic expression parsing one, which would be the most critical section of the project and that should be working properly for the application itself be acceptable at all. The JFrame class of the application doesn't have any tests, since it's just a wrapper for everything to glue together and build the user interface. Used the JUnit library for tests, so that is the other library that has been included on the project (that should be used only on tests though).

============

Some ideas of what could be done next:

 - Better feedback for the user on errors. We could show a dialog with a more detailed message of what happened and how it could be fixed.

 - Fix some interactions that are weird because of the Swing toolkit. e.g.: should clear the cell when you start typing something instead of amending to it; not being able to Copy/Paste using the Cmd+C/V accelerators (only via the Edit menu).

 - Add helper functions like Excel, for example SQ, SQROOT, EXP, etc. and extra operators like ^ (exponentiation), % (percentage), \ (mod), etc. Also have referencing to a range of cells to execute a SUM easily for example (it's very useful for analyzing a bigger amount of data).

 - Make a dynamic cell dependency adjustment, so that we don't have to re-evaluate the whole table when we update a single cell (make it update only the cells which depend on the cell that changed, and recursively that). The current impl. could get expensive/slow if handling bigger amounts of data.

 - Have separate (or even custom) formatting for different kinds of cells, which would help to visualize the data (e.g. align text left and parsed numbers/expressions to the right).

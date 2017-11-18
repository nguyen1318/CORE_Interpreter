# CORE_Interpreter

Tony Nguyen
<br />
Project: Interpreter for CORE Language
<br />
<br />
To compile all the .java files, please run "make".
<br />
To delete all the .class files, please run "make clean".
<br />
Ensure that all files are in the directory.
<br />
<br />
Package Contents:
<br />
	Main.java - interpreter => Scanner > Parser > Printer > Executor
  <br />
	Scanner.java - Used to generate a token stream. Reads the file and reports
						it to the parser to be turned into a parsetree.
            <br />
	Tokenizer.java - Used to extract the input from provided file. Then it sends
						it to the scanner.
            <br />
	Parser.java - Used to generate a parse tree. Given a scanner, it returns a
						parse tree of the entire input program.
            <br />
	Executor.java - Used to run the program and generate the output of the
						program's given input. Given an input file and
						parsetree, it executes the code.
            <br />
	Printer.java - Used as a output printing program that is generated from the
						parsetree.
            <br />
	makefile - The makefile used to compile the project or clean the project
						back to it's original state.
<br />
<br />
To run:
<br />
	run "java Main *program.code* *program.data*"
  <br />
  <br />
	To pretty print the output code, go into the Main.java file and uncomment
	the pretty print line that is used to print the code.

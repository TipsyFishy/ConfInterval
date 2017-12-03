import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;
import java.util.Scanner;
import java.util.ArrayList;

/*
	Allison Anderson Dec. 2, 2017
	Input: filename.csv
	Compile: "javac CSVDataCheck.java"
	Run: "java CSVDataCheck filename.csv num_conf_bubbles"

	Checks:
	-If exam # is a #
	-If repeat exam #'s exist
	-Checks if point values and confidence values are #'s
	-Checks if ":" exists in each data cell
	-Checks if 2 values are input into each data cell
	-Checks if point values are allowed to have partial credit or not
		-If partial credit allowed, checks if point values are in the correct range
		-If partial credit not allowed, checks if point values are full point worth, 0, or -1
	-Checks if confidence values are in correct range (for both 5 bubbles and 4 bubbles)
*/

class CSVDataCheck {
	public static String MAXCONF= "";
	public static boolean TF = false;
	public static String[] questPts;
	public static String[] questType;
	public static ArrayList<String> examList = new ArrayList<String>(0);

	/* Checks if exam number is a number and checks if the exam number has already been used */
	public static boolean checkExam(String value, String columnVal, int row, boolean errors){
		if(value.matches("\\d+")){ //checks if exam number is a number
			//checks if exam number has already been used
			if(examList.contains(value) == false){
				examList.add(value);
			}
			else{
				System.out.println("\tExam number already exists: " + columnVal + row + " " + value);
				errors = true;
			}
		}
		else{
			System.out.println("\tExam not a number: " + columnVal + row + " " + value);
			errors = true;
		}
		return errors;
	}

	/* Checks ranges for point values */
	public static boolean questValueCheck(String value, String columnVal, int column, int row, boolean errors){
		//System.out.println("Column L #: " + columnVal + " " + column + " Quest Value: " + value + " Quest Type: " + questType[column] + " Quest Pts: " + questPts[column]);
		if(questType[column].equals("MC")){ //checks that MC questions were marked full points, 0, or -1
			if(!(value.equals(questPts[column]) || value.equals("-1") || value.equals("0"))){
				System.out.println("\tIncorect point value: " + columnVal + row + " " + value + " MC");
				errors = true;
			}
		}
		else if(questType[column].equals("T/F")){
			//checks for correct point value if no partial points are allowed
			if(TF == false && !(value.equals(questPts[column]) || value.equals("-1") || value.equals("0"))){
				System.out.println("\tIncorect point value: " + columnVal + row + " " + value + " T/F");
				errors = true;
			} //checks for correct point values if partial points are allowed
			else if(TF == true && !(Integer.parseInt(value) >= -1 && Integer.parseInt(value) <= Integer.parseInt(questPts[column]))){
				System.out.println("\tIncorrect point value: " + columnVal + row + " " + value + " T/F");
				errors = true;
			}
		}
		else{ //checks all other question types to see if their points are in the correct range
			if(Integer.parseInt(value) >= -1 && !(Integer.parseInt(value) <= Integer.parseInt(questPts[column]))){
				System.out.println("\tIncorrect point value: " + columnVal + row + " " + value + " " + questType[column]);
				errors = true;
			}
		}
		return errors;
	}

	/* Checks for the correct range of values for confidence */
	public static boolean confValueCheck(String value, String column, int row, boolean errors){
		if(MAXCONF.equals("5")){ //checks ranges if there are 5 confidence bubbles
			if(!(Integer.parseInt(value) >= -2 && Integer.parseInt(value) <= 5 && Integer.parseInt(value) != 0)){
				System.out.println("\tIncorect value: " + column + row + " " + value);
				errors = true;
			}
		} //checks ranges if there are 4 confidence bubbles
		else if(!(Integer.parseInt(value) >= -2 && Integer.parseInt(value) <= 4 && Integer.parseInt(value) != 0)){
			System.out.println("\tIncorect conf value: " + column + row + " " + value);
			errors = true;
		}
		return errors;
	}

	/* Reads in the csv file and checks for errors in the data */
	public static void readInCSV(String CSVfile){
		try{
			BufferedReader buffRead = null;
			String line = "";
			String[] parsedLine;
			String[] colonBegone;
			String[] column = new String[]{"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
			int rowNum = 0;
			int Qnum = 1;
			boolean errors = false;

			buffRead = new BufferedReader( new FileReader(CSVfile));
			line = buffRead.readLine();

			while(line != null){
				rowNum++;
				parsedLine = line.split(",");
				if(rowNum == 2){ //saves question worths
					questPts = parsedLine;
				}
				else if (rowNum == 3){ //saves question types
					questType = parsedLine;
				}
				else if (rowNum >= 6){//starts checking the data with semi colons
					//loops through each token in a line
					for(int i = 0; i < parsedLine.length; i++){
						if(i == 0){
							errors = checkExam(parsedLine[i],column[i],rowNum,errors);
							//System.out.println(parsedLine[0]);
						}
						else if(parsedLine[i].contains(":") == true){//checks if the token has a ":"
							colonBegone = parsedLine[i].split(":");
							if(colonBegone.length > 2){ //checks if there's more than 2 values
								System.out.println("\tToo much data in the cell: " + column[i] + rowNum + "  " + parsedLine[i]);
								errors = true;
							}
							else if(colonBegone.length < 2){ //checks if there's less than 2 values
								System.out.println("\tNot enough data in the cell: " + column[i] + rowNum + "  " + parsedLine[i]);
								errors = true;
							} //checks if the point value is a number
							if(!(colonBegone[0].matches("\\d+") || colonBegone[0].equals("-1"))){
								System.out.println("\tNot an integer: " + column[i] + rowNum + " " + colonBegone[0]);
								errors = true;
							} //checks if the conf is a number
							else if(!(colonBegone[1].matches("\\d+") || colonBegone[1].equals("-1") || colonBegone[1].equals("-2"))){
								System.out.println("\tNot an integer: " + column[i] + rowNum + " " + colonBegone[1]);
								errors = true;
							} //if both are numbers, check their values
							else{
								errors = questValueCheck(colonBegone[0],column[i],i,rowNum,errors); //checks if the question is a correct value
								errors = confValueCheck(colonBegone[1],column[i],rowNum,errors); //checks if the conf is a correct value
							}
						}
						else if(i != 1){//ignores student id column
							System.out.println("\tNo semi colon found: " + column[i] + rowNum + " " + parsedLine[i]);
							errors = true;
						}
					}
				}
				line = buffRead.readLine();
			}
			if(errors == false){ //if no erros found prints finished message
				System.out.println("Finished searching for errors.");
    			}
		}
    		catch(FileNotFoundException e){
    			System.out.println("Unable to find the file: " + CSVfile);
    		}
		catch (IOException e){
			System.err.println("Unable to read the file: " + CSVfile);
		}
	}

	/* Main function that checks for correct number of args and if there are any errors in the csv file */
	public static void main(String[] args){
		if(args.length == 2){
			Scanner input = new Scanner(System.in);
			System.out.print("Can True/False questions recieve partial points? y/n: ");
			String tf = input.next();
			MAXCONF = args[1];
			if(tf.toUpperCase().equals("Y")){
				TF = true;
				System.out.println("True/False: partial points");
			}
			else{
				System.out.println("True/False: no partial points");
			}
			System.out.print("\n");
			readInCSV(args[0]);
			System.out.print("\n");
		}
		else{
			System.out.println("You forgot the filename or max confidence arg.");
			System.out.println("Correct format: java CSVDataCheck filename.csv num_conf_bubbles");
		}
	}

}

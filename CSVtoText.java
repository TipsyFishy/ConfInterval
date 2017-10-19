import java.io.File;
import java.io.FileNotFoundException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileReader;

/*
	Allison Anderson Oct. 18, 2017
	Input: filename.csv
	Output: filename.txt
	Compile: "javac CVStoText.java"
	Run: "java CVStoText filename.csv"
*/

class CSVtoText {

	/*Reads in the CVS file and replaces "," with spaces and ":" with spaces.*/
	public static void readInCSV(String CSVfile){
		try{
			BufferedReader buffRead = null;
			String line = "";
			String colonless = "";
			String textFile = CSVfile.replace("csv", "txt"); //properly formats string to make a txt file
			String[] parsedLine;
			String[] colonBegone;
			int Qnum = 1;

			PrintWriter HermanHesse  = new PrintWriter(textFile, "UTF-8"); //txt file made with correct name
			buffRead = new BufferedReader( new FileReader(CSVfile));
			line = buffRead.readLine();

			while(line != null){
				colonless = "";
				parsedLine = line.split(",");
				//loops through each token in a line
				for(int i = 0; i < parsedLine.length; i++){
					//checks if the token has a ":" if so adds the token back to the line without the ":"
					if(parsedLine[i].contains(":") == true){
						colonBegone = parsedLine[i].split(":");
						colonless = colonless + " " + colonBegone[0] + " " + colonBegone[1];
					}//gets rid of excess spaces
					else if(parsedLine[i].equals("") == true){
						colonless = colonless;
					}//adds a column name for the confidence levels "C#"
					else if((parsedLine[i].contains("Q") == true) & (parsedLine[i].matches(".*\\d+.*"))){
						colonless = colonless + " " + parsedLine[i] + " C" + Qnum;
						Qnum = Qnum + 1;
					}
					else{
						colonless = colonless + " " + parsedLine[i];
					}
				}//writes the modified line to the new txt file then goes to the next line
				HermanHesse.write(colonless + "\n");
				line = buffRead.readLine();
			} //closes the file that's eing written to and outputs finished message
			HermanHesse.close();
			System.out.println("Finished writing: " + textFile);
    	}
    	catch(FileNotFoundException e){
    		System.out.println("Unable to find the file: " + CSVfile);
    	}
		catch (IOException e){
			System.err.println("Unable to read the file: " + CSVfile);
		}
    }

	public static void main(String[] args){
		if(args.length == 1){
			readInCSV(args[0]);
		}
		else{
			System.out.println("You forgot the filename arg..");
			System.out.println("Correct format: java CVStoText filename.csv");
		}
	}

}
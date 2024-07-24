package test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.io.IOException;
import java.util.List;

import request.ForwardCurve.OptionData;
import request.ForwardCurve;
import pricing.VanillaOptions;
public class TestCall {
	
	public static void printMatrix(double[][] matrix) {
        String[] labels = {"Call Prices", "Forward Prices", "Volatility"};
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(labels[i] + ": ");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "\t"); // Print each element followed by a tab
            }
            System.out.println(); // Move to the next line after each row
        }
    }
	
	public static long daysBetween(String yyyymmdd) {
        // Define the date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        // Parse the input date
        LocalDate inputDate = LocalDate.parse(yyyymmdd, formatter);

        // Get today's date
        LocalDate today = LocalDate.now();

        // Calculate the number of days between the input date and today
        long daysBetween = ChronoUnit.DAYS.between(today, inputDate);

        return daysBetween;
    }
	
	public static void main(String[] args) throws IOException {
    	List<OptionData> options = ForwardCurve.forwardRequest("NYMEX:CL");
    	double S0 = options.get(0).close;
    	double[][] matrixCall = new double[3][24];
    	for(int i=1;i<25; i++) {
    		double expiry = daysBetween(options.get(i).expiration) / 365.25;
    		matrixCall[0][i-1] = VanillaOptions.generalBlackScholes(options.get(i).close, options.get(i).close, expiry, 0, 0, 0.3, "c")/S0;
    		matrixCall[1][i-1] = options.get(i).close/S0;
    		matrixCall[2][i-1] = 0.3;
    	}
    	printMatrix(matrixCall);
    	System.out.print("Exipration :");
    	for(int i=1;i<25; i++) {
    		System.out.print(options.get(i).expiration+"\t");
    	}
    }
	
	
}

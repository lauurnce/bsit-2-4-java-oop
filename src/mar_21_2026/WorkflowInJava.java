package mar_21_2026;

public class WorkflowInJava {
	// TOPIC: Workflow in JAVA
	// it executes from top to bottom
	// according to which they appear 
	// TYPES of CONTROL FLOW STATEMENT 
	// 1.) IF STATEMENT 
		// evaluates bullet expression depends on the condition provided 
	
	public static void main(String[] args) {
		
		//syntax: 
		// if(condition) {
		//	statement to be executed
		// } else {
		// 		statement to be executes when the condition is false e
		// }
		
		// if(condition 1) {
		//	statement to be executed
		// } else if (condition 2) {
		// 	
		// } else if (condition 3) {
		// 
		// } else {
		// }
				
		
		int x = 10;
		int y = 12; 
		
		if( x + y == 20 ) {
			System.out.println("x + y" + "is greater than 20");
		 } else {
			 System.out.println("x + y" + " is not greater than 20");
		 }
		System.out.println("Statement outside is not greater than 20");		
		
		
		String city = "Brgy BGC, Taguig, Philippines";
		
		if(city.contains("Mandaluyong")){
			System.out.println("Your city is Mandaluyong");
		 } else if (city.endsWith("Quezon City")){
			 System.out.println("Your city is Quezon City");
		 } else if (city.endsWith("Philippines")){
			 System.out.println(city.split(",")[1]);
		 } else  {
			 System.out.println(city);
		}
		
		String city1 = "Anonas Street Sta. Mesa Manila";
		
		if(city.contains("Mandaluyong")){
			System.out.println("Your city is Mandaluyong");
		 } else if (city1.endsWith("Quezon City")){
			 System.out.println("Your city is Quezon City");
		 } else if (city1.endsWith("Manila")){
			 System.out.println(city1.split(" ")[2]);
		 } else  {
			 System.out.println(city1);
		}
		
String city2 = "Anonas Street Sta. Mesa Manila";
		
		if(city2.contains("Mandaluyong")){
			System.out.println("Your city is Mandaluyong");
		 } else if (city2.contains(".") & city.contains("Man")){
			 System.out.println(city2.split(" ")[3] + "" + city2.split(" ")[2] + city2.split(" ")[3] + city2.split(" ")[11]);
		 } else if (city2.contains("Manila") & city2.contains("Anona")){
			 System.out.println(city2.split(" ")[2]);
		 } else  {
			 System.out.println(city2);
		}	
		
		String city3 = "Anonas Street Sta. Mesa Manila";
		if (city2.contains("Manda"){
			if(city.contains("Mandaluyong")){
				System.out.println("Your city is Mandaluyong");
			 } else if (city2.contains(".") & city.contains("Man")){
				 System.out.println(city2.split(" ")[3] + "" + city2.split(" ")[2] + city2.split(" ")[3] + city2.split(" ")[11]);
			 } else if (city2.contains("Manila") & city2.contains("Anona")){
				 System.out.println(city2.split(" ")[2]);
			 } else  {
				 System.out.println(city3);
		}
			}
		
	}
}

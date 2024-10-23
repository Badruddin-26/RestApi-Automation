
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given; //import this

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import pojoClasses.BookingDates;
import pojoClasses.BookingDetails;

import utility.AllureLogger;
import utility.BaseTest;
import utility.ExcelLib;

public class UpdateBooking extends BaseTest {
	
	@Test(description="To update the details of the booking IDs") 
	public void updateBooking(){
		
		AllureLogger.logToAllure("Starting the test to update details");

		
		//Sending the PUT request for a specific booking id and receiving the response after updating the detals
		AllureLogger.logToAllure("PUT update booking detail");

		//To get the auth token

		
		//Created a new booking
		CreateBooking createBooking = new CreateBooking();
		createBooking.createNewBooking("Sanda", "Jon", "114", "false", "2018-01-03", "2018-01-05", "Dinner", null);
		String IDtoUpdate = createBooking.newID;
		AllureLogger.logToAllure("New Booking ID created is : "+IDtoUpdate);
		
		//Update the booking with new first name
		Response getResponse = given().

				pathParam("id", IDtoUpdate).
			when().
				get("/booking/{id}");

		BookingDetails bookingDetails = getResponse.as(BookingDetails.class);
		bookingDetails.setFirstname("Tarly");


		
		//Sending the PUT request
		AllureLogger.logToAllure("Sending the PUT request to update the booking detail of booking id : "+IDtoUpdate);
		Response response = given().

			header("Content-Type", "application/json").
			header("Accept", "application/json").
				header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=").

	        pathParam("id", IDtoUpdate).
	        body(bookingDetails).log().body().
	    when().
			put("/booking/{id}");

		//Verify the response code
		AllureLogger.logToAllure("Asserting the response if the status code returned is 200");


		//To log the response to report
		logResponseAsString(response);
		
	}
}


import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
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

public class Crud extends BaseTest {

    public static String newID = "";
    BookingDetails bookingDetails = new BookingDetails();
    BookingDates bookingDates = new BookingDates();

    @DataProvider (name="DataFromExcel")
    public Object[][] data() throws Exception {
        ExcelLib xl = new ExcelLib("Booking", this.getClass().getSimpleName());
        return xl.getTestdata();
    }

    @Test(dataProvider="DataFromExcel", description="To retrieve the details of the booking IDs",priority = 1)
    public void createNewBooking(String firstname,
                                 String lastname,
                                 String totalprice,
                                 String depositpaid,
                                 String checkin,
                                 String checkout,
                                 String additionalneeds, String dontUseThis
    ){

        AllureLogger.logToAllure("Starting the test to create new details");


        AllureLogger.logToAllure("Posting a new booking detail");


        bookingDetails.setFirstname(firstname);
        bookingDetails.setLastname(lastname);
        bookingDetails.setTotalprice(Integer.parseInt(totalprice));
        bookingDetails.setDepositpaid(Boolean.parseBoolean(depositpaid));
        bookingDetails.setAdditionalneeds(additionalneeds);


        bookingDates.setCheckin(checkin);
        bookingDates.setCheckout(checkout);
        bookingDetails.setBookingdates(bookingDates);

        AllureLogger.logToAllure("Sending the POST request to create new booking");

        RestAssured.baseURI ="https://restful-booker.herokuapp.com";
        RequestSpecification request = RestAssured.given().contentType("application/json");
        request.body(bookingDetails);
        Response response = request.post("/booking");

        logResponseAsString(response);

        newID = response.then().extract().path("bookingid").toString();
        AllureLogger.logToAllure("Retrieved booking id : "+response.then().extract().path("bookingid"));
        Assert.assertEquals(response.then().extract().path("booking.firstname"), firstname);
        Assert.assertEquals(response.then().extract().path("booking.lastname"), lastname);
        Assert.assertEquals(Integer.toString(response.then().extract().path("booking.totalprice")), totalprice);
        Assert.assertEquals(Boolean.toString(response.then().extract().path("booking.depositpaid")), depositpaid);
        Assert.assertEquals(response.then().extract().path("booking.bookingdates.checkin"), checkin);
        Assert.assertEquals(response.then().extract().path("booking.bookingdates.checkout"), checkout);
        Assert.assertEquals(response.then().extract().path("booking.additionalneeds"), additionalneeds);


    }

    @Test(description="To retrieve the details of the booking IDs" , priority = 2)
    public void getBookingID(){

        AllureLogger.logToAllure("Starting the test to get booking details");
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.get("booking/"+newID );

        AllureLogger.logToAllure("Asserting the response if the status code returned is 200");

        logResponseAsString(response);

        AllureLogger.logToAllure("Asserting of response body with the data from test data excel");

         bookingDetails = response.as(BookingDetails.class);

        Assert.assertEquals(response.then().extract().path("firstname"),bookingDetails.getFirstname());
        Assert.assertEquals(response.then().extract().path("lastname"),bookingDetails.getLastname());
        Assert.assertEquals(response.then().extract().path("depositpaid"), bookingDetails.getDepositpaid());
        Assert.assertEquals(response.then().extract().path("bookingdates.checkin"), bookingDates.getCheckin());
        Assert.assertEquals(response.then().extract().path("bookingdates.checkout"), bookingDates.getCheckout());
        Assert.assertEquals(response.then().extract().path("additionalneeds"), bookingDetails.getAdditionalneeds());

  }

    @Test(description="To update the details of the booking IDs" , priority = 3)
    public void updateBooking(){

        AllureLogger.logToAllure("Starting the test to update details");
        bookingDetails.setFirstname("Tarly");

        AllureLogger.logToAllure("Sending the PUT request to update the booking detail of booking id : "+newID);
        Response response = given().

                header("Content-Type", "application/json").
                header("Accept", "application/json").
                header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=").

                pathParam("id", newID).
                body(bookingDetails).log().body().
                when().
                put("/booking/{id}");

        AllureLogger.logToAllure("Asserting the response if the status code returned is 200");
        Assert.assertEquals(response.getStatusCode(),200);
        Assert.assertEquals(response.then().extract().path("firstname"),bookingDetails.getFirstname());
        Assert.assertEquals(response.then().extract().path("lastname"),bookingDetails.getLastname());
        Assert.assertEquals(response.then().extract().path("depositpaid"), bookingDetails.getDepositpaid());
        Assert.assertEquals(response.then().extract().path("bookingdates.checkin"), bookingDates.getCheckin());
        Assert.assertEquals(response.then().extract().path("bookingdates.checkout"), bookingDates.getCheckout());
        Assert.assertEquals(response.then().extract().path("additionalneeds"), bookingDetails.getAdditionalneeds());

        logResponseAsString(response);

    }


    @Test(description="To delete the details of the booking IDs", priority = 4)
    public void deleteBookingId(){

        AllureLogger.logToAllure("Starting the test to delete booking details");



        RequestSpecification request = RestAssured.given().contentType("application/json").header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=");
        Response response = request.delete("/booking/"+newID);

        AllureLogger.logToAllure("Asserting the response if the status code returned is 201 as this is a Delete request");
        response.then().assertThat().statusCode(201);

        logResponseAsString(response);

    }




}

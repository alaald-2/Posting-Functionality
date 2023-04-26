package org.example;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Set the path to the chromedriver.exe file
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\alaae\\Downloads\\chromedriver_win32\\chromedriver.exe");

        // Create a new instance of the ChromeDriver
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");// Disable browser notifications
        options.addArguments("--disable-popup-blocking"); // disable popup blocking

        WebDriver driver = new ChromeDriver(options);
        // Navigate to the Facebook login page
        driver.get("https://www.facebook.com");

        // Verify that the page title contains the word "Facebook"
        if (driver.getTitle().contains("Facebook")) {
            System.out.println("Page title contains the word \"Facebook\".");
        } else {
            System.out.println("Page title does not contain the word \"Facebook\".");
        }
        // Wait for the cookie policy popup to appear
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        By allowAllCookiesButton = By.xpath("//button[text()='Allow essential and optional cookies']");
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(allowAllCookiesButton));
        element.click();

        // Set the path to the JSON file
        File jsonFile = new File("C:\\Users\\alaae\\IdeaProjects\\Posting\\facebook.json");
        String email = null;
        String password = null;

        // Read the Facebook credentials from the JSON file
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(jsonFile);

            email = jsonNode.get("facebookCredentials").get("email").asText();
            password = jsonNode.get("facebookCredentials").get("password").asText();

            System.out.println("Email: " + email);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Enter the user's login credentials and click on the "Log In" button
        try {
            driver.findElement(By.id("email")).sendKeys(email);
            driver.findElement(By.id("pass")).sendKeys(password);
            driver.findElement(By.name("login")).click();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Verify that the user is logged in and redirected to their profile page
        String expectedUrl = "https://www.facebook.com/";
        String actualUrl = driver.getCurrentUrl();
        if (actualUrl.contains(expectedUrl)) {
            System.out.println("User is successfully logged in and redirected to their profile page.");
        } else {
            System.out.println("Login failed. User is not redirected to their profile page.");
        }

        // Post a status update on the user's timeline
        String statusMessage = "Hello,world";

        // Set the status message to be posted
        try {
            // Find the status update box "What's on your mind"
            WebElement statusUpdateBox = driver.findElement(By.xpath("//span[contains(text(), \"What's on your mind\")]"));
            statusUpdateBox.click();
            // Wait for the input field to be visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div[role='dialog'] div[contenteditable='true']")));
            // Enter the status message into the input field
            WebElement statusUpdateInput = driver.findElement(By.cssSelector("div[role='dialog'] div[contenteditable='true']"));
            statusUpdateInput.sendKeys(statusMessage);
        // Find and click the 'Post' button to post the status update
            driver.findElement(By.xpath("//span[text()='Post']")).click();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        // Wait
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
        // Click the round profile picture in the right corner
        WebElement profilePic = driver.findElement(By.xpath("//*[@aria-label='Your profile']"));
        profilePic.click();

        // Wait for the "Log Out" button to become visible
        WebElement logoutButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[text()='Log Out']")));

        // Perform logout
        if(logoutButton.isDisplayed()) {
            logger.debug("Logout successful."); // Log debug message using logger
            System.out.println("User is successfully logged out of their profile page.");
        } else {
            logger.error("Logout unsuccessful."); // Log error message using logger
        }

        // Click on the "Log Out" button
            logoutButton.click();
        } catch (TimeoutException e) {
            System.out.println("Timeout waiting for element to be visible.");
        } finally {
            driver.quit();
        }
    }
}


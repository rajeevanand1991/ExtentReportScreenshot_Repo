import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;



public class FreeCRMTest {

	public WebDriver driver;
	public ExtentReports extent;
	public ExtentTest extentTest;

	@BeforeTest
	public void setExtent() {
		extent = new ExtentReports(System.getProperty("user.dir")+"/test-output/ExtentReport.html", true); // automatically will get the project location and replace the ExtentReport.html file with new one for every execution
		extent.addSystemInfo("Host Name", "Rajeev Windows 10 Pro OS");
		extent.addSystemInfo("User Name", "Rajeev Anand");
		extent.addSystemInfo("Environment Name", "QA");
	}
	
	@AfterTest
	public void endReport() {
		extent.flush();
		extent.close();
	}
	
	public static String getScreenshot(WebDriver driver, String screenshotName) throws IOException {
		String dateName = new SimpleDateFormat("yyyy-MM-dd-hh_mm_ss").format(new Date()); //To generate your customized date format
		TakesScreenshot ts = (TakesScreenshot) driver;
		File source = ts.getScreenshotAs(OutputType.FILE);
		// after execution, you could see a folder "FailedTestsScreenshots"
		// under src folder
		String destination = System.getProperty("user.dir") + "/FailedTestsScreenshots/" + screenshotName + dateName
				+ ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}

	@BeforeMethod
	public void setup() {
		System.setProperty("webdriver.chrome.driver", "E:\\ChromeDriver\\chromedriver.exe");
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		driver.get("https://freecrm.com/");
	}

	@Test
	public void freeCrmTitleTest() throws InterruptedException {
		extentTest = extent.startTest("freeCrmTitleTest"); // we need to add this line for every test cases
		String title = driver.getTitle();
		System.out.println("The login page title is: " + title);
		Assert.assertEquals(title,
				"Free CRM software for any business with sales, support and customer relationship management123"); //deliberately failing this testcase to capture failure screenshot in extent report
		Thread.sleep(3000);
	}
	
	@Test
	public void freemCRMLogoTest(){
		extentTest = extent.startTest("freemCRMLogoTest");
		boolean b = driver.findElement(By.xpath("//img[@class='img-responsive111']")).isDisplayed(); //deliberately failing this testcase to capture failure screenshot in extent report
		Assert.assertTrue(b);
	}

	@AfterMethod
	public void tearDown(ITestResult result) throws IOException {
		if(result.getStatus()==ITestResult.FAILURE) {
			extentTest.log(LogStatus.FAIL, "TEST CASE FAILED IS "+result.getName()); //To add name in extent report
			extentTest.log(LogStatus.FAIL, "TEST CASE FAILED IS "+result.getThrowable()); //To add error/exception in extent report
			
			String screenshotPath = FreeCRMTest.getScreenshot(driver, result.getName());
			extentTest.log(LogStatus.FAIL, extentTest.addScreenCapture(screenshotPath)); //To add screenshot in extent report
			//extentTest.log(LogStatus.FAIL, extentTest.addScreencast(screenshotPath)); //To add screencast/video in extent report	
		}
		else if(result.getStatus()==ITestResult.SKIP) {
			extentTest.log(LogStatus.SKIP, "Test Case SKIPPED is "+result.getName());
		}
		else if(result.getStatus()==ITestResult.SUCCESS) {
			extentTest.log(LogStatus.PASS, "Test Case PASSED is "+result.getName());
		}
		
		extent.endTest(extentTest);//ending test and ends the current test and prepare to create html report
		driver.quit();
	}
}
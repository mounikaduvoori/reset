package com.qa.rgn;
import java.awt.AWTException;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import atu.testng.reports.ATUReports;
import atu.testng.reports.ATUReports;
import atu.testng.reports.logging.LogAs;
import atu.testng.selenium.reports.CaptureScreen;
import atu.testng.selenium.reports.CaptureScreen.ScreenshotOf;
import atu.testng.reports.listeners.ATUReportsListener;
import atu.testng.reports.listeners.ConfigurationListener;
import atu.testng.reports.listeners.MethodListener;
import atu.testng.reports.utils.Utils;

import com.qa.common.ATUReporter;
import com.qa.common.ReadfromProperties;
import com.qa.common.poi_Reader_e;
import com.qa.keyword.Keywords;
import com.qa.ui.CS_Editable;
import com.qa.ui.CS_Oneclick;
//import com.qa.ui.ContestSetup_Editable;
import com.qa.ui.HomepageValidation;
import com.qa.ui.LoginPage;
import com.qa.ui.LoginProperty;
import com.qa.ui.Setup;

public class CS_OneclickValidation {
	// Set Property for ATU Reporter Configuration
	{
		System.setProperty("atu.reporter.config","..\\Presslaff\\properties\\atu_rgn.properties");
    }
	
	//Logger class
	Logger log = Logger.getLogger(CS_OneclickValidation.class);
			
	private WebDriver driver = null;
	private LoginPage loginpage = null;
	private LoginProperty property =null;
	private HomepageValidation homePage=null;
	private Setup setup =null;	
	private CS_Oneclick contestOneclick=null;
	private ATUReporter atu=null;
	private String p_URL = null;
	String sdate = null,edate = null;
	String getmail = null;
	private String endDate;
	
	// Create Object for Properties Class
	ReadfromProperties prop = new ReadfromProperties();
	
	 @BeforeTest
	public void setup()throws Exception
	{
		log.info("Contest Setup-Oneclick Validation");
		driver = new FirefoxDriver();
		driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
		
		loginpage = new LoginPage(driver);
		property = new LoginProperty(driver);
		
		homePage = new HomepageValidation(driver);
		contestOneclick=new CS_Oneclick(driver);
		atu = new ATUReporter(driver);
		
		driver.manage().window().maximize();
		
		String[][] testData = poi_Reader_e.readExcelData(".\\testdata\\resources\\Presslaff_URL.xlsx","P_URL","Valid_URL");
		String getURL = Arrays.deepToString(testData);
		//log.info(getURL);
		p_URL = getURL.replaceAll("[\\[\\]]", "");
	    //log.info(p_URL); 
		driver.get(p_URL);

		// This is required to retrieve the browser information and taking screenshots
	    ATUReports.setWebDriver(driver);

		// Atu Reporters configuration
		atu.AtuConfig();
		
		// Setting Index page Description
		ATUReports.indexPageDescription ="PRESSLAFF <br/> <b>Regression_Testing</b>";
		
		// Setting Author Details.This must be set for every test case so that
		// the author details are set on the Test Case Report Page
		atu.setAuthorInfoForReports();
		}
	
	//-----------Test method for login into Dat-e-Base------------
			// Data provider annotation is used to supply data for a test method
			@DataProvider(name = "login")
			public Object[][] dataProvider_valid() {
				Object[][] testData = poi_Reader_e.readExcelData(
						"..\\Presslaff\\testdata\\resources\\Login.xls","login","Login_Data");
				return testData;
			}

			// Suppressing or disabling compilation warnings.
			@SuppressWarnings("deprecation")	
			
			/* To receive data from this DataProvider needs to use a dataProvider name equals to the name of this annotation.*/
			@Test(dataProvider = "login", description = "Login to Dat-e-Base")
			public void OneclickContest_login(String username, String password) throws Exception {
				   Keywords.testName = "login";
				   log.info("@Test1: Login into Dat-e-Base");
				try {
					// Login into Dat-e-Base tool
					loginpage.loginTitle();
					
					loginpage.typeUsernmae(username);
					loginpage.typePassword(password);
					loginpage.clickLogin();
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

					// Launching Tester property				
					property.clickTester();
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					homePage.verify_Home();
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					
				} catch (Exception e) {
					e.printStackTrace();
					log.debug("Element not found"+" "+e.getMessage());
				}
		  }
			//-----------Test method for verifying contest page and buttons----------
			@SuppressWarnings("deprecation")
			@Test()
			public void verify_contestPageandBtns() throws Exception {
				   Keywords.testName = "Verify contest setup page and buttons";
				   log.info("@Test2: Verifying the contest setup page and buttons");
				   
				try {
					// Click on Setup
				    contestOneclick.click_Setup();
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					contestOneclick.click_AddContest();
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		
					//Verify the buttons and fields under addContest module
					Assert.assertTrue(contestOneclick.isContestSetupPageAndBtns());
					log.info("Assert:Verify buttons under contest module");	
					
				} catch (Exception e) {
					e.printStackTrace();
					log.debug("Elements not found"+" "+e.getMessage());
				}
		  }
				
			//----------Test method for Creating One click contest----------------	
			@DataProvider(name = "Create a One click contest")
			public Object[][] dataProvider_valid2() {
			Object[][] testData = poi_Reader_e.readExcelData(
							"..\\Presslaff\\testdata\\resources\\Contest_Oneclick.xls","login","Login_Data");
			return testData;
		  }
			@SuppressWarnings("deprecation")
			@Test(dataProvider = "Create a One click contest", description = "Creating a one click contest and upload image,enter text at page wizard")
			public void contestSetupOneclick_pagewizard(String contestName, String contestType,String headerText,String header_contestPage,String header_loginpage,String header_confirmationPage) throws Exception {
				   Keywords.testName = "Create One click contest";
				   log.info("@Test3: Creating One click contest,Uploading a banner and verifying image and header text at login and confirmation pages");
				   
				try {											
				//Enter the name of the Contest
				contestOneclick.type_Contestname(contestName);
				
				//Select contest type as Editable
				contestOneclick.select_ContestType(contestType);
				
				//Enter end date
				contestOneclick.edate(endDate);
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				//Verify the multiple entries - once selected
				Assert.assertTrue(contestOneclick.isMultipleonceselected());
				
				//Enter text at heading
				contestOneclick.contest_Heading(headerText);
				//Thread.sleep(2000l);
							
				//Save the contest
				contestOneclick.save_contest();	
				Thread.sleep(300);
				
				// Verify the headers of Add Contest page
				Assert.assertTrue(contestOneclick.isAddContest_headers());
							
				// Verify the Add Question is not displayed for ONE CLICK contest
				Assert.assertTrue(contestOneclick.isAddQuestionDisplayed());
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

				// Verify the contest type is Disabled
				Assert.assertTrue(contestOneclick.isContestTypeDisabled());
				
				Assert.assertTrue(contestOneclick.isMultipleonceselected());
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
				// Verify the contest link is displayed
				Assert.assertTrue(contestOneclick.isSurveylinkDisplayed());
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				//Navigate to page wizard				
				contestOneclick.click_pageWizard();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				// Page Wizard page has opened
				Assert.assertTrue(contestOneclick.isPageWizardOpened());
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				//Double click on banner image link
				contestOneclick.doubleClick_bannerimage();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				//Upload banner for wizard contest
				contestOneclick.uploadBannerImage();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				// Verify the page uploaded with banner
				//Assert.assertTrue(contestOneclick.isPW_UploadBanner());
				//System.out.println("Page Wizard uploads with image- Assert");
				Thread.sleep(3000);
				
				// Get source of banner of contest page
				//contestOneclick.getsourceOfBanner_ContestPage();
				
				// Open Contest header
				contestOneclick.pw_addContestHeading();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				//Add Contest header in CK Finder
				contestOneclick.contest_PW_contestPage_ckeditor(header_contestPage);
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				// Save CKEditor
				contestOneclick.ckfinder_save();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				// Verify the entered PWcontest page Header is same as received from test data 
				contestOneclick.oneclick_wizard(header_contestPage); 
				
				// Navigate to Page Wizard - Login Page
				contestOneclick.select_PW_login_page();
				
				// Verify the  PW_login Page has opened
				Assert.assertTrue(contestOneclick.isPW_loginpageOpened());
				System.out.println("Page Wizard Login Page opened- Assert");
				
				//Get the source of banner image of PW login Page
				contestOneclick.getsourceOfBanner_LoginPage();
				
				//Verify the banner of login page is same as banner of contest image
				contestOneclick.verifyBannerOfLoginPage();
				
				//Open Contest header of login page
				contestOneclick.contestHeading_loginPage();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				//Add header into  Contest header of login page - CK Finder
				contestOneclick.contest_PW_loginpage_ckeditor(header_loginpage);
				
				// Save Contest header of login page - CK Finder
				contestOneclick.ckfinder_save();
				
				// Verify the entered PWLogin page Header is same as received from test data 
				contestOneclick.verifyPW_Login_Contest_Header(header_loginpage);
				
				// Navigate to PW_confirmation_page
				contestOneclick.select_PW_confirmation_page();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				//Verify the banner of Confirmation page is same as banner of contest image
				contestOneclick.verifyBannerOfConfirmationPage();
				
				//Open header of COnfirmation page - CK Finder
				contestOneclick.contestHeading_confirmationPage();
				Thread.sleep(3000);
				
				// Add header into contest_PW_confirmation_ckeditor header
				contestOneclick.contest_PW_confirmation_ckeditor(header_confirmationPage);
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				contestOneclick.ckfinder_save();
				
				// Verify the enteredPW_Confirmation_Contest_Header is same as received from test data 
				contestOneclick.verifyPW_Confirmation_Contest_Header(header_confirmationPage);
				Thread.sleep(3000);
								
				// Navigate to Back to contest
				contestOneclick.backToContest();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

				}
				
	   			 catch (Exception e) {
							e.printStackTrace();
							log.error("Element not found"+" "+e.getMessage());
						}
				  }
		//-----------Test method for launching one click contest------------
		@DataProvider(name = "Launching oneclick contest")
		public Object[][] dataProvider_valid7() {
			Object[][] testData = poi_Reader_e.readExcelData(
							"..\\Presslaff\\testdata\\resources\\LaunchContestLink.xls", "login","Login_Data");
				return testData;
			}
		   @SuppressWarnings("deprecation")				
		   @Test(dataProvider = "Launching oneclick contest", description = "Launching the one click contest and complete the regestration")					
		   public void LaunchingOneclickContest(String header_contestPage,String header_loginpage,String header_confirmationPage) throws Exception {
						Keywords.testName = "Launching oneclick contest and complete the registration";							  
						log.info("@Test4: Launching OneClick contest");
								   
							try {								
								// Launch Contest link
								contestOneclick.openSureveyLink();
								driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
																
								// Verify front page login header
							    contestOneclick.frontPage_login(header_loginpage);
			
								// Enter email id to login 
								contestOneclick.typeEmailid(getmail);
								
								// Enter Submit
								contestOneclick.emailSubmit();
								driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
								
								// Navigate to the Registration page
								contestOneclick.fp_RegistrationHeader(header_contestPage);
								driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
								
								// Enter First name
								contestOneclick.fp_Registration_type_firstname();
								
								// Enter last name
								contestOneclick.fp_Registration_type_lastname();
								
								// Select Gender
								contestOneclick.fp_Registration_Select_Gender();
								
								// Select Date of Birth
								contestOneclick.fp_Registration_Select_DOB();
								
								// Select Local Opt in
								contestOneclick.fp_Registration_Select_optin();
								
								// Enter Submit
								contestOneclick.fp_RegSubmit();
													
								// Navigate to the confirmation page(navigate back to contest - Dat-e-base)
								contestOneclick.fp_ConfirmationHeader(header_confirmationPage);
							
													
								// Navigate to setup page
								contestOneclick.click_Setup();
								
								// Click on editable contest
								contestOneclick.click_CS_OneClick_Rgn();
								
								// Delete the contest
								contestOneclick.delete();
								
								// Handling alert for deleting the message
								contestOneclick.alert();
														
								// logout
								contestOneclick.logout();
								 driver.close();
								 driver.quit();

							}
	   			 catch (Exception e) {
							e.printStackTrace();
							log.error("Element not found"+" "+e.getMessage());
						}
				  }
		
		   //Quit from browser after test
			@AfterTest
			public void aftertest() throws Exception {

			 //driver.close();
			// driver.quit();

			}
	
}
 
	



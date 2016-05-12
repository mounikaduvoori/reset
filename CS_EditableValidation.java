package com.qa.rgn;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
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
import atu.testng.reports.listeners.ATUReportsListener;
import atu.testng.reports.listeners.ConfigurationListener;
import atu.testng.reports.listeners.MethodListener;
import atu.testng.reports.utils.Utils;

import com.qa.common.ATUReporter;
import com.qa.common.ReadfromProperties;
import com.qa.common.poi_Reader_e;
import com.qa.keyword.Keywords;
import com.qa.ui.CS_Editable;
import com.qa.ui.HomepageValidation;
import com.qa.ui.LoginPage;
import com.qa.ui.LoginProperty;

public class CS_EditableValidation 
{
		// Set Property for ATU Reporter Configuration
		{
			System.setProperty("atu.reporter.config","..\\Presslaff\\properties\\atu_rgn.properties");
		}
		
		//Logger class
		Logger log = Logger.getLogger(CS_EditableValidation.class);
		
		    private WebDriver driver = null;
		    private LoginPage loginpage = null;
		    private HomepageValidation homePage=null;
		    private LoginProperty property = null;
		    private CS_Editable contestEditable = null;
		    private ATUReporter atu = null;
		    private String p_URL = null;
		    String startDate = null;
		    String endDate = null;
		    String getmail = null;
	
		 // Create Object for Properties Class
		    ReadfromProperties prop = new ReadfromProperties();

	    @BeforeTest
	      public void setup() throws Exception {
			log.info("Contest setup-Editable Validation");

			driver = new FirefoxDriver();
			driver.manage().timeouts().implicitlyWait(10,TimeUnit.SECONDS);
			
			loginpage = new LoginPage(driver);
			property = new LoginProperty(driver);
			
			homePage = new HomepageValidation(driver);
			contestEditable = new CS_Editable(driver);
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
		public void EditableContest_login(String username, String password) throws Exception {
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
				log.error("Element not found"+" "+e.getMessage());
			}
	  }
		//-----------Test method for verifying contestpage and buttons----------
		@SuppressWarnings("deprecation")
		@Test()
		public void verify_contestPageandBtns() throws Exception {
			   Keywords.testName = "Verify contest setup page and buttons";
			   log.info("@Test2: Verifying the contest setup page and buttons");
			   
			try {
				// Click on Setup
				contestEditable.click_Setup();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				contestEditable.click_AddContest();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
	
				//Verify the buttons and fileds under addContest module
				Assert.assertTrue(contestEditable.isContestSetupPageAndBtns());
				log.info("Assert:Verify buttons under contest module");	
				
			} catch (Exception e) {
				e.printStackTrace();
				log.error("Element not found"+" "+e.getMessage());
			}
	  }

		
		//----------Test method for verifying error message without giving start date--------
		@DataProvider(name = "ContestSetup_Editable_Rgn_WOstartDate")
		public Object[][] dataProvider_valid1() {
			Object[][] testData = poi_Reader_e.readExcelData(
					"..\\Presslaff\\testdata\\resources\\Contest_WO_StartDate.xls", "login",
					"Login_Data");
			return testData;
		}
		
		@SuppressWarnings("deprecation")
		@Test(dataProvider = "ContestSetup_Editable_Rgn_WOstartDate", description = "Verifying the error message without giving start date")	
		public void contestSetup_Editable_WOstartDate(String contestName,String contestType,String headingText) throws Exception {
			   Keywords.testName = "ContestSetup_Editable_Rgn_WOstartDate";
			   log.info("@Test3: Create contest without startdate");
			try {			
				//Enter the name of the Contest
				contestEditable.type_Contestname(contestName);
				
				//Select contest type as Editable
				contestEditable.select_ContestType(contestType);
				
				//Remove the autofilled startDate and enter endDate,Text.
				contestEditable.clear_StartDate();
				contestEditable.edate(endDate);
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				//Enter text at heading
				contestEditable.contest_Heading(headingText);
				//Thread.sleep(2000l);
				
				//Save the contest
				contestEditable.save_contest();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
				//Verify error message
				Assert.assertTrue(contestEditable.verify_SDErrrMsg());
				log.info("Assert:Verify Error message without StartDate ");

			} catch (Exception e) {
				e.printStackTrace();
				log.error("Element not found"+" "+e.getMessage());
			}
	  }
		
		  //--------Test method for verifying error message without giving enddate---------
			@DataProvider(name = "ContestSetup_Editable_Rgn_WOendDate")
			public Object[][] dataProvider_valid2() {
				Object[][] testData = poi_Reader_e.readExcelData(
						"..\\Presslaff\\testdata\\resources\\Contest_WO_EndDate.xls", "login",
						"Login_Data");
				return testData;
			}
			
			@SuppressWarnings("deprecation")
			@Test(dataProvider = "ContestSetup_Editable_Rgn_WOendDate", description = "Verifying the error message without giving end date")
			public void contestSetup_Editable_WOendDate(String contestName, String contestType,String headingText) throws Exception {
				   Keywords.testName = "ContestSetup_Editable_Rgn_WOendDate";
				   log.info("@Test4: Create contest without enddate");
				   
				try {
					// Click on Setup
					contestEditable.click_Setup();
					contestEditable.click_AddContest();
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					
					//Enter the name of the Contest
					contestEditable.type_Contestname(contestName);
					
					//Select contest type as Editable
					contestEditable.select_ContestType(contestType);
					
					//Enter text at heading
					contestEditable.contest_Heading(headingText);
					//Thread.sleep(2000l);
					
					//Save the contest
					contestEditable.save_contest();
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
					
					//Verify error message
					Assert.assertTrue(contestEditable.verify_EDErrrMsg());
					log.info("Assert:Verify Error message without EndDate ");
			    	
				} catch (Exception e) {
					e.printStackTrace();
					log.error("Element not found"+" "+e.getMessage());
		      }
	  }
			   //---------Test method for verifying error message without giving contestname-----------
				@DataProvider(name = "ContestSetup_Editable_Rgn_WOcontestName")
				public Object[][] dataProvider_valid3() {
					Object[][] testData = poi_Reader_e.readExcelData(
							"..\\Presslaff\\testdata\\resources\\Contest_WO_Contestname.xls", "login",
							"Login_Data");
					return testData;
				}
				
				@SuppressWarnings("deprecation")
				@Test(dataProvider = "ContestSetup_Editable_Rgn_WOcontestName", description = "Verifying the error message without giving contest name")
				public void contestSetup_Editable_WOcontestName(String contestType,String headingText) throws Exception {
					   Keywords.testName = "ContestSetup_Editable_Rgn_WOcontestName";
					  
					   log.info("@Test5: Create contest without contestname");
					   
					try {
						
						// Click on Setup
						contestEditable.click_Setup();
						contestEditable.click_AddContest();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//Select contest type as Editable
						contestEditable.select_ContestType(contestType);
						
						
						//Enter endDate
						contestEditable.edate(endDate);
						
						//Enter text at heading
						contestEditable.contest_Heading(headingText);
						//Thread.sleep(2000l);
						
						//Save the contest
						contestEditable.save_contest();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//Verify error message
						Assert.assertTrue(contestEditable.verify_CNErrrMsg());
						log.info("Assert:Verify Error message for without Contentname");
				    	
				    	
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Element not found"+" "+e.getMessage());
					}
			}
				
				//---------Test method for verifying error message with blank data----------
				@Test()
				
				public void contestSetup_Editable_Blankdata() throws Exception {
					   Keywords.testName = "contestSetup_Editable_Blankdata";
					  
					   log.info("@Test6: Create contest with blankdata");
					   
					try {
						
						// Click on Setup
						contestEditable.click_Setup();
						contestEditable.click_AddContest();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//Clear the auto filled start date
						contestEditable.clear_StartDate();
						
						//Save the contest
						contestEditable.save_contest();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//Verify error message
						Assert.assertTrue(contestEditable.verify_Errmsg_BlankData());
						log.info("Assert:Verify Error message for blank data");
				    	
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Element not found"+" "+e.getMessage());
					}
				}
				
				//--------Test method for verifying error message with multiple entries hourly----------
				@DataProvider(name = "contestSetup_Editable_WithMultipleEntries_Hourly")
				public Object[][] dataProvider_valid4() {
					Object[][] testData = poi_Reader_e.readExcelData(
							"..\\Presslaff\\testdata\\resources\\Contest_Editable_multipleEntries.xls", "login",
							"Login_Data");
					return testData;
				}
				
				@SuppressWarnings("deprecation")		
				@Test(dataProvider = "contestSetup_Editable_WithMultipleEntries_Hourly", description = "Verifying the error message with multiple entries,Hourly")
				
				public void contestSetup_Editable_WithMultipleEntries(String contestName, String contestType,String headingText) throws Exception {
					   Keywords.testName = "contestSetup_Editable_WithMultipleEntries";
					  
					   log.info("@Test7: Create contest with multiple entries hourly");
					   
					try {			
						// Click on Setup
						contestEditable.click_Setup();
						contestEditable.click_AddContest();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//Enter the name of the Contest
						contestEditable.type_Contestname(contestName);
						
						//Select contest type as Editable
						contestEditable.select_ContestType(contestType);
						
						//Enter endDate
						
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						contestEditable.hourly();
						
						//Enter text at heading
						contestEditable.contest_Heading(headingText);
						Thread.sleep(3000l);
						contestEditable.edate(endDate);
						
						//Save the contest
						contestEditable.save_contest();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//Verify error message
					     Assert.assertTrue(contestEditable.verify_multipleEntriesErrmsg());
						log.info("Assert:Verify Error message for multiple entries");
			  	
				    	
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Element not found"+" "+e.getMessage());
					}
			}
				
				//----------Test method for verifying enddate by enabling never ends------------
				@DataProvider(name = "contestSetup_Editable_verifyNeverends")
				public Object[][] dataProvider_valid5() {
					Object[][] testData = poi_Reader_e.readExcelData(
							"..\\Presslaff\\testdata\\resources\\Contest_Editable_verifyNeverends.xls", "login",
							"Login_Data");
					return testData;
				}
				@SuppressWarnings("deprecation")				
				@Test(dataProvider = "contestSetup_Editable_verifyNeverends", description = "Verifying end date by selecting never ends")
				
				public void contestSetup_Editable_verifyNeverends(String contestName, String contestType,String headingText) throws Exception {
					   Keywords.testName = "contestSetup_Editable_verifyNeverends";
					  
					   log.info("@Test8: Create contest and verify Neverends");
					   
					try {			
						// Click on Setup
						contestEditable.click_Setup();
						contestEditable.click_AddContest();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
						//Enter the name of the Contest
						contestEditable.type_Contestname(contestName);
						
						//Select contest type as Editable
						contestEditable.select_ContestType(contestType);
						
						//check in neverEnds
						contestEditable.checkin_neverEnds();
						
						//Enter text at heading
						contestEditable.contest_Heading(headingText);
						//Thread.sleep(2000l);
						
						
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//Verify error message
						Assert.assertTrue(contestEditable.isEndDateDisabled());
				    	
					} catch (Exception e) {
						e.printStackTrace();
						log.error("Element not found"+" "+e.getMessage());
					}
			  }
				
				//----------Test method for creating contest as editable------------
				@DataProvider(name = "contestSetup_Editable")
				public Object[][] dataProvider_valid6() {
					Object[][] testData = poi_Reader_e.readExcelData(
							"..\\Presslaff\\testdata\\resources\\Contest_Editable.xls", "login",
							"Login_Data");
					return testData;
				}
				@SuppressWarnings("deprecation")				
				@Test(dataProvider = "contestSetup_Editable", description = "Verifying the editable contest")
				
				public void contestSetupEditable_pagewizard(String contestName, String contestType,String headingText,String header_contestPage,String header_loginpage,String header_confirmationPage) throws Exception {
					   Keywords.testName = "contestSetup_Editable";
					  
					   log.info("@Test9: Create contest as Editable");
					   
					try {			
						// Click on Setup
						contestEditable.click_Setup();
						//driver.findElement(By.linkText("welcome")).click();
						
						//Click Add contest
					    contestEditable.click_AddContest();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				
						//Enter the name of the Contest
						contestEditable.type_Contestname(contestName);
						
						//Select contest type as Editable
						contestEditable.select_ContestType(contestType);
						
						//Enter endDate,Text.
						contestEditable.edate(endDate);
						
						//Enter text at heading
						//contestEditable.contest_Heading(headingText);
						Thread.sleep(2000l);
						
						//Save the contest
						contestEditable.saveEditableContest();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//Verify the link and buttons
						Assert.assertTrue(contestEditable.isContestLinkAndBtns());
						log.info("Assert:Verify buttons and link");
						
						//Click page wizard button
						contestEditable.click_pageWizard();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
					    Assert.assertTrue(contestEditable.isPageWizardOpened());
						
						//double click on upload banner image link
						contestEditable.doubleClick_bannerimage();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						Thread.sleep(2000);
						
						//Select the image
						contestEditable.uploadBannerImage();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				        
						contestEditable.getsourceOfBanner_ContestPage();
						
						//verify the page wizard upload banner
					    Assert.assertTrue(contestEditable.isPW_UploadBanner());
						log.info("Assert:Image uploaded in page wizard");
						Thread.sleep(2000);
						
						//Double click on add contest heading
						contestEditable.pw_addContestHeading();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
												
						 //ck editor at contest page
						contestEditable.contest_PW_contestPage_ckeditor(header_contestPage);
						Thread.sleep(3000);
						
						//save ckfinder changes
						contestEditable.ckfinder_save();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
							
						//Select page wizard login page
						contestEditable.select_PW_login_page();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						Thread.sleep(2000);
											
						//Verify that the login page was opened or not
						Assert.assertTrue(contestEditable.isPW_loginpageOpened());
						
						//verify the source of login page
						contestEditable.getsourceOfBanner_LoginPage();
						
						//verify the banner of login page
						contestEditable.verifyBannerOfLoginPage();
					
						//Double click contest heading at login page
						contestEditable.contestHeading_loginPage();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);			
						
						//ck editor at page wizard login page
						contestEditable.contest_PW_loginpage_ckeditor(header_loginpage);
						
						//save ckfinder changes
						contestEditable.ckfinder_save();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//verify contest heading at login page
						contestEditable.verifyPW_Login_Contest_Header(header_loginpage);						
																
						//select page wizard confirmation page
						contestEditable.select_PW_confirmation_page();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						Thread.sleep(2000);
						
						//verify the source of Confirmation page
						contestEditable.getsourceOfBanner_ConfirmationPage();
						
						//verify the banner of confirmation page						
						contestEditable.verifyBannerOfConfirmationPage();
						Thread.sleep(3000);;
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//Double click on contest heading at confirmation page
						contestEditable.contestHeading_confirmationPage();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//ck editor at page wizard confirmation page
						contestEditable.PW_confirmation_ckeditor(header_confirmationPage);
						
						//save ck finder changes
						contestEditable.ckfinder_save();
						driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
						
						//verify contest heading at confirmation page
						contestEditable.verifyPW_Confirmation_Contest_Header(header_confirmationPage);
						
						//Back to contest
						contestEditable.backToContest();
					}
					catch (Exception e) {
						e.printStackTrace();
						log.error("Element not found"+" "+e.getMessage());
					}
			  }
				
				//-----------Test method for adding question to the editable contest------------
				@Test()
				public void AddQuestion_Editable() throws Exception{
					 Keywords.testName = "Adding question to the contest";					  
				      log.info("@Test10:Adding question to the contest ");
							   
				try {	
				//Click add question
				contestEditable.Click_AddQuestion();
				
				//Enter question
				contestEditable.Type_Question();
				
				//Verify the question type
				contestEditable.isQuestionTypeisText();
				
				//save the question
				contestEditable.Save_Question();
				
				//Click on back to contest at add question page
				contestEditable.Click_BacktoContest();
				driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);			
				
				}
				catch (Exception e) {
					e.printStackTrace();
					log.error("Element not found"+" "+e.getMessage());
				}
		  }
								
			//-----------Test method for launching editable contest------------
			@DataProvider(name = "Launching editable contest")
			public Object[][] dataProvider_valid7() {
				Object[][] testData = poi_Reader_e.readExcelData(
						"..\\Presslaff\\testdata\\resources\\LaunchContestLink.xls", "login","Login_Data");
							return testData;
				}
			@SuppressWarnings("deprecation")				
			@Test(dataProvider = "Launching editable contest", description = "Launching the editable contest and complete the regestration")						
			public void LaunchingEditableContest(String header_contestPage,String header_loginpage,String header_confirmationPage) throws Exception {
			          Keywords.testName = "Launching editable contest and complete the registration";					  
				      log.info("@Test11: Launching editable contest");
							   
						try {								
						// Launch Contest link
						contestEditable.openSureveyLink();
														
						// Verify front page login header
						contestEditable.frontPage_login(header_loginpage);
	
						// Enter email id to login 
						contestEditable.typeEmailid(getmail);
						
						// Enter Submit
						contestEditable.emailSubmit();
						
						// Navigate to the Registration page
						//contestEditable.fp_RegistrationHeader(header_contestPage);
						
						//Type Answer for contest question
						contestEditable.fp_Registration_type_Answer();
						
						// Enter First name
						contestEditable.fp_Registration_type_firstname();
						
						// Enter last name
						contestEditable.fp_Registration_type_lastname();
						
						// Select Gender
						contestEditable.fp_Registration_Select_Gender();
						
						// Select Date of Birth
						contestEditable.fp_Registration_Select_DOB();
						
						// Select Local Opt in
						contestEditable.fp_Registration_Select_optin();
						
						// Enter Submit
						contestEditable.fp_RegSubmit();
											
						// Navigate to the confirmation page(navigate back to contest - Dat-e-base)
						contestEditable.fp_ConfirmationHeader(header_confirmationPage);
							
						// Navigate to setup page
						contestEditable.click_Setup();
						
						// Click on editable contest
						contestEditable.click_CS_Editable_Rgn();
						
						// Delete the contest
						contestEditable.delete();
						
						// Handling alert for deleting the message
						contestEditable.alert();
												
						// logout
						contestEditable.logout();
					}
					
   			 catch (Exception e) {
						e.printStackTrace();
						log.error("Element not found"+" "+e.getMessage());
					}
			  }
		
				
			 //Quit from browser after test
	@AfterTest
	public void aftertest() throws Exception {

			driver.close();
			driver.quit();

			}
	}

	


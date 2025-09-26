package testBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Platform;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.openqa.selenium.remote.RemoteWebDriver;

public class BaseClass {
	public static WebDriver driver;
	public Logger logger; //logger log4j
	public Properties p;
	
	
	@BeforeClass(groups= {"sanity","regression","master"})
	@Parameters({"os","browser"})
	public void setup(String os, String br) throws IOException 
	{
		
		//loading config.properties file
		FileReader file = new FileReader(".//src//test//resources//config.properties");
		p=new Properties();
		p.load(file);
	
		
		
		logger = LogManager.getLogger(this.getClass()); // log4j2
		
		
		//execution in remote
		if(p.getProperty("execution_env").equalsIgnoreCase("remote"))
		{
			//created class
			DesiredCapabilities capabilities = new  DesiredCapabilities();
			
		
			//os- coming from xml file
			
			if(os.equalsIgnoreCase("windows"))
			{
				capabilities.setPlatform(Platform.WIN11);
			}
			else if(os.equalsIgnoreCase("mac"))
			{
				capabilities.setPlatform(Platform.MAC);
			}
			else
			{
				System.out.println("No matching os");
				return;
			}
			
			
			//browswer -coming from xml file
			switch(br.toLowerCase())
			{
				case "chrome": capabilities.setBrowserName("chrome"); break;
				case "edge": capabilities.setBrowserName("MicrosoftEdge"); break;
				default: System.out.println("No matching browser"); return;
			}
			
			driver = new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),capabilities);
			
		}
		
		
		//execution in local
		if(p.getProperty("execution_env").equalsIgnoreCase("local"))
		{
		
		//for local env //before grid 
		switch(br.toLowerCase())
		{
		case "chrome" : driver=new ChromeDriver();break;
		case "edge" : driver=new EdgeDriver();break;
		case "firefox" : driver=new FirefoxDriver();break;
		default : System.out.println("invalid browser name..");
		}
		}
		
		driver.manage().deleteAllCookies();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
		driver.get(p.getProperty("appURL"));//reading url from prop file
		driver.manage().window().maximize();
	}
	
	@AfterClass(groups= {"sanity","regression","master"})
	public void tearDown()
	{
		driver.close();
	}	
	
	
	//random generate
	public String randomeString()
	{
		String generatedstring = RandomStringUtils.randomAlphabetic(5);
		return  generatedstring;
	}
	
	
	public String randomeNumber()
	{
		String generatednumber = RandomStringUtils.randomAlphanumeric(10);
		return  generatednumber;
	}
	
	
	public String randomeAlphaNumeric()
	{
		String generatedstring = RandomStringUtils.randomAlphabetic(3);
		String generatednumber = RandomStringUtils.randomAlphanumeric(3);
		return  (generatedstring+"@"+generatednumber);
	}
	
	
	
	public String captureScreen(String tname) throws IOException {

		/*
		 * //bellow  - due to simpledateformat below code written by ai
		 
		 String timeStamp = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		 */
		
		
		String timeStamp;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
			timeStamp = sdf.format(new Date());
		} catch (Exception e) {
			// Fallback to system time if SimpleDateFormat fails
			timeStamp = String.valueOf(System.currentTimeMillis());
		}
				
		TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
		File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
		
		String targetFilePath=System.getProperty("user.dir")+"\\screenshots\\" + tname + "_" + timeStamp + ".png";
		File targetFile=new File(targetFilePath);
		
		sourceFile.renameTo(targetFile);
			
		return targetFilePath;

	}
}

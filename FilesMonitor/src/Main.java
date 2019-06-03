import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicLong;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

// Created by Rachid AZGAOU @ Itron 2018


public class Main {


	
	// --> check all the files in a folder and sub-folders , alert if a file is bigger than X 
	// the X can be set up in the Config.xml file
	
	// find log file containes a keyword , find the com server of a meter
	
	// if big size --> execute .bat file set up in the config gile
	
	// the config.xml file contains the the servers , the Path , the target size
	
	
	
	public static void main(String[] args) {

		System.out.println("FilesMonitor V0.1 : Size Monitoring tool , Starting..  ");

		if ( ReadConfig()==1) return ;
		
		
		// ------------- test bloc 
		
	//	if (1==1) return;  // for test
		
		
		// C:\Users\razgaou\Desktop\Fr-docs
		// \\172.24.80.56\\c$\\Asais\\Saturne\\CommunicationServer\\log\\"+TodayDate
		
		// \\172.24.80.52\c$\Asais\Saturne\CommunicationServer\log\20180208\ZMQ
		
	// ------------------
		
		
		/*
		
		// ------------- test bloc 
		// max total size was in 20180131 = 327 Mb
		//String Path="C:\\Users\\razgaou\\Downloads";
		String Path="C:\\Users\\razgaou\\eclipse-workspace2\\FileWriter";
		int x=1;
		while (x==1)
		CheckSize(Paths.get(Path), 1, "alert.bat");
		
		// ----------------
		
		*/
		
		// get all the paths
		String serversString =ConfigMap.get("servers");
		String [] ServersList = serversString.split("-");
		
		// get the path from the config file
		String pathConfig =ConfigMap.get("path");
		
		int SizeTarget =Integer.parseInt( ConfigMap.get("size"));
		
		
		String BatchToExecute = ConfigMap.get("Execute");
		
		
		
		// concat the path with the server (\\\\172.24.80.56\\c$\\Asais\\Saturne\\\\CommunicationServer\\log\\"+TodayDate)
		// has to be like that :  \\172.24.80.55\c$\Asais\Saturne\CommunicationServer\log\2018020
		                             
	
		for (int i=0 ;i<ServersList.length;i++)	{
			ServersList[i]="\\\\"+ServersList[i]+pathConfig;
			//System.out.println(ServersList[i]);
			
		}
		
		
		
		// ---------> call the main function for all the servers in infinite loop
		
		while (true) 	{
			
			System.out.println("Date & Time Start  : " +	displayDateTime());
		
		for(String serverPath:ServersList) {
		
		long Total=	CheckSize(Paths.get(serverPath.trim()),SizeTarget,BatchToExecute);
		
		System.out.println("Total size of this folder  : " + Total/1048576 + " MB" );
			
		
		}  // end for
		
		
		System.out.println("Date & Time End  : " +	displayDateTime());
		System.out.println("  ");
		
		
		/* sleep bloc
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/ // end sleep bloc
		
		System.gc();
		
		
		} // end while
		//--------------
		
		
		
		
		
		
	} // end main
	
	// display date time
	static Date dateNow;
	public static String displayDateTime() {

				dateNow = new Date();
			return  dateNow.toString();
			
	}
	
	
	public static long CheckSize(Path path,int SizeTarget,String BatchToExecute) {

		System.out.println("Checking for " + path);
		
	    final AtomicLong size = new AtomicLong(0);

	    try {
	        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
	            @Override
	            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {

	              //  size.addAndGet(attrs.size());
	               // System.out.println("File : " + file.getRoot() );
	             //   int Size=(int) (attrs.size()/1048576);
	                
	            //    System.out.println("File  : " + file + "; Size : " + Size + " MB");
	                
	              //  if (Size>SizeTarget) Alert(file.toString(),Size,BatchToExecute);
	                
	                
	               // print file size in other way
	                File fileNew =new File((file.toString()));
	                long FileSize=fileNew.length();
	                size.addAndGet(FileSize);
	    			int megabytes = (int)(FileSize  /1048576);
	    			//System.out.println("megabytes : " + megabytes);
	    			fileNew=null;
	    			// System.out.println("File  : " + file + "; Size : " + megabytes + " MB");
	    			if (megabytes>=SizeTarget) Alert(file.toString(),megabytes,BatchToExecute);
	    			
	    			
	                return FileVisitResult.CONTINUE;
	            }

	            

				@Override
	            public FileVisitResult visitFileFailed(Path file, IOException exc) {

	                System.out.println("[ERROR]  Path Unreachable: " + file );
	                System.out.println("Check your VPN/Proxy/Internet!" );
	                // Skip folders that can't be traversed
	                return FileVisitResult.CONTINUE;
	            }

	            @Override
	            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {

	                if (exc != null)
	                    System.out.println("had trouble traversing: " + dir + " (" + exc + ")");
	                // Ignore errors traversing a folder
	                return FileVisitResult.CONTINUE;
	            }
	        });
	    } catch (IOException e) {
	        throw new AssertionError("walkFileTree will not throw IOException if the FileVisitor does not");
	    }

	    return size.get();
	}

	
	
	public static void Alert(String file,int size,String BatchToExe) {
		
		 System.out.println("[WARNING !] : The file '" + file + "' is " + size + " MB" );
		 
		 try {
			Runtime.getRuntime().exec("cmd /C start "+BatchToExe + " " + file );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
	
		 /* continu after theu ser input
		 
		 
		 Scanner sn = new Scanner(System.in);
		sn.nextLine();
	//	sn.close();
		 
		 
		 */
		 
	}
	
	
	
	// the Hashmap containes these keys : Servers(string value separated with a '-' , Size , Path ) 
	public static HashMap<String, String> ConfigMap = new HashMap<String,String>();
	public static int ReadConfig()  {
		
		
		// this to replace the keyword [@TodayDate] by Todaydate if exists
		String TodayDate;
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		Date date = new Date();
		//System.out.println(dateFormat.format(date)); // 20180208
		TodayDate=dateFormat.format(date);
		
		
		 try {

		 DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
         DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
         Document doc = docBuilder.parse (new File("Config.xml"));

         // normalize text representation
         doc.getDocumentElement ().normalize ();
//         System.out.println ("Root element of the doc is " + 
//              doc.getDocumentElement().getNodeName());


         NodeList listOfServers = doc.getElementsByTagName("server");
         int totalServers = listOfServers.getLength();
         System.out.println("Total number of Servers : " + totalServers);
         
         // get the servers 
         String Servers="";
         for(int i=0; i<listOfServers.getLength() ; i++){


             Node firstServerNode = listOfServers.item(i);
                              
             if(firstServerNode.getNodeType() == Node.ELEMENT_NODE){
            	 
            //	 System.out.println("Server : " + 	 firstServerNode.getTextContent() );
            
            	 Servers=Servers+  firstServerNode.getTextContent()+ "-";
            	 
            	 
             }//end of if clause


         }//end of for servers loop 

      //   System.out.println("All Servers : " + 	Servers );
            
         ConfigMap.put("servers", Servers);
         
        
         
         // get the target size 
         
         String SizeTarget = doc.getElementsByTagName("Size").item(0).getTextContent().trim();
         ConfigMap.put("size", SizeTarget);
         System.out.println("TargetSize MB: " + SizeTarget );
            	 
         String PathTarget =doc.getElementsByTagName("Directory").item(0).getTextContent().trim();
         PathTarget=PathTarget.trim();    
         PathTarget= PathTarget.replace("[@TodayDate]", TodayDate );
         ConfigMap.put("path", PathTarget);
      //   System.out.println("TargetPath : " + PathTarget );
         
         String Execute =doc.getElementsByTagName("Execute").item(0).getTextContent().trim();
         ConfigMap.put("Execute", Execute);
        // System.out.println("Execute : " + Execute );
         
         
         		docBuilderFactory  =null;
                 docBuilder = null;
                 doc = null;
         
         

     }catch (SAXParseException err) {
     System.out.println ("** Parsing error" + ", line " 
          + err.getLineNumber () + ", uri " + err.getSystemId ());
     System.out.println(" " + err.getMessage ());

     return 1;
     
     }catch (SAXException e) {
     Exception x = e.getException ();
     ((x == null) ? e : x).printStackTrace ();
     return 1;
     }catch ( java.io.FileNotFoundException ex) {
    	 System.out.println(" " + ex.getMessage ());
         return 1;
    
     
     }catch (Throwable t) {
     t.printStackTrace ();
     return 1;
     
     }
		
		 
		 	TodayDate=null;
			 dateFormat = null;
			 date = null;
		 
		
		 return 0;
		
		
	}
	
	
	
}

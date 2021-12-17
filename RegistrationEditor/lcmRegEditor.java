import java.io.*;
import java.awt.event.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.*;
import java.lang.*;
import java.util.*;
import java.util.List;

import lcm.logging.*;
import lcm.lcm.*;
import lcm.util.*;
import lcm.*;
import java.nio.charset.StandardCharsets.*;
import java.nio.charset.Charset;
import java.awt.*;

import javax.swing.border.Border;
public class lcmRegEditor
{
	//GUI + IO
	public static String Filein;
	public JFrame frame = new JFrame("LCM Registration Editor");
	public JPanel regFrame = new JPanel();
	public JTextArea txt = new JTextArea();
	public JTextArea chanin = new JTextArea();
	public List<JTextArea> regInput = new ArrayList<JTextArea>();
	public JButton subbtn = new JButton("Submit Log Choice");
	public JButton aspnbtn = new JButton();
	public static JTextArea txtboxFilename = new JTextArea();

	//LCM STUFF
	public static LCM lcm;
	public static Log logout;
	public static Log login;

	//Computational stuff
	public List<String> regNames = new ArrayList<String>();
	public int numOfRegs = 0;

	public static void main(String[] args) throws Exception
	{
		lcmRegEditor starter = new lcmRegEditor();
		System.out.println("Start Program");
		
		// List<String> reg = new ArrayList<String>();		
		// String tester = "D";
		// reg.add(tester);
		// tester = "E";
		// if(!reg.contains(tester) || reg.isEmpty())
		// {
		// 	reg.add(tester);
		// 	tester = "F";
			
		// }
		// for (int i = 0; i < reg.size(); i++)
		// {
		// 	System.out.println(reg.get(i));
		// }
		//FLOW:
		//Start
		//CreateGui
		//FileCHoice
		//LogChosen
		//SearchLog()
		//RegTable
		//WriteLog
		starter.CreateGui();
		
	}
	public void searchLog(Log in)
	{
		//Will count the number of unique registration names
		//Will Store all of the unique existing registration names
		System.out.println("in searchlog");
		Log.Event event = new Log.Event();
		
		while (true)
		{
			try 
			{
				
				event = in.readNext();
				//System.out.println("chnl name of event: " + event.channel);
				//System.out.println(numOfRegs);
				if (regNames.isEmpty())
				{
					//System.out.println("names null?");
					regNames.add(event.channel);
					//System.out.println(regNames.get(numOfRegs));
					numOfRegs = numOfRegs + 1;
					
					//System.out.print(numOfRegs);
				}
				else if (!regNames.contains(event.channel))
				{
					//System.out.println("Checking if name is already stored");
					//System.out.print(numOfRegs);
					regNames.add(event.channel);
					numOfRegs = numOfRegs + 1;
				}
			}catch(IOException e)
				{
					//System.out.println("EOF within searchLog");
					try
					{
						in.close();
					}
					catch(IOException f)
					{
						f.printStackTrace();
						break;
					}
					//e.printStackTrace();
					break;
				}

			}

	}
	public void logChosen()
	{
		System.out.println("IN LOG CHOSEN akaakaka pressed the button once");
		try
		{
			login = new Log(Filein, "r");
			System.out.println("Log file selected: " + Filein);
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		// FIND log data
		System.out.println("Calling seachLog");
		searchLog(login);
		// Updates GUI	

		System.out.println("rm regFrame");
		frame.remove(regFrame);
		System.out.println("Calling regFrame");
		regFrame = RegTable(numOfRegs, regNames);
		System.out.println("Calling regFrame finished, adding frame?");
		frame.add(regFrame);
		System.out.println("regFrame added, removing button");
		frame.remove(subbtn);
		subbtn = new JButton("Submit Registration Changes");
		frame.add(subbtn);
		frame.setVisible(true);
		subbtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(Filein != null && Filein != "")
				{
					System.out.println("PRESSED BUTTON to WRITELOG");
					WriteLog();
				}
				
			}
		});
		
	}
	public void WriteLog()
	{
		System.out.println("In writelog");
		//Event has: 
		//long utime
		//long eventNumber
		//byte[] data
		//String Channel
		//
		//Log has: 
		//lcm = LCM.getSingleton();
		try
		{
			System.out.println("inside of writeLog");
			//System.out.println(Filein);
			//login.close();
			//login = new Log(Filein, "r");
			Log logfresh = new Log(Filein, "r");
			File filechkold = new File("Output_Log_Testerino_1");
			if (filechkold.exists())
			{
				filechkold.delete();
			}
			File filedynamic = new File(txtboxFilename.getText());
			if (filedynamic.exists())
			{
				filedynamic.delete();
			}
			logout = new Log(txtboxFilename.getText(), "rw");
			// for (int i = 0; i < regInput.size(); i++)
			// {
			// 	System.out.println(regInput.size());
			// 	System.out.println(regInput.get(i).getText());
			// }
			//BufferedReader br = new BufferedReader(new FileReader(Filein));
			Log.Event event = new Log.Event();
			Log.Event eventout = new Log.Event();
			while (true)
			{
				try
				{
				event = logfresh.readNext();
				
				for (int b = 0; b < regNames.size(); b++)
				{
					//System.out.println("EVENT NAME BEFORE CHANGE: " + event.channel + "\nCompared to regNames: " + regNames.get(b));
					if(event.channel.equals(regNames.get(b)))
					{
						//System.out.println("txtbox contents: " + regInput.get(b).getText() + "\nb value: " + String.valueOf(b));
						//System.out.println("FOUND MATCH\n" + event.channel + "==" + regNames.get(b));
						if (regInput.get(b).getText().equals(String.valueOf(b)))
						{
							System.out.println("Event channel unchanged: " + event.channel);
							eventout = event;
							//break;	
						}
						else
						{							
							//System.out.println("box and b are not equaltxtbox contents: " + regInput.get(b).getText() + "\nb value: " + String.valueOf(b));
							eventout = event;
							eventout.channel = regInput.get(b).getText();		
							//System.out.println("EVENT NAME AFTER CHANGE: " + eventout.channel);	
						}
					}
				//Pull event
				//Check the chhannel vs stored changes
				//apply the change and add event to the logout
				}
				logout.write(eventout);
				}catch(EOFException e)
				{
					//e.printStackTrace();
					login.close();
					break;
				}
				
			}


		}
		catch(IOException e)
				{
					e.printStackTrace();
				}
			System.out.println("JOBS DONE!");
	}
	public void FileChoice()
	{
		JFileChooser f = new JFileChooser();
		f.setSize(400,400);
		String FPath = "";
		int result = f.showOpenDialog(frame);
		if(result == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = f.getSelectedFile();
			FPath = selectedFile.getPath();
		}
		Filein = FPath;
	}
	public JPanel RegTable(int regNum, List<String> Names)
	{
		//VARIABLES CORRECT: regNUm = 10, Names has all registration names inside
	//	for (int i =0; i < Names.size(); i++)
	//	{
	//		System.out.println(Names.get(i));
	//	}
	//	System.out.println(regNum);

		regFrame = new JPanel();
		//regFrame.setPreferredSize(new Dimension(1000,1000));
		regFrame.setVisible(true);
		GridLayout reggieout = new GridLayout(regNum + 2,2);
		regFrame.setLayout(reggieout);
		Border bord = BorderFactory.createLineBorder(Color.BLACK);
		for (int a = 0; a < regNum; a++)
		{
			regInput.add(new JTextArea(String.valueOf(a)));
			regFrame.add(new JLabel(Names.get(a)));
			regInput.get(a).setBorder(bord);
			regFrame.add(regInput.get(a));
		}
		regFrame.add(new JLabel("Enter The File Name of Output Log -> "));
		
		txtboxFilename.setBorder(bord);
		regFrame.add(txtboxFilename);
		aspnbtn.setText("Click here to apply ASPN naming conventions");
		regFrame.add(aspnbtn);
		aspnbtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
					AspnCalc();				
			}
		});
		return regFrame;
	}
	public void AspnCalc()
	{
		//MAYBE CREATE AN EXCEL SHEET THAT HAS ALL OF THE ASPN NAMES AND ALL OF OUR NAMES
		String [] correspondingAspn = 	{"test1",				"test2"	
										
										,"test3",	"test4"
										,"5",		"6"
										,"7",		"8"
										,"9",		"10"
										,"imu",		"pva"
										,"etc",		"ohGod"
										,"plzwby",	"jenoo"};
		String [] allTypes = 			{"aspn/hg1700/imu",		"aspn/novatel4truth/positionvelocityattitude"	
										
										,"aspn://vikingnav/fluxmag/2001.0/0/sim",	"aspn/novatel"	,	"5",	"6",	"7",	"8",	"9",	"10",	"imu",	
											"pva",	"etc",	"ohGod",	"plzwby",	"jenoo"};
		String result = "";
		//DETERMINE ASPN NAMES
		//Check title of file???
		//Check contents?????

		//Registration check
		for (int i = 0; i < regNames.size(); i++)
		{
			//if (Filein.contains(allTypes[i]))
			//	result = correspondingAspn[i];
			for (int c = 0; c < allTypes.length; c++)
			{
				if (regNames.get(i).contains(allTypes[c]))
				{
					result = correspondingAspn[c];	
					break;	
				}
			}
			regInput.get(i).setText(result);

		}
		//chanin.setText(result);
//		if (result == "")
//		{
//			int a = Arrays.asList(allTypes).indexOf(CheckDataForChannels(allfiles.get(filecnt)));
//			System.out.println(a);
//			result = correspondingAspn[a];
//		}
//
//		if (result == "")
//		{
//			//LOOK AT DATA BEFORE HAVING TO WRITE THIS
////			//CHECK THE NUMBER OF COLUMNS AND TYPE OF DATA ETC STORE EXPECTED AND COMPARE (HOPEFULLY DONT HAVE TO DO)
	//	}
//		chanin.setText(result);
//		//Column of file check??
//		File csvin = allfiles.get(filecnt);
//		

		//Data Check????
	}
	public void CreateGui()
	{
		
		System.out.println("Enter Create GUI");
		frame = new JFrame("LCM Registration Editor");
		frame.setPreferredSize(new Dimension(1000,1000));
		frame.setVisible(true);
		GridLayout Lout = new GridLayout(3,1);
		frame.setLayout(Lout);
		JButton filebutton = new JButton("Choose LCM Log File");
		//frame.add(new JLabel("Select LCM Log File with registrations you would like to change"));
		frame.add(filebutton);
		
		frame.add(regFrame);
		//frame.add(new JLabel("<html>To change the event's channels enter new channel here.<br>Otherwise leave blank.</html>"));
		//frame.add(chanin);
		//frame.add(regFrame);
		//frame.add(new JLabel("Click Submit If You Are Ready To Submit."));
		filebutton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				FileChoice();
			}
		});
		frame.add(subbtn);
		subbtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(Filein != null && Filein != "")
				{
					logChosen();
				}
				
			}
		});

		changeFont(frame, new Font("Verdana", Font.PLAIN, 20));
		changeFont(chanin, new Font("Verdana", Font.PLAIN, 40));
	//	frame.add(chanin);
//	frame.add(txt);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		System.out.println("Leave CREATE GUI");
		
	}
	public static void changeFont(Component comp, Font font)
	{
		comp.setFont(font);
		if(comp instanceof Container)
		{
			for (Component child: ((Container)comp).getComponents())
			{
				changeFont(child, font);
			}
		}
	}
}

import java.io.*;
import java.awt.event.*;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.*;
import java.lang.*;
import java.util.*;
import lcm.logging.*;
import lcm.lcm.*;
import lcm.util.*;
import lcm.*;
import java.nio.charset.StandardCharsets.*;
import java.nio.charset.Charset;
import java.util.List;

public class csvtolcm
{
	//GUI + IO
	public static String Filein;
	public JButton filebutton = new JButton("Choose CSV File");
	public JFrame frame = new JFrame("CSV To LCM");
	public JTextArea txt = new JTextArea();
	public JTextArea chanin = new JTextArea();
	public JTextArea fileOutName = new JTextArea();
	public static LCM lcm;
	public static Log logout;
	public JButton aspnbtn = new JButton();
	public JButton subbtn = new JButton();
	public JButton nextcsvbtn = new JButton();
	//public static boolean looper = false;
	public List<File> allfiles = new ArrayList<File>();
	public int filecnt = 0;
	public JLabel currentFile = new JLabel();
	public boolean finalpass = false;

	//Data Check

	public static void main(String[] args) throws Exception
	{
		csvtolcm starter = new csvtolcm();
		System.out.println("STARTING PROGRAM");
		starter.CreateGui();
		
	}
	public void CreateGui()
	{
		frame = new JFrame("CSV to LCM Exportation");
		frame.setPreferredSize(new Dimension(1000,1000));
		frame.setVisible(true);
		GridLayout Lout = new GridLayout(3,1);
		frame.setLayout(Lout);
		
		//frame.add(new JLabel("Select CSV File To Change To LCMLog"));
		frame.add(filebutton);
		//frame.add(new JLabel("<html>To change the event's channels enter new channel here.<br>Otherwise leave blank.</html>"));
		
		frame.add(GoodiePanel());
		
		//frame.add(new JLabel("Click Submit If You Are Ready To Submit."));
		filebutton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				FolderChoice();
				//FileChoice();
				aspnbtn.setEnabled(true);
				subbtn.setEnabled(true);
			}
		});
		subbtn.setEnabled(false);
		subbtn.setText("Submit CSV #" + (filecnt + 1) +"?");
		frame.add(subbtn);
		subbtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				//if(Filein != null && Filein != "")
					//WriteLog();
				if (allfiles.get(filecnt).getName() != null && allfiles.get(filecnt).getName() != "")
				{
					WriteLog();	
				}
				
			}
		});

		changeFont(frame, new Font("Verdana", Font.PLAIN, 20));
		//changeFont(chanin, new Font("Verdana", Font.PLAIN, 20));
	//	frame.add(chanin);
//	frame.add(txt);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	public JPanel GoodiePanel()
	{
		JPanel goodies = new JPanel();
		GridLayout Lout = new GridLayout(4,1);
		goodies.setLayout(Lout);

		currentFile = new JLabel("Current CSV: ");
		goodies.add(currentFile);

		
		goodies.add(chanin);
		chanin.setText("Input Desired Channel Name");
		
		
		fileOutName.setText("File_Out_Name");
		goodies.add(fileOutName);

		aspnbtn.setText("Click here to apply ASPN channel name");
		aspnbtn.setEnabled(false);
		aspnbtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				
				AspnCalc();
			}
		});
		goodies.add(aspnbtn);
		changeFont(goodies, new Font("Verdana", Font.PLAIN, 20));
		
		return goodies;
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
	public void FolderChoice()
	{
		File selectedFile = new File("");
		JFileChooser f = new JFileChooser();
		f.setSize(400,400);
		f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		String FPath = "";
		int result = f.showOpenDialog(frame);
		if(result == JFileChooser.APPROVE_OPTION)
		{
			selectedFile = f.getSelectedFile();
			FPath = selectedFile.getPath();
		}
		String [] filenames = selectedFile.list();

		for (String individual : filenames)
		{
			allfiles.add(new File(FPath + "/" + individual));
		}
		System.out.println("All Files read");
		Update();
		Filein = FPath;
	}
	public void Update()
	{
		filebutton.setEnabled(false);
		chanin.setText("Input Desired Channel Name");
		if (filecnt < allfiles.size())
			currentFile.setText("Current CSV (" + (filecnt + 1) + " / " + allfiles.size() + "): "  + allfiles.get(filecnt).getName());
		if (filecnt + 1 < allfiles.size())
			subbtn.setText("Submit CSV # " + (filecnt + 1) + "?");
		else
		{
			subbtn.setText("Submit Final CSV?");
			finalpass = true;
		}
	}
	public void AspnCalc()
	{

		//MAYBE READ FROM A TXT FILE THAT HAS ALL OF THE COMBOS
		//Fileresults = ....
		//allTypes = filres(:,1)
		//allchans = filres(:,2)
		//etc
		// for (int i =0; i < allTypes.length; i++)
		//{
		//		if(Filein.contains(allTypes[i]))
		//			resultchan = allchans[i];
		//		return resultchan;
		//}
		String [] correspondingAspn = 	{"BaroASPN"	,	"AltASPN"	,	"ImuASPN"	,	"HMASPN",	"5",	"6",	"7",	"8",	"9",	"10",	"imu",
											"pva",	"etc",	"ohGod",	"plzwby",	"jenoo"};
		String [] allTypes = 			{"Baro-Sensor"		,	"Altitude"	,	"imu"		,	"HM"	,	"5",	"6",	"7",	"8",	"9",	"10",	"imu",	
											"pva",	"etc",	"ohGod",	"plzwby",	"jenoo"};
		String result = "";
		//DETERMINE ASPN NAMES
		//Check title of file???
		//Check contents?????

		//TITLE CHECK
		for (int i = 0; i < allTypes.length; i++)
		{
			//if (Filein.contains(allTypes[i]))
			//	result = correspondingAspn[i];
			if (allfiles.get(filecnt).getName().contains(allTypes[i]))
				result = correspondingAspn[i];
		}
		//chanin.setText(result);
		if (result == "")
		{
			int a = Arrays.asList(allTypes).indexOf(CheckDataForChannels(allfiles.get(filecnt)));
			System.out.println(a);
			result = correspondingAspn[a];
		}

		if (result == "")
		{
			//LOOK AT DATA BEFORE HAVING TO WRITE THIS
			//CHECK THE NUMBER OF COLUMNS AND TYPE OF DATA ETC STORE EXPECTED AND COMPARE (HOPEFULLY DONT HAVE TO DO)
		}
		chanin.setText(result);
		//Column of file check??
//		File csvin = allfiles.get(filecnt);
//		

		//Data Check????
	}
	public String CheckDataForChannels(File f)
	{
		String out = "";
		String line = "";
		String splitchar = ",";
		//List<String> aspn = new ArrayList<String>();
		int num = 0;
		int linecnt = 0;
		int colnum = 0;
		boolean header = false;
		//first pass
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(f));
			line = br.readLine();
			if (line != null)
			{
				String[] contents = line.split(splitchar);
				for (int i = 0; i < contents.length; i++)
				{
					if (contents[i].contains("channel") || contents[i].contains("registration or anything like that"))
					{
						header = true;
						colnum = i;
					}
				}	
			}
			//Grab value
			line = br.readLine();
			if (line != null)
			{
				String[] contents = line.split(splitchar);
				out = contents[colnum];
			}	
		}catch(IOException e)
		{
			e.printStackTrace();
		}
		System.out.println(out);
		return out;
	}
	public void WriteLog()
	{
		//Event has: 
		//long utime
		//long eventNumber
		//byte[] data
		//String Channel
		//
		//Log has: 
		System.out.println("IN WRITELOG");
		String line = "";
		String splitchar = ",";
		//List<String> aspn = new ArrayList<String>();
		long nanotime_start;
		long utime_start;
		int num = 0;
		int linecnt = 0;
		//lcm = LCM.getSingleton();
		try
		{
		//	System.out.print(Filein);

			if(filecnt == 0)
			{
				File filechk = new File(fileOutName.getText());
				File filechkjlp = new File(fileOutName.getText() + ".jlp");
				if (filechk.exists())
				{
					System.out.println("Deleted :" + fileOutName.getText());
					filechk.delete();
					filechkjlp.delete();
				}
				logout = new Log(fileOutName.getText(), "rw"); 
			}
			BufferedReader br = new BufferedReader(new FileReader(allfiles.get(filecnt)));
		//	if (Filein.contains("imu"))
		//	{
		//		//aspn.add("EEXXAAMMPPLLEEIIMMUUAASSPPNNCCHHAANNEELLNNAAMMEE");
		//	}
		//CURRENT SETUP: 

			System.out.println("trying to go through file: " + allfiles.get(filecnt).getName());

			while ((line = br.readLine()) != null)
			{
				String[] contents = line.split(splitchar);
				Log.Event event = new Log.Event();
				String in = contents[0];
				System.out.println("" + in);
			//	if(chanin.getText() != "")
			//	{
			//		event.channel = chanin.getText();
			//	}
				if (in.contains("Baro"))
				{
					//UTF-8 channel name
					String raw = in;
					byte[] bytes = raw.getBytes("ISO-8859-1");
					
					String chanutf = new String(bytes, "UTF-8");
					System.out.println("In Baro\n");
					LcmGui.Baro baromsg = new LcmGui.Baro();
					LcmGui.Header head = new LcmGui.Header();
					head.sec = Long.parseLong(contents[1]);
					head.nsec = Integer.valueOf(contents[2]);
					baromsg.altitude_m = Double.valueOf(contents[3]);
					baromsg.header = head;
					
			//		event.channel = raw;
					if(chanin.getText() != "")
					{

						event.channel = chanin.getText();
					}
					else 
						event.channel = raw;
					System.out.print(event.channel + "\n");
					nanotime_start = System.nanoTime();
			//		utime_start = System.currentTimeMillis() * 1000;
			//		event.utime = utime_start + (System.nanoTime() - nanotime_start) / 1000;
				
					event.utime = baromsg.header.sec + (baromsg.header.nsec / 1000000000);

		//			event.utime = 0;
					event.eventNumber = num;
					num++;
					//GEN NEW LOG FILE. DELTA T vary time within acceptable range
					//CHRONOLOGICAL			
					System.out.println("Event written info:\nLog name:\t" + fileOutName.getText() + "\nEvent channel:\t" + event.channel + "\nEvent Utime:\t" + event.utime + "\nEvent Number:\t" + event.eventNumber);							
					logout.write(event.utime, event.channel, baromsg);
					//logout.write(event);
					logout.flush();
				}
				if(in.contains("Gps"))
				{
					//UTF-8 channel name
					String raw = "Gps";
					byte[] bytes = raw.getBytes("ISO-8859-1");
					
					String chanutf = new String(bytes, "UTF-8");
					txt.append("In Gps\n");
					LcmGui.Gps gpsmsg = new LcmGui.Gps();
					LcmGui.Header head = new LcmGui.Header();
					head.sec = Long.parseLong(contents[1]);
					txt.append("\n" + contents[1]);
					head.nsec = Integer.valueOf(contents[2]);
					txt.append("\n" + contents[2]);
					gpsmsg.header = head;
					gpsmsg.latitude_deg = Double.valueOf(contents[3]);
					txt.append("\n" + contents[3]);
					gpsmsg.longitude_deg = Double.valueOf(contents[4]);
					txt.append("\n" + contents[4]);
					gpsmsg.altitude_m = Double.valueOf(contents[5]);
					txt.append("\n" + contents[5]);
					if(chanin.getText() != "")
					{
						event.channel = chanin.getText();
					}
					else
						event.channel = raw;
					System.out.print(event.channel + "\n");
					nanotime_start = System.nanoTime();
					utime_start = System.currentTimeMillis() * 1000;
					event.utime = utime_start + (System.nanoTime() - nanotime_start) / 1000;
				//	event.utime = baromsg.header.sec * 1000000000;
					event.eventNumber = num;
					num++;
//										
					for (int i = 0; i < 50; i++)
						logout.write(event.utime, event.channel, gpsmsg);
					logout.flush();
				//	logout.close();
//					lcm.publish(event.channel, baromsg);

				}
				if(in.contains("Imu"))
				{
					//UTF-8 channel name
					String raw = "Imu";
					byte[] bytes = raw.getBytes("ISO-8859-1");
					
					String chanutf = new String(bytes, "UTF-8");
					txt.append("In Imu\n");
					LcmGui.Imu imumsg = new LcmGui.Imu();
					LcmGui.Header head = new LcmGui.Header();
					head.sec = Long.parseLong(contents[1]);
					txt.append("\n" + contents[1]);
					head.nsec = Integer.valueOf(contents[2]);
					txt.append("\n" + contents[2]);
					imumsg.header = head;
					imumsg.accel[0] = Double.valueOf(contents[3]);
					imumsg.accel[1] = Double.valueOf(contents[4]);
					imumsg.accel[2] = Double.valueOf(contents[5]);
					imumsg.angular_rate[0] = Double.valueOf(contents[6]);
					imumsg.angular_rate[1] = Double.valueOf(contents[7]);
					imumsg.angular_rate[2] = Double.valueOf(contents[8]);
					if(chanin.getText() != "")
					{
						event.channel = chanin.getText();
					}
					else
						event.channel = raw;
					System.out.print(event.channel + "\n");
					nanotime_start = System.nanoTime();
					utime_start = System.currentTimeMillis() * 1000;
					event.utime = utime_start + (System.nanoTime() - nanotime_start) / 1000;
				//	event.utime = baromsg.header.sec * 1000000000;
					event.eventNumber = num;
					num++;
//										
					for (int i = 0; i < 50; i++)
						logout.write(event.utime, event.channel, imumsg);
					logout.flush();
				//	logout.close();
//					lcm.publish(event.channel, baromsg);

				}
				if(in.contains("Pva"))
				{
					//UTF-8 channel name
					String raw = "Pva";
					byte[] bytes = raw.getBytes("ISO-8859-1");
					
					String chanutf = new String(bytes, "UTF-8");
					txt.append("In Pva\n");
					LcmGui.Pva pvamsg = new LcmGui.Pva();
					LcmGui.Header head = new LcmGui.Header();
					head.sec = Long.parseLong(contents[1]);
					txt.append("\n" + contents[1]);
					head.nsec = Integer.valueOf(contents[2]);
					txt.append("\n" + contents[2]);
					pvamsg.header = head;
					pvamsg.latitude = Double.valueOf(contents[3]);
					txt.append("\n" + contents[3]);
					pvamsg.longitude = Double.valueOf(contents[4]);
					txt.append("\n" + contents[4]);
					pvamsg.altitude_m = Double.valueOf(contents[5]);
					txt.append("\n" + contents[5]);
					pvamsg.velocity[0] = Double.valueOf(contents[6]);
					pvamsg.velocity[1] = Double.valueOf(contents[7]);
					pvamsg.velocity[2] = Double.valueOf(contents[8]);
					pvamsg.attitude = Double.valueOf(contents[9]);
					if(chanin.getText() != "")
					{
						event.channel = chanin.getText();
					}
					else
						event.channel = raw;
					System.out.print(event.channel + "\n");
					nanotime_start = System.nanoTime();
					
					utime_start = System.currentTimeMillis() * 1000;
					
					event.utime = utime_start + (System.nanoTime() - nanotime_start) / 1000;
//					event.utime = pvamsg.header.sec * 1000000000;
					
					
					event.eventNumber = num;
					num++;
//										
					for (int i = 0; i < 50; i++)
						logout.write(event.utime, event.channel, pvamsg);
					logout.flush();
				//	logout.close();
//					lcm.publish(event.channel, baromsg);

				}
			}
		}
		catch(IOException e)
				{
					e.printStackTrace();
				}
		
		System.out.println("JOBS DONE");

		//looper = true;
		//frame.remove(subbtn);
		//subbtn.setText("Restart Program?");
		//subbtn.addActionListener(new ActionListener(){
		//	@Override
		//	public void actionPerformed(ActionEvent e)
		//	{
		//			RepeatThisNoUserInput();				
		//	}
		//});

		if (filecnt + 1 < allfiles.size())
		{
			System.out.println("Cycling to the next CSV: " + allfiles.get(filecnt + 1).getName());
		}
		if (finalpass == false)
			NextCSV();
		else
			endthis();
		//subbtn.setText("Cycle to Next CSV?");
		//subbtn.addActionListener(new ActionListener(){
		//	@Override
		//	public void actionPerformed(ActionEvent e)
		//	{
		//			NextCSV();		
		//	}
		//});
		//frame.add(subbtn);
		//RepeatThis(looper);;
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

	public void RepeatThis(boolean again)
	{
		csvtolcm starter = new csvtolcm();
		//System.out.println("STARTING PROGRAM");
		String userinput = "";
		if (again == true)
		{
			//System.out.println("STARTING PROGRAM");
			Scanner in = new Scanner(System.in);
			System.out.println("Would you like to input another csv file?\nInput anything to continue: x or X to exit.");
			userinput = in.nextLine();
			for (int i = 0; i < userinput.length(); i++)
				System.out.println("USER INPUT: " + userinput.charAt(i));
			if (userinput.contains("x") || userinput.contains("X") && userinput.length() < 2)
			{
				endthis();
			}
			else
			{				
				frame.dispose();
				frame.removeAll();
				starter = new csvtolcm();
				ResetGlobals();
				System.out.println("STARTING PROGRAM");
				starter.CreateGui();
			}
		}
	}
	public void RepeatThisNoUserInput()
	{
		csvtolcm starter = new csvtolcm();
		//System.out.println("STARTING PROGRAM");
		String userinput = "";
			
		frame.dispose();
		frame.removeAll();
		ResetGlobals();
		starter = new csvtolcm();
		System.out.println("STARTING PROGRAM");
		starter.CreateGui();
	}
	public void NextCSV()
	{
		System.out.println(allfiles.size());
		if (filecnt < allfiles.size())
		{
			System.out.println("Size of file array: " + allfiles.size());
			filecnt++;
			Update();
		}
		else
		{
			System.out.println("All files have been stored.");
		}

		//Filein = "";
		//frame = new JFrame("CSV To LCM");
		//CreateGui();
		
		System.out.println("Moving to next csv. Loop num: " + (filecnt -1));

	}
	public void endthis()
	{
		//frame.dispose();
		//frame.removeAll();
		System.exit(0);

	}
	
	
	public void ResetGlobals()
	{
		Filein = "";
		frame = new JFrame("CSV To LCM");
		chanin = new JTextArea();
		fileOutName = new JTextArea();
		LCM lcm;
		Log logout;
		aspnbtn = new JButton();
		subbtn = new JButton("Submit");
		//looper = false;
	}
}
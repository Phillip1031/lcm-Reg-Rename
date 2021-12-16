import java.io.*;
import java.awt.event.*;
import java.util.Scanner;
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

public class csvtolcm
{
	//GUI + IO
	public static String Filein;
	public JFrame frame = new JFrame("CSV To LCM");
	public JTextArea txt = new JTextArea();
	public JTextArea chanin = new JTextArea();
	public static LCM lcm;
	public static Log logout;

	public static void main(String[] args) throws Exception
	{
		csvtolcm starter = new csvtolcm();
		starter.CreateGui();
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
		File file = new File("testlog");
		file.delete();
		String line = "";
		String splitchar = ",";
		long nanotime_start;
		long utime_start;
		int num = 0;
		int linecnt = 0;
		lcm = LCM.getSingleton();
		try
		{
		//	System.out.print(Filein);
			logout = new Log("testlog", "rw"); 
			BufferedReader br = new BufferedReader(new FileReader(Filein));
			while ((line = br.readLine()) != null)
			{
				String[] contents = line.split(splitchar);
				Log.Event event = new Log.Event();
				String in = contents[0];
			//	if(chanin.getText() != "")
			//	{
			//		event.channel = chanin.getText();
			//	}
				if (in.contains("Baro"))
				{
					//UTF-8 channel name
					String raw = "Baro";
					byte[] bytes = raw.getBytes("ISO-8859-1");
					
					String chanutf = new String(bytes, "UTF-8");
					txt.append("In Baro\n");
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
				
					event.utime = baromsg.header.sec * 1000000000 + (baromsg.header.nsec / 1000);

		//			event.utime = 0;
					event.eventNumber = num;
					num++;
					//GEN NEW LOG FILE. DELTA T vary time within acceptable range
					//CHRONOLOGICAL										
					for (int i = 0; i < 5; i++)
						logout.write(event.utime, event.channel, baromsg);
					logout.flush();
				//	logout.close();
//					lcm.publish(event.channel, baromsg);
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
	public void CreateGui()
	{
		frame = new JFrame("CSV to LCM Exportation");
		frame.setPreferredSize(new Dimension(1000,1000));
		frame.setVisible(true);
		GridLayout Lout = new GridLayout(3,3);
		frame.setLayout(Lout);
		JButton filebutton = new JButton("Choose CSV File");
		frame.add(new JLabel("Select CSV File To Change To LCMLog"));
		frame.add(filebutton);
		frame.add(new JLabel("<html>To change the event's channels enter new channel here.<br>Otherwise leave blank.</html>"));
		frame.add(chanin);
		frame.add(new JLabel("Click Submit If You Are Ready To Submit."));
		filebutton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				FileChoice();
			}
		});
		JButton subbtn = new JButton("Submit");
		frame.add(subbtn);
		subbtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(Filein != null && Filein != "")
					WriteLog();	
				
			}
		});

		changeFont(frame, new Font("Verdana", Font.PLAIN, 20));
		changeFont(chanin, new Font("Verdana", Font.PLAIN, 40));
	//	frame.add(chanin);
//	frame.add(txt);
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
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

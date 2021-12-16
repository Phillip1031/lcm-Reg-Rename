import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.io.*;
import java.nio.*;
import java.nio.file.*;
import lcm.lcm.*;
import java.lang.*;
import java.util.*;
import lcm.util.*;
import lcm.*;
public class FailedVentureHardCodedLCMLoggingMar31
{
	
	final static boolean shouldFill = true;
	final static boolean shouldWeightX = true;
	final static boolean RIGHT_TO_LEFT = false;
	private static String[] Sensors = new String[10];
	private static String timewindow = "";
	public static JFrame frame = new JFrame("Mavan Data Extration");
	public static JFrame frameFile = new JFrame("File Exportation");
	public static LcmGui.Baro[] BaroData = new LcmGui.Baro[1000];
	public static LcmGui.Baro[] BaroDataFiltered = new LcmGui.Baro[1000];
	public static LcmGui.Gps[] GpsData = new LcmGui.Gps[1000];
	public static LcmGui.Gps[] GpsDataFiltered = new LcmGui.Gps[1000];
	public static LcmGui.Header[] HeaderData = new LcmGui.Header[1000];
	public static LcmGui.Imu[] ImuData = new LcmGui.Imu[1000];
	public static LcmGui.Imu[] ImuDataFiltered = new LcmGui.Imu[1000];
	public static LcmGui.Pva[] PvaData = new LcmGui.Pva[1000];
	public static LcmGui.Pva[] PvaDataFiltered = new LcmGui.Pva[1000];
	public static int barcnt,gpscnt,headercnt,imucnt,pvacnt = 0;
	public static double timeinlow,timeinhigh = 0.0;
	public static JTextArea txttest = new JTextArea();
	public static String[][][] OverallData = new String[4][1000][7];
	

	//log read / write
		
	public static BufferedRandomAccessFile raf;
	public static BufferedRandomAccessFile rafin;
	//LCMSyncWord Magic
	static final int LOG_MAGIC = 0xEDA1DA01;
	//static final int LOG_MAGIC = 65;
	public static String path = "Test.log";
	public static long numMessagesWritten = 0;
	
	public static class LCMEvent
	{
		public long utime;
		public long eventNumber;
		public byte Data[];
		public String channel;

	}
	public static void TestData()
	{
		
		//Test
		LcmGui.Gps gpss = new LcmGui.Gps();
		LcmGui.Baro bars = new LcmGui.Baro();
		LcmGui.Baro barstwo = new LcmGui.Baro();
		LcmGui.Imu imus = new LcmGui.Imu();
		LcmGui.Pva pvas = new LcmGui.Pva();
		barstwo.altitude_m = 9999.99;
		LcmGui.Header headfail = new LcmGui.Header();
		double[] test = {10.01, 20.02, 30.03};
		headfail.sec = 0;
		headfail.nsec =0;
		barstwo.header = headfail;
		bars.altitude_m = 696969.420;
		LcmGui.Header headspace = new LcmGui.Header();
		headspace.sec = 3600;
		headspace.nsec = 900;
		bars.header = headspace;
		BaroIncomingData(bars);
		BaroIncomingData(barstwo);
		imus.header = headspace;
		imus.accel = test;
		imus.angular_rate = test;
		pvas.header = headfail;
		pvas.latitude = 34;
		pvas.longitude = 56;
		pvas.altitude_m = 69.420;
		pvas.velocity = test;
		pvas.attitude = 9;
		ImuIncomingData(imus);
		PvaIncomingData(pvas);
		gpss.header = headspace;
		gpss.latitude_deg = 11;
		gpss.longitude_deg = 22;
		gpss.altitude_m = 42.69;
		GpsIncomingData(gpss);
		//Create Log File
		//File Name: Test.log
		try{
			raf = new BufferedRandomAccessFile("Test.log", "rw");

			//Log("Test.log", "rw");
			LCMEvent teste = new LCMEvent();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			DataOutputStream doubleout = new DataOutputStream(bos);
			teste.utime = 123456;
			teste.eventNumber = 0;
			teste.channel = "Baro";
			//doubleout.writeLong(123456789);
			doubleout.writeDouble(123.456);
			doubleout.flush();
			bos.flush();
			teste.Data = bos.toByteArray();
			//this works. Can pass the data to the Data and get it back by converting the byte[] to doulbe
			txttest.append("\nData test from Test()" + ByteBuffer.wrap(teste.Data).getDouble());
			
			write(teste);
			//flush();
		}catch (IOException e) {
		}
		LCMIncoming();
		
	}
	public static void main(String[] args)
	{
		createGUI();
		TestData();
		//LCM lcm = LCM.getSingleton();
	/*	javax.swing.SwingUtilities.invokeLater(new Runnable() 
				{
					public void run()
					{
						createGUI();
			
					}
				});*/
	}
	public static void Log(String Fpath, String mode) 
	{
		path = Fpath;
		try{
			raf = new BufferedRandomAccessFile(Fpath, mode);
			rafin = new BufferedRandomAccessFile("Test.log", "r");
		}catch(IOException e){
		}
	}
	public static String getPath()
	{
		return path;
	}
	public static void flush() throws IOException
	{
		raf.flush();
	}
	public static synchronized LCMEvent readNext() throws IOException
	{
		int magic = 0;
		LCMEvent e = new LCMEvent();
		int channellen =0; 
		int datalen = 0;
		
		while(true)
		{
			int v = rafin.readByte()&0xff;
			magic = (magic<<8) | v;
			txttest.append("\n" + magic + " ~= " + LOG_MAGIC);
			if (magic != LOG_MAGIC)
			{
				txttest.append("\nKicking out b/c != LOGMAGIC");
				continue;
			}
			txttest.append("\nStarting readNext() Outputs:\n");
			e.eventNumber = rafin.readLong() - 1;
			txttest.append("\nEvent Num:\t" + e.eventNumber);
			e.utime = rafin.readLong();
			txttest.append("\nutime:\t"+e.utime);
			channellen = rafin.readInt();
			txttest.append("\nchanlen:\t"+channellen);
			datalen = rafin.readInt();
			txttest.append("\ndatalen:\t"+datalen);

			if (channellen <= 0 || datalen <= 0 || channellen >= 256 || datalen >= 2147483647)
			{
				txttest.append("\nBad log event");
				continue;
			}
			break;
		}
		byte bchannel[] = new byte[channellen];
		e.Data = new byte[datalen];
		rafin.readFully(bchannel);
		e.channel = new String(bchannel);
		txttest.append("\nChannel:\t" + e.channel);
		rafin.readFully(e.Data);
		txttest.append("\nData:\t" + ByteBuffer.wrap(e.Data).getDouble() + "\n");
		return e;
	}
	public static void write(long utime, String channel, LCMEncodable msg) throws java.io.IOException
	{
		LCMEvent le = new LCMEvent();
		le.utime = utime;
		le.channel = channel;
		LCMDataOutputStream outs = new LCMDataOutputStream();
		msg.encode(outs);
		le.Data = outs.toByteArray();
		le.eventNumber = numMessagesWritten;
		numMessagesWritten++;
		write(le);

	}
	public static synchronized void write(LCMEvent e) throws IOException
	{
		byte[] channelb = e.channel.getBytes();
		txttest.append("\nWriting from write(event)");

		raf.writeInt(LOG_MAGIC);
		txttest.append("\n" + LOG_MAGIC);
		raf.writeLong(0);
		txttest.append("\n" + e.eventNumber);
		raf.writeLong(e.utime);
		txttest.append("\n" + e.utime);
		raf.writeInt(channelb.length);
		txttest.append("\n" + channelb.length);
		raf.writeInt(e.Data.length);
		txttest.append("\n" + e.Data.length);

		raf.write(channelb, 0, channelb.length);
		String s = new String(channelb);
		txttest.append("\n" + s);
		raf.write(e.Data, 0, e.Data.length);
		txttest.append("\n" + ByteBuffer.wrap(e.Data).getDouble() + "\n");
	}
	public static synchronized void close() throws IOException { raf.close(); }
	public static void LCMIncoming()
	{
		txttest.append(txttest.getText() + "Into LCMIncoming\n");
		Log("Test.log", "rw");
		LCMEvent event = new LCMEvent();
		while(true)
		{
			try{
				event = readNext();
				txttest.append("\nReading event in log\n");
				txttest.append("\nChecking Log for:\t" + event.channel + "\n");		
			
			
				if (event.channel.contains("Baro"))
				{
					txttest.append("\nBaro found from log file!! wahooooo yea yea you go Phil\n");
					//Assume Baro data is altitude_m
					//Currently Data is wrong
					LcmGui.Baro baromsg = new LcmGui.Baro(event.Data);
					LcmGui.Header headermsg = new LcmGui.Header();

					headermsg.sec = event.utime;
					baromsg.header = headermsg;
					txttest.append(txttest.getText() + "Baro found from log file!! wahooooo yea yea you go Phil\n");
					BaroIncomingData(baromsg);
	
				}
				if (event.channel == "Gps")
				{
					LcmGui.Gps gpsmsg = new LcmGui.Gps(event.Data);
					LcmGui.Header headermsg = new LcmGui.Header();
					headermsg.sec = event.utime;
					gpsmsg.header = headermsg;
					GpsIncomingData(gpsmsg);
				}
				if(event.channel == "Imu")
				{
					LcmGui.Imu imumsg = new LcmGui.Imu(event.Data);
					LcmGui.Header headermsg = new LcmGui.Header();
					headermsg.sec = event.utime;
					imumsg.header = headermsg;
					ImuIncomingData(imumsg);
				}
				if(event.channel == "Pva")
				{
					LcmGui.Pva pvamsg = new LcmGui.Pva(event.Data);
					LcmGui.Header headermsg = new LcmGui.Header();
					headermsg.sec = event.utime;
					PvaIncomingData(pvamsg);
				}
			}catch (IOException e){

			}
		}
			/*try
			{
				FileInputStream fstream = new FileInputStream("Test.log");
				BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
				String strLine;
				//Read log line by line
				while((strLine = br.readLine()) != null)
				{
					//strLine will be line taken from log
					//
					txttest.setText(txttest.getText() + "\n" + strLine + "\n");
					
				}
				fstream.close();
			} catch (Exception e){
				System.err.println("Error: " + e.getMessage());
			}
		}
	/*	try
		{	
			
			if (channel == "Baro")
			{
				LcmGui.Baro Baromsg = new LcmGui.Baro();
				//Baromsg.header.sec = msg.timestamp;
				//Baromsg.header.nsec = msg.nsec;
				//Baromsg.altitude_m = msg.altitude_m;
				BaroIncomingData(Baromsg);
			}
			if (channel == "Gps")
			{
				LcmGui.Gps Gpsmsg = new LcmGui.Gps();
				GpsIncomingData(Gpsmsg);
			}
			if (channel == "Imu")
			{
				LcmGui.Imu Imumsg = new LcmGui.Imu();
				ImuIncomingData(Imumsg);
			}
			if (channel == "Pva")
			{
				LcmGui.Pva Pvamsg = new LcmGui.Pva();
				PvaIncomingData(Pvamsg);
			}
		}catch (Exception e){
			
		}*/	
	
	}
	public static void BaroIncomingData(LcmGui.Baro bar)
	{		
		BaroData[barcnt] = bar;
		barcnt++;
	}
	public static void GpsIncomingData(LcmGui.Gps gps)
	{
		GpsData[gpscnt] = gps;
		gpscnt++;
	}
	public static void ImuIncomingData(LcmGui.Imu imu)
	{
		ImuData[imucnt] = imu;
		imucnt++;
	}
	public static void PvaIncomingData(LcmGui.Pva pva)
	{
		PvaData[pvacnt] = pva;
		pvacnt++;
	}
	public static void addComponents(Container pane, int counter)
	{
		JButton button;
		GridBagConstraints c = new GridBagConstraints();
		JPanel btnwindow = new JPanel();
		JPanel labelwindow = new JPanel();
		if (counter == 0)
		{
		
			JButton submitbtn = new JButton("Submit");
			Font f = new Font("Verdana", Font.PLAIN, 18);
			GridLayout Lout = new GridLayout(3,2);
			pane.setLayout(Lout);
			JLabel sensorpromt = new JLabel("<html>Please select from the follow sensors:<br>Data will only be shown from selected sensors.</html>");
			sensorpromt.setFont(new Font("Verdana", Font.PLAIN, 18));

			pane.add(sensorpromt, BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("Baro"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("GPS"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("Imu"),BorderLayout.CENTER);
		//	btnwindow.add(new JCheckBox("mag"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("Pva"),BorderLayout.CENTER);
		//	btnwindow.add(new JCheckBox("cam1"),BorderLayout.CENTER);
		//	btnwindow.add(new JCheckBox("cam2"),BorderLayout.CENTER);
		//	btnwindow.add(new JCheckBox("All Data"),BorderLayout.CENTER);

			changeFont(btnwindow, f);
				
			pane.add(btnwindow, BorderLayout.CENTER);
			//pane.add(labelwindow, BorderLayout.CENTER);
			
			JLabel timepromt = new JLabel("<html>Enter the time after takeoff you would like data from<br>Format: mm.ss-mm.ss</html>");
			timepromt.setFont(new Font("Verdana", Font.PLAIN, 18));
			pane.add(timepromt);
		//TEST
		//JFormattedTextField txtin = new JFormattedTextField("Enter Time Range Here: mm.ss-mm.ss");
			JFormattedTextField txtin = new JFormattedTextField("00.00-9999.00");
			pane.add(txtin, BorderLayout.CENTER);
			pane.add(new JLabel(" "));
			pane.add(submitbtn, BorderLayout.CENTER);
			if(RIGHT_TO_LEFT)
			{
				pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			}
			if(shouldFill)
			{
				c.fill = GridBagConstraints.HORIZONTAL;
			}
			changeFont(pane, f);
			submitbtn.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e)
						{
							//Send info to other form essentially
							String[] names = new String[10];
							Component[] comps = btnwindow.getComponents();
							int cycle = 0;
							//See which checkboxes are checked
							for (Component compo : comps)
							{

								if(compo instanceof JCheckBox) {
									//This causes program to fall down and go boom
									JCheckBox holder = (JCheckBox)compo;
									if (holder.isSelected())
									{
										String textname = holder.getText();
							//			txtin.setText(txtin.getText() + textname + '\n');
										names[cycle] = textname;
										cycle = cycle + 1;
									}
								}
							}
							//txtin.setText("Subbtn Clicked\n");
							String in = txtin.getText();
							String[] inputstr = in.split("-", 2);
							timeinlow = Double.parseDouble(inputstr[0]);	
							timeinhigh = Double.parseDouble(inputstr[1]);
							//close form 1
							
							frame.setVisible(false);
							SendFormData(names, in);
							ShowFormTwo();
							FillData(txttest);

						}
					});
			
		}
		else
		{
			//File Export Frame
			txttest.setSize(800,1800);
			txttest.setLineWrap(true);
			txttest.setEditable(false);
			txttest.setVisible(true);
			txttest.setRows(50);
			JScrollPane scroll = new JScrollPane (txttest);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			
			pane.add(scroll);
			FlowLayout flow = new FlowLayout();
			pane.setLayout(flow);
			button = new JButton("Click to Download All Data as CSV");
			pane.add(button);
			//Csv click button event
			button.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
							txttest.append("Exporting All Data to CSV\n");
							try{
								filemake();	
							}catch (IOException a)
							{

							}
						}
					});
			button = new JButton("Click to Download Data Within Time Window as CSV");
			pane.add(button);
			button.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
							txttest.append("Exporting Time Filtered Data to CSV\n");
							try{
								filemakefiltered();	
							}catch (IOException a)
							{

							}
						}
					});
		}
	}
	public static void FillData(JTextArea txtbox)
	{
		//check to make sure data is within window timeinlow timeinhigh
		//check which sensors are active
		//display data from arrays holding types
		//
		//
		int filtercnt = 0;
		String holder = "";
		for (String part : Sensors)
		{
			if (part == "Baro")
			{
				for (int i =0; i < barcnt; i++)
				{
					//holder = holder + "Looping Baro\n";
					if (CompareTime(BaroData[i].header.sec) == true)
					{
						holder = holder + "Baro-Sensor:\t" + BaroData[i].header.sec + " s,";
						holder = holder + "" + BaroData[i].header.nsec + " ns, ";
						holder = holder + "" + BaroData[i].altitude_m + " m";
						holder = holder + "\n";
						BaroDataFiltered[filtercnt] = BaroData[i];
						filtercnt++;
					}	
				}
			}
			if (part == "Gps")
			{
				filtercnt = 0;
				for (int i = 0; i < gpscnt; i++)
				{
					if(CompareTime(GpsData[i].header.sec) == true)
					{
						holder = holder + "Gps-Sensor:\t" + GpsData[i].header.sec + " s, ";
						holder = holder + "" + GpsData[i].header.nsec + " ns,";
						holder = holder + "" + GpsData[i].latitude_deg + " degrees,";
						holder = holder + "" + GpsData[i].longitude_deg + " degrees, ";
						holder = holder + "" + GpsData[i].altitude_m + " m";
						holder = holder + "\n";
						GpsDataFiltered[filtercnt] = GpsData[i];
						filtercnt++;
					}
				}
			}	
			if (part == "Imu")
			{
				filtercnt = 0;
				
				for (int i = 0; i < imucnt; i++)
				{
					if(CompareTime(ImuData[i].header.sec) == true)
					{
						holder = holder + "Imu-Sensor:\t" + ImuData[i].header.sec + " s, ";
						holder = holder + "" + ImuData[i].header.nsec + " ns, ";
						holder = holder + "{ " + ImuData[i].accel[0] + " , " + ImuData[i].accel[1] + " , " + ImuData[i].accel[2] + " } m/(s*s), ";
						holder = holder + "{ " + ImuData[i].angular_rate[0] + " , " + ImuData[i].angular_rate[1] + " , " + ImuData[i].angular_rate[2] + "} rad/hr";
						holder = holder + "\n";
						ImuDataFiltered[filtercnt] = ImuData[i];
						filtercnt++;
					}
				}
			}
			if (part == "Pva")
			{
				filtercnt = 0;
				for (int i = 0; i < pvacnt; i++)
				{
					//holder = holder + "Looping Pva\n";
					if(CompareTime(PvaData[i].header.sec) == true)
					{
						holder = holder + "Pva-Sensor:\t" + PvaData[i].header.sec + " s, ";
						holder = holder + "" + PvaData[i].header.nsec + " ns, ";
						holder = holder + "" + PvaData[i].latitude + " degrees, ";
						holder = holder + "" + PvaData[i].longitude + " degrees, ";
						holder = holder + "" + PvaData[i].altitude_m + " m, ";
						holder = holder + "{ " + PvaData[i].velocity[0] + " , " + PvaData[i].velocity[1] + " , " + PvaData[i].velocity[2] + " } m/(s*s)";
						holder = holder + "" + PvaData[i].attitude;
						holder = holder + "\n";
						PvaDataFiltered[filtercnt] = PvaData[i];
						filtercnt++;
					}
				}
			}
		
		}
		txtbox.setText(txtbox.getText() + holder);

	}
	public static void SendFormData(String[] in, String time)
	{
		Sensors = in;	
		timewindow = time;
	}
	public static void ShowFormTwo()
	{
		frameFile.setVisible(true);
	}
	public static boolean CompareTime(long timestamp)
	{
		//mm.ss is the input format
		if (timeinlow*60 <= timestamp && timeinhigh*60 >= timestamp)
			return true;
		else
			return false;
	}

/*	public String escapeChars(String data)
	{
		String escapedData = data.replaceAll("\\R"," ");
		if (data.contains(",") || data.contains("\"") || data.contains("'"))
		{
			data = data.replace("\"","\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}
	public String convertToCSV(String[] data)
	{
		return Stream.of(data).map(this::escapeChars).collect(Collectors.joining(","));
		//return "test";
	}*/
	public static void filemake() throws IOException
	{
		PrintWriter pw = null;
		StringBuilder builder = new StringBuilder();
		String columns = "";
		//baro = time, alt
		//Gps = time, lat, long, alt
		//IMU = time, accel[], angular_rate[]
		//PVA = time, lat, long, alt, velocity[], attitude
//				BARO SENSOR FILE MAKE
		builder = new StringBuilder();
		File BaroFile = new File("Baro-sensor.csv");
		try
		{
			pw = new PrintWriter(BaroFile);
			}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		columns = "Sensor_Name, Time(s), Time(ns), Altitude(m)";
		builder.append(columns + "\n");
		for(int i =0; i < 1000; i++)
		{
			try {builder.append("Baro-Sensor" + "," + BaroData[i].header.sec + "," + BaroData[i].header.nsec + "," + BaroData[i].altitude_m + "\n");
			}catch (NullPointerException p){
			}
		}
		pw.write(builder.toString());
		pw.close();
//			}
//				GPS SENSOR FILE MAKE
		builder = new StringBuilder();
		File GpsFile = new File("Gps-sensor.csv");
		try
		{
			pw = new PrintWriter(GpsFile);
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
		
		columns = "Sensor_Name, Time(s), Time(ns), Latitude(deg), Longitude(deg), Altitude(m)";
		builder.append(columns + "\n");
		for (int i = 0; i < 1000; i++)
		{
			try{builder.append("Gps-Sensor" + "," + GpsData[i].header.sec + "," + GpsData[i].header.nsec + "," + GpsData[i].latitude_deg + "," + GpsData[i].longitude_deg + "," + GpsData[i].altitude_m + "\n");
			}catch(NullPointerException m){
			}
		}
		pw.write(builder.toString());
		pw.close();
		//IMU SENSOR FILE MAKE
		builder = new StringBuilder();
		File ImuFile = new File("Imu-sensor.csv");
		try
		{
			pw = new PrintWriter(ImuFile);
			}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		columns = "Sensor_Name, Time(s),Time(ns),Acceleration_x(m/(s*s)),Acceleration_y(m/(s*s)),Acceleration_z(m/(s*s)),Angular_Rate_x(rad/h),Angular_Rate_y(rad/h),Angular_Rate_z(rad/h)";
		builder.append(columns + "\n");
		for (int i = 0; i < 1000; i++)
		{
			try{builder.append("Imu-Sensor" + "," + ImuData[i].header.sec + "," + ImuData[i].header.nsec + "," + ImuData[i].accel[0] + "," + ImuData[i].accel[1] + "," + ImuData[i].accel[2] + "," + ImuData[i].angular_rate[0] + "," + ImuData[i].angular_rate[1] + "," + ImuData[i].angular_rate[2] + "\n");
			}catch(NullPointerException m){
			}
		}
		pw.write(builder.toString());
		pw.close();
		//PVA SENSOR FILE MAKE	
		builder = new StringBuilder();	
		File PvaFile = new File("Pva-sensor.csv");
		try
		{
			pw = new PrintWriter(PvaFile);
			}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		columns = "Sensor_Name, Time(s), Time(ns), Latitude(deg), Longitude(deg), Altitude(m), Velocity_x(m/(s*s)),Velocity_y(m/(s*s)),Velocity_z(m/(s*s)), Attitude";
		builder.append(columns + "\n");
		for (int i = 0; i < 1000; i++)
		{
			try{builder.append("Pva-Sensor" + "," + PvaData[i].header.sec + "," + PvaData[i].header.nsec + "," + PvaData[i].latitude + "," + PvaData[i].longitude + "," + PvaData[i].altitude_m + "," + PvaData[i].velocity[0] +"," + PvaData[i].velocity[1] + "," + PvaData[i].velocity[2] + "," + PvaData[i].attitude + "\n");
			}catch(NullPointerException m){
			}
		}
		pw.write(builder.toString());
		pw.close();

	}

	public static void filemakefiltered() throws IOException
	{
		PrintWriter pw = null;
		StringBuilder builder = new StringBuilder();
		String columns = "";
		//baro = time, alt
		//Gps = time, lat, long, alt
		//IMU = time, accel[], angular_rate[]
		//PVA = time, lat, long, alt, velocity[], attitude
//				BARO SENSOR FILE MAKE
		builder = new StringBuilder();
		File BaroFile = new File("Baro-sensor.csv");
		try
		{
			pw = new PrintWriter(BaroFile);
			}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		columns = "Sensor_Name, Time(s), Time(ns), Altitude(m)";
		builder.append(columns + "\n");
		for(int i =0; i < 1000; i++)
		{
			try {builder.append("Baro-Sensor" + "," + BaroDataFiltered[i].header.sec + "," + BaroDataFiltered[i].header.nsec + "," + BaroDataFiltered[i].altitude_m + "\n");
			}catch (NullPointerException p){
			}
		}
		pw.write(builder.toString());
		pw.close();
//			}
//				GPS SENSOR FILE MAKE
		builder = new StringBuilder();
		File GpsFile = new File("Gps-sensor.csv");
		try
		{
			pw = new PrintWriter(GpsFile);
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
		
		columns = "Sensor_Name, Time(s), Time(ns), Latitude(deg), Longitude(deg), Altitude(m)";
		builder.append(columns + "\n");
		for (int i = 0; i < 1000; i++)
		{
			try{builder.append("Gps-Sensor" + "," + GpsDataFiltered[i].header.sec + "," + GpsDataFiltered[i].header.nsec + "," + GpsDataFiltered[i].latitude_deg + "," + GpsDataFiltered[i].longitude_deg + "," + GpsDataFiltered[i].altitude_m + "\n");
			}catch(NullPointerException m){
			}
		}
		pw.write(builder.toString());
		pw.close();
		//IMU SENSOR FILE MAKE
		builder = new StringBuilder();
		File ImuFile = new File("Imu-sensor.csv");
		try
		{
			pw = new PrintWriter(ImuFile);
			}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		columns = "Sensor_Name, Time(s),Time(ns),Acceleration_x(m/(s*s)),Acceleration_y(m/(s*s)),Acceleration_z(m/(s*s)),Angular_Rate_x(rad/h),Angular_Rate_y(rad/h),Angular_Rate_z(rad/h)";
		builder.append(columns + "\n");
		for (int i = 0; i < 1000; i++)
		{
			try{builder.append("Imu-Sensor" + "," + ImuDataFiltered[i].header.sec + "," + ImuDataFiltered[i].header.nsec + "," + ImuDataFiltered[i].accel[0] + "," + ImuDataFiltered[i].accel[1] + "," + ImuDataFiltered[i].accel[2] + "," + ImuDataFiltered[i].angular_rate[0] + "," + ImuDataFiltered[i].angular_rate[1] + "," + ImuDataFiltered[i].angular_rate[2] + "\n");
			}catch(NullPointerException m){
			}
		}
		pw.write(builder.toString());
		pw.close();
		//PVA SENSOR FILE MAKE	
		builder = new StringBuilder();	
		File PvaFile = new File("Pva-sensor.csv");
		try
		{
			pw = new PrintWriter(PvaFile);
			}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		columns = "Sensor_Name, Time(s), Time(ns), Latitude(deg), Longitude(deg), Altitude(m), Velocity_x(m/(s*s)),Velocity_y(m/(s*s)),Velocity_z(m/(s*s)), Attitude";
		builder.append(columns + "\n");
		for (int i = 0; i < 1000; i++)
		{
			try{builder.append("Pva-Sensor" + "," + PvaDataFiltered[i].header.sec + "," + PvaDataFiltered[i].header.nsec + "," + PvaDataFiltered[i].latitude + "," + PvaDataFiltered[i].longitude + "," + PvaDataFiltered[i].altitude_m + "," + PvaDataFiltered[i].velocity[0] + "," + PvaDataFiltered[i].velocity[1] + "," + PvaDataFiltered[i].velocity[2] + "," + PvaDataFiltered[i].attitude);
			}catch(NullPointerException m){
			}
		}
		pw.write(builder.toString());
		pw.close();

//		}
//		builder.append("123" + ",");
//		builder.append("Jimbo");
//		builder.append('\n');
		//pw.write(builder.toString());
		//pw.close();

	}
	
	private static void createGUI()
	{
		int counter =0;
		frame = new JFrame("Mavan Data Extraction");
		addComponents(frame.getContentPane(), counter);
		counter++;
		frame.setPreferredSize(new Dimension(1000,1000));
		frame.pack();
		frame.setVisible(true);
		
		
		frameFile = new JFrame("File exportation");
		frameFile.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponents(frameFile.getContentPane(), counter);
		frameFile.setPreferredSize(new Dimension(1000,1000));
		frameFile.pack();
		frameFile.setVisible(false);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	public static void changeFont( Component comp, Font font)
	{
		comp.setFont(font);
		if (comp instanceof Container)
		{
			for (Component child: (( Container) comp ).getComponents())
					{
						changeFont(child,font);
					}
		}
	}
}

import javax.swing.*;
import javax.swing.JFileChooser;
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
import lcm.logging.*;
import lcm.lcm.*;
import javax.swing.filechooser.*;
import java.math.*;
public class Test
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
	public static LcmGui.Imu[] ImuData = new LcmGui.Imu[1000];
	public static LcmGui.Imu[] ImuDataFiltered = new LcmGui.Imu[1000];
	public static LcmGui.Pva[] PvaData = new LcmGui.Pva[1000];
	public static LcmGui.Pva[] PvaDataFiltered = new LcmGui.Pva[1000];
	public static int barcnt,gpscnt,headercnt,imucnt,pvacnt = 0;
	public static double timeinlow,timeinhigh = 0.0;
	public static JTextArea txttest = new JTextArea();
//Log stuff
//	
	public static Log logout;
	public static Log login;
	public static LCM lcm;	

	//first message time to compare rest of time to
	public static double firsttime = 0;
	//Filename chosen from the file selector windo
	public static String Filein = "";
	
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
	}
	public static void main(String[] args)
	{
	
//		txttest.append("\nGUI Created.\n");
		//Program starts by building 2 forms, hiding one on start.
		createGUI();
		//Prompts User with a choice of a file.
	//	FileChoice();
//		txttest.append("\nWriting Log.\n");
//		txttest.append("\nBuilding Test Data.\n");
		//Builds initial test data for use of program without test file.
		TestData();
		//LogWrite();
//		LCMIncoming();
		
	}
	public static double convertByteArrayToDouble(byte[] doubleBytes)
	{
		ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
		byteBuffer.put(doubleBytes);
		byteBuffer.flip();	
		return byteBuffer.getDouble();
	}
	public static byte[] convertDoubleToByteArray(double number)
	{
		ByteBuffer byteBuffer = ByteBuffer.allocate(Double.BYTES);
		byteBuffer.putDouble(number);
		return byteBuffer.array();
	}
	public static byte[] convertDoubleArrayToByteArray(double[] data)
	{
		if (data == null) return null;
		byte[] byts = new byte[data.length * Double.BYTES];
		for (int i = 0; i < data.length; i++)
		{
			System.arraycopy(convertDoubleToByteArray(data[i]), 0, byts, i * Double.BYTES, Double.BYTES);
		}
		return byts;
	}
	public static void LogWrite()	
	{
			
		//do lcm-logger in bash on seprate terminal
		//use log file produced as input
		
			lcm = LCM.getSingleton();
	//		txttest.append("\nEnteded try in logwrite\n");
			lcm.publish("Baro",BaroData[0]);
			lcm.publish("Gps", GpsData[0]);
			lcm.publish("Imu", ImuData[0]);
			lcm.publish("Pva", PvaData[0]);

	}
	public static void ResetData()
	{
		BaroData = new LcmGui.Baro[]{};
		GpsData = new LcmGui.Gps[]{};
		ImuData = new LcmGui.Imu[]{};
		PvaData = new LcmGui.Pva[]{};
		barcnt = 0;
		gpscnt = 0;
		imucnt = 0;
		pvacnt = 0;
	}
	public static void LCMIncoming()
	{
		Log.Event event = new Log.Event();
		
		try
		{
			//Reads the user choice from the file selector
			//prevent empty file choice.
			if (Filein == "") FileChoice();
			Log login = new Log(Filein, "r");
			
			//txttest.append("Channel:\t" + event.channel + "\n");
			while(true) {
				try
				{
					event = new Log.Event();
					//parse events from log file
					event = login.readNext();
				//	txttest.append("\nReading event in log\n");
				//	txttest.append("\nChecking Log for:\t" + event.channel + "\n");		
					
					//Checks found event's channel for a known channel to pull data
					if (event.channel.contains("Baro"))
					{
						//txttest.append("\nBaro found from log file!! wahooooo yea yea you go Phil\n");
						//Decodes data and constructs new Baro obj to store.					
						LcmGui.Baro baromsg = new LcmGui.Baro(event.data);
			
					//	tai time
					//	use first time to compare to rest
					//	txttest.append("Baro Header timesec:\t" + baromsg.header.sec + "\nBaro Header Nsec:\t" + baromsg.header.nsec + "\n");
				//		txttest.append("\nBaro found from log file!! wahooooo yea yea you go Phil\n");
						BaroIncomingData(baromsg);
					}
					if (event.channel.contains("Gps"))
					{
						LcmGui.Gps gpsmsg = new LcmGui.Gps(event.data);
					//	txttest.append("\nGPS found from log file!!\n");
						GpsIncomingData(gpsmsg);
					}
					if(event.channel.contains("Imu"))
					{
						LcmGui.Imu imumsg = new LcmGui.Imu(event.data);
					//	txttest.append("\nImu Found from log file!!\n");
						ImuIncomingData(imumsg);
					}
					if(event.channel.contains("Pva"))
					{
						LcmGui.Pva pvamsg = new LcmGui.Pva(event.data);
					//	txttest.append("\nPVA Found from log file!!\n");
						PvaIncomingData(pvamsg);
					}
				}catch (EOFException e){
					break;
				}
			}
		}catch(IOException a){}
			
	//	txttest.append("Into LCMIncoming\n");
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
	public static void FileChoice()
	{
		//FILE SELECTION WINDOW
		//Sets global variable to the file path seleted for use later in finding log file
		JFileChooser f = new JFileChooser();
		f.setSize(400,400);
		String FPath = "";
		int result = f.showOpenDialog(frame);
		if (result == JFileChooser.APPROVE_OPTION)
		{
			File selectedFile = f.getSelectedFile();
			FPath = selectedFile.getPath();
		}
		Filein = FPath;
	}
	public static void addComponents(Container pane, int counter)
	{
		//Builds both starting frames. Can add more in the future
		//Use of counter is redundant can  be simplified in the future
		//adds buttons and panels to form.
		JButton button;
		GridBagConstraints c = new GridBagConstraints();
		JPanel btnwindow = new JPanel();
		JPanel labelwindow = new JPanel();
		if (counter == 0)
		{
		
			JButton submitbtn = new JButton("Submit");
			Font f = new Font("Verdana", Font.PLAIN, 18);
			//changing to from (3,2) to (4,2) to add a new label and give file explorer button own grid
			GridLayout Lout = new GridLayout(4,2);
			pane.setLayout(Lout);
			pane.add(new JLabel("<html>Please select a LCM log file<br>Data will be read from this file."), BorderLayout.CENTER);
			JButton filebutton = new JButton("File Explorer");
			pane.add(filebutton);
			JLabel sensorpromt = new JLabel("<html>Please select from the follow sensors:<br>Data will only be shown from selected sensors.</html>");
			sensorpromt.setFont(new Font("Verdana", Font.PLAIN, 18));
			pane.add(sensorpromt, BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("Baro"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("Gps"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("Imu"),BorderLayout.CENTER);
		//	btnwindow.add(new JCheckBox("mag"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("Pva"),BorderLayout.CENTER);
		//	btnwindow.add(new JCheckBox("cam1"),BorderLayout.CENTER);
		//	btnwindow.add(new JCheckBox("cam2"),BorderLayout.CENTER);
		//	btnwindow.add(new JCheckBox("All Data"),BorderLayout.CENTER);
			//btnwindow.add(filebutton);
			changeFont(btnwindow, f);
			filebutton.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e)
						{
							FileChoice();
						}
					});
				
			pane.add(btnwindow, BorderLayout.CENTER);
			//pane.add(labelwindow, BorderLayout.CENTER);
			
			JLabel timepromt = new JLabel("<html>Enter the time window(after takeoff) you would like to view data.<br>Format: mm.ss-mm.ss<br>Restriction:\tWhole units only.<br>Example:\t1 min and 20 sec after takeoff:<br>0.00-1.20</html>");
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
		//Adds click event to the submit button on form 1.
			submitbtn.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e)
						{
						//	LCMIncoming();
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
							//round values
							round(timeinlow, 2);
							round(timeinhigh, 2);
							timeinlow = ConvertTime(timeinlow);
							timeinhigh = ConvertTime(timeinhigh);
							//close form 1
							
							frame.setVisible(false);
							SendFormData(names, in);
							ShowFormTwo();
							LCMIncoming();
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
								LogWrite();
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
		//Fills the textbox with the sensor data
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
		//txtbox.setText(txtbox.getText() + holder);
		txtbox.append(holder);

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
	public static double round(double value, int places)
	{
		if (places < 0) throw new IllegalArgumentException();
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}
	public static double ConvertTime(double time)
	{
		//999.20
	//	time = round(time, 2);
		int a,b, c = 0;
		a = (int)time;
		double fractional = round(time - a, 2);
		String[] l = Double.toString(time).split("\\.");
		int intplace = l[0].length();
		int decplaces = l[1].length();
		if(decplaces <= 1) decplaces = decplaces + 1;
		b = (int)(fractional * Math.pow(10, decplaces));
		txttest.append("bVal: " + b + "\n");
		while(b > 60)
		{	
			a = a + 1;
			b = b - 60;
		}
		txttest.append("" + a + "," + b + "\n");
		if (a >= 1) a = a*60;
		
	//	b = b*1000000000;
		c = a + b;
		String together = "";
		//together = ("" + a + "." + b);
		together = ("" + c);
		double out = Double.parseDouble(together);
		txttest.append("\n" + out + "\n");
		return out;
	}
	public static boolean CompareTime(long timestamp)
	{
		//mm.ss is the input format
		//Checks to see if message header time is within the range of the time window set by the user.
	//	if (timeinhigh >= 1) timeinhigh = timeinhigh*60*1000000000;
	//	else
	//		timeinhigh = timeinhigh*1000000000;
	//	if(timeinlow >= 1) timeinlow = timeinlow*60*1000000000;
	//	else
	//		timeinlow = timeinlow*1000000000;
	//	round(timeinhigh, 2);
	//	round(timeinlow , 2);
	//	txttest.append("\n" + timeinhigh);	
	//	double comparehi = ConvertTime(timeinhigh);
	//	double comparelo = ConvertTime(timeinlow);
	//	timeinlow = ConvertTime(timeinlow);
	//Assumes that the first messages recieved are the earliest messages. Checks for lowest among all recieved message types.
		if (firsttime == 0)
		{
			double[] arr = new double[] {BaroData[0].header.sec, GpsData[0].header.sec, ImuData[0].header.sec, PvaData[0].header.sec};
			double min = 0;
			for(int i =0; i <arr.length; i++) {
				if (arr[i] < min)
				{
					min = arr[i];
				}
			}
			firsttime = min;
		}
		//Checks the time window
		if((timestamp - firsttime)/1000000000.00 <= (timeinhigh) && ((timestamp - firsttime)/1000000000.00 >= (timeinlow)))
		{
			return true;
			
		}
		else
			return false;
		//
	//testing use of comparison
	//if (timeinlow*60 <= timestamp && timeinhigh*60 >= timestamp)
	//		return true;
	//	else
	//		return false;
	}

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

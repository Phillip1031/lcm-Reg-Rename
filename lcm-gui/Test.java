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
//UI
import javax.swing.filechooser.*;
import javax.swing.border.*;
import java.math.*;
import java.util.List;
//images
import javax.imageio.ImageIO;
import java.awt.image.*;
//threading
import java.util.concurrent.*;
public class Test
{
	//threading
	public static ExecutorService executorService;
	final static boolean shouldFill = true;
	final static boolean shouldWeightX = true;
	final static boolean RIGHT_TO_LEFT = false;
	private static String[] Sensors = new String[10];
	private static String timewindow = "";
	//UI
	public static JFrame frame = new JFrame("Mavan Data Extration");
	public static JFrame frameFile = new JFrame("File Exportation");
	public static JCheckBox alldata = new JCheckBox("All Data");
	public static JCheckBox partialdata = new JCheckBox("Filtered Data");
	public static JProgressBar dl = new JProgressBar(0,100);
	public static int progress = 0;
	//data	
	public static List<LcmGui.Baro> BaroData = new ArrayList<LcmGui.Baro>();
	public static List<LcmGui.Baro> BaroDataFiltered = new ArrayList<LcmGui.Baro>();
	public static List<LcmGui.Gps> GpsData = new ArrayList<LcmGui.Gps>();
	public static List<LcmGui.Gps> GpsDataFiltered = new ArrayList<LcmGui.Gps>();
	public static List<LcmGui.Imu> ImuData = new ArrayList<LcmGui.Imu>();
	public static List<LcmGui.Imu> ImuDataFiltered = new ArrayList<LcmGui.Imu>();
	public static List<LcmGui.Pva> PvaData = new ArrayList<LcmGui.Pva>();
	public static List<LcmGui.Pva> PvaDataFiltered = new ArrayList<LcmGui.Pva>();
	public static int barocnt, gpscnt, imucnt, pvacnt, camcnt = 0;
	public static double timeinlow,timeinhigh = 0.0;
	public static JTextArea txttest = new JTextArea();
//Log stuff
//	
	public static Log logout;
	public static Log login;
	public static LCM lcm;	

	//first message time to compare rest of time to
	public static double firsttime = 0;
	public static boolean checked = false;
	//Filename chosen from the file selector windo
	public static String Filein = "";
	//filtering output
	public static boolean barochkd, gpschkd, imuchkd, pvachkd, allchkd, filterchkd = false;		
	
/*	public static class ReaderThread implements Runnable
	{
		protected BlockingQueue<String> blockingQueue = null;
		public ReaderThread(BlockingQueue<String> blockingQueue){blockingQueue = blockingQueue;}
		@Override
		public void run()
		{
			BufferedReader br = null;
			try
			{
				br = new BufferedReader(new FileReader(new File("./testimg.jpg")));
				String buffer = null;
//				while((buffer = br.readLine()) != null) blockingQueue.put(buffer);
			//	blockingQueue.put("EOF");
			}catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
//			catch(IOException e)
//			{
//				e.printStackTrace();
//			}
//			catch(InterruptedException e) 
//			{
//				e.printStackTrace();
//			}
			finally 
			{
				try
				{
					br.close();
				}catch(IOException e) {e.printStackTrace();}
			}
		}
	}
	public static class WriterThread implements Runnable
	{
		protected BlockingQueue<String> blockingQueue = null;
		public WriterThread(BlockingQueue<String> blockingQueue)
		{
			blockingQueue = blockingQueue;
		}
		@Override
		public void run()
		{
			BufferedOutputStream imgout = null;
			try
			{

				File testimg = new File("testimg.jpg");
				BufferedImage cammsg = ImageIO.read(testimg);
				File picfile = new File("/home/phillip/Programs/lcm-gui/imgs/sample.png");
				while(true)
				{
				//	String buffer = blockingQueue.take();
				//	if(buffer.equals("EOF")) break;
					
					imgout = new BufferedOutputStream(new FileOutputStream(picfile));
					ImageIO.write(cammsg, "PNG", imgout);
					imgout.close();
				}
//				BufferedOutputStream imgout = new BufferedOutputStream(new FileOutputStream(picfile));
				//	ImageIO.write(cammsg, "jpg", imgout);
				//imgout.close();

	//			BufferedOutputStream imgout = new BufferedOutputStream(new FileOutputStream(picfile));
	//			ImageIO.write(cammsg, "PNG", imgout);
	//			imgout.close();
			}//catch (FileNotFoundException | InterruptedException e) { e.printStackTrace(); }
			catch (IOException e) {e.printStackTrace();}
		}
	}*/
	//progressba
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
		pvas.header = headspace;
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
		Test start = new Test();
		ImageIO.setUseCache(false);
		executorService = Executors.newFixedThreadPool(1);
		start.createGUI();
	//	BlockingQueue<String> queue = new ArrayBlockingQueue<String>(1024);
	//	ReaderThread reader = new ReaderThread(queue);
	//	WriterThread writer = new WriterThread(queue);

	//	new Thread(reader).start();
	//	new Thread(writer).start();
		//Prompts User with a choice of a file.
	//	FileChoice();
//		txttest.append("\nWriting Log.\n");
//		txttest.append("\nBuilding Test Data.\n");
		//Builds initial test data for use of program without test file.
//		start.TestData();
		//LogWrite();
//		LCMIncoming();
		
	}
/*	public static double convertByteArrayToDouble(byte[] doubleBytes)
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
	}*/
	public static void LogWrite()	
	{
			
		//do lcm-logger in bash on seprate terminal
		//use log file produced as input
		
			lcm = LCM.getSingleton();
	//		
	//		txttest.append("\nEnteded try in logwrite\n");
			LcmGui.Baro[] bars = new LcmGui.Baro[BaroData.size()];
			bars = BaroData.toArray(bars);
			LcmGui.Gps[] gps = new LcmGui.Gps[GpsData.size()];
			gps = GpsData.toArray(gps);
			LcmGui.Imu[] imus = new LcmGui.Imu[ImuData.size()];
			imus = ImuData.toArray(imus);
			LcmGui.Pva[] pvas = new LcmGui.Pva[PvaData.size()];
			pvas = PvaData.toArray(pvas);
/*			int i = 0;
			while(i < 2000000)
			{	
				lcm.publish("Baro",bars[0]);
				lcm.publish("Gps", gps[0]);
				lcm.publish("Imu", imus[0]);
				lcm.publish("Pva", pvas[0]);
				i++;
			}
*/
	}
	class Progress extends SwingWorker<Double,Double>
	{
		@Override
		public Double doInBackground() throws Exception
		{
			LCMIncoming();
		//	Thread.sleep(1000);
			return 0.0;
		}
		@Override
		public void done()
		{
			try
			{
				txttest.append("\nFile Writing Completed!\n");
				Thread.sleep(1000);
				dl.setValue(100);
			}catch(Exception ignore){}
		}
	}
	public static void waitForImages()
	{
		executorService.shutdown();
		try
		{
			executorService.awaitTermination(1,TimeUnit.DAYS);
		}catch(InterruptedException e){e.printStackTrace();}
	}
	public static void LCMIncoming()
	{
//		int prog = 0;
	//	JProgressBar dl = new JProgressBar(0,100);
//		dl.setSize(50,100);
//		dl.setValue(0);
//		dl.setStringPainted(true);
	//	frameFile.add(dl);
		Log.Event event = new Log.Event();
		
	try{	
			//Reads the user choice from the file selector
			//prevent empty file choice.
		if (Filein == "") FileChoice();
		Log login = new Log(Filein, "r");
		int count = 0;
		int prog = 0;
		//txttest.append("Channel:\t" + event.channel + "\n");
		FileWriter barowrite = null;
		FileWriter barowritefiltered = null;
		FileWriter gpswrite = null;
		FileWriter gpswritefiltered = null;
		FileWriter imuwrite = null;
		FileWriter imuwritefiltered = null;
		FileWriter pvawrite = null;
		FileWriter pvawritefiltered = null;
		String builder = new String();
		String columns = "";
		builder = "";
		File BaroFile = new File("Baro-sensor-test.csv");
		File BaroFileFiltered = new File("Baro-Sensor-Filtered.csv");
		File GpsFile = new File("Gps-Sensor-test.csv");
		File GpsFileFiltered = new File("Gps-Sensor-Filtered.csv");
		File ImuFile = new File("Imu-sensor-test.csv");
		File ImuFileFiltered = new File("Imu-Sensor-Filtered.csv");
		File PvaFile = new File("Pva-sensor-test.csv");
		File PvaFileFiltered = new File("Pva-Sensor-Filtered.csv");
		try
		{
			barowrite = new FileWriter(BaroFile);
			barowritefiltered = new FileWriter(BaroFileFiltered);
			columns = "Sensor_Name, Time(s), Time(ns), Altitude(m)\n";
			barowrite.write(columns);
			barowritefiltered.write(columns);
			gpswrite = new FileWriter(GpsFile);
			gpswritefiltered = new FileWriter(GpsFileFiltered);
			columns = "Sensor_Name, Time(s), Time(ns), Latitude(deg), Longitude(deg), Altitude(m)\n";
			gpswrite.write(columns);
			gpswritefiltered.write(columns);
			imuwrite = new FileWriter(ImuFile);
			imuwritefiltered = new FileWriter(ImuFileFiltered);
			columns = "Sensor_Name, Time(s),Time(ns),Acceleration_x(m/(s*s)),Acceleration_y(m/(s*s)),Acceleration_z(m/(s*s)),Angular_Rate_x(rad/h),Angular_Rate_y(rad/h),Angular_Rate_z(rad/h)\n";
			imuwrite.write(columns);
			imuwritefiltered.write(columns);
			pvawrite = new FileWriter(PvaFile);
			pvawritefiltered = new FileWriter(PvaFileFiltered);
			columns = "Sensor_Name, Time(s), Time(ns), Latitude(deg), Longitude(deg), Altitude(m), Velocity_x(m/(s*s)),Velocity_y(m/(s*s)),Velocity_z(m/(s*s)), Attitude\n";
			pvawrite.write(columns);
			pvawritefiltered.write(columns);
		}catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
			int imagefilecnt = 0;
			while(true) 
			{
				try
				{
					event = new Log.Event();
					//parse events from log file
					event = login.readNext();
						
					//Checks found event's channel for a known channel to pull data
					if (event.channel.contains("Baro") && barochkd == true)
					{
						//Decodes data and constructs new Baro obj to store.					
						LcmGui.Baro baromsg = new LcmGui.Baro(event.data);
						if(barocnt == 0)
						{
							txttest.append("First Baro: " + baromsg.header.sec + "\n");	
							BaroIncomingData(baromsg);
							barocnt++;
						}
						if(count == 0) 
						{
							firsttime = baromsg.header.sec;
							count++;
						}
						builder = ("Baro-Sensor" + "," + baromsg.header.sec + "," + baromsg.header.nsec + "," + baromsg.altitude_m + "\n");
						if(allchkd == true)
							barowrite.write(builder);
						
						if(CompareTime(baromsg.header.sec) == true && filterchkd == true)
						{
						 	barowritefiltered.write(builder);	
						}
						else
						{
							txttest.append("Out of window: Stored:\t"+firsttime+"Compared:\t"+baromsg.header.sec);
						}
//IMAGE TESTING
						if(imagefilecnt < 24000)
						{	
						try{

							//LcmGui.Cam cammsg = new LcmGui.Cam(event.data);
							File testimg = new File("testimg.jpg");
							if(imagefilecnt%240 == 0)
							{
								prog++;
								if(prog <= 99)
									dl.setValue(prog);
							}
							BufferedImage cammsg = ImageIO.read(testimg);
							if(imagefilecnt == 0)
							{
								File imgdir = new File("imgs");
								deletedir(imgdir);
								imgdir.mkdir();
							}
							File picfile = new File("/home/phillip/Programs/lcm-gui/imgs/sample" + imagefilecnt + ".jpg");
							BufferedOutputStream imgout = new BufferedOutputStream(new FileOutputStream(picfile));
							imagefilecnt++;
						//	ImageIO.write(cammsg, "jpg", imgout);
						//	imgout.close();

							Runnable r = new Runnable()
							{
								@Override
								public void run()
								{
									try
									{

										BufferedOutputStream imgout = new BufferedOutputStream(new FileOutputStream(picfile));
										ImageIO.write(cammsg, "jpg", imgout);
										imgout.close();
									}catch(IOException e){e.printStackTrace();}
								}
							};
							executorService.submit(r);
						}catch(FileNotFoundException a){}
						}
					}	
						

					//	tai time
					//	use first time to compare to rest
					if (event.channel.contains("Gps") && gpschkd == true)
					{
						LcmGui.Gps gpsmsg = new LcmGui.Gps(event.data);
						if(gpscnt == 0)
						{	
							GpsIncomingData(gpsmsg);
							gpscnt++;
						}
						BufferedWriter buffw = new BufferedWriter(gpswrite);
						txttest.append("\nGPS found from log file!!\n");
						if(count == 0)
						{
							count++;
							firsttime = gpsmsg.header.sec;
						}
						builder = ("Gps-Sensor" + "," + gpsmsg.header.sec + "," + gpsmsg.header.nsec + "," + gpsmsg.latitude_deg + "," + gpsmsg.longitude_deg + "," + gpsmsg.altitude_m + "\n");
					//	txttest.append(builder);
						if(allchkd == true)
							gpswrite.write(builder);
						if(CompareTime(gpsmsg.header.sec) == true)
						{
					//		txttest.append("in time:\t" + builder);
						 	gpswritefiltered.write(builder);	
						}
					}
					if(event.channel.contains("Imu") && imuchkd == true)
					{
						LcmGui.Imu imumsg = new LcmGui.Imu(event.data);
						if(imucnt == 0)
						{	
							ImuIncomingData(imumsg);
							imucnt++;
						}
						if(count == 0)
						{
						       	count++;
							firsttime = imumsg.header.sec;
						}
						builder = ("Imu-Sensor" + "," + imumsg.header.sec + "," + imumsg.header.nsec + "," + imumsg.accel[0] + "," + imumsg.accel[1] + "," + imumsg.accel[2] + "," + imumsg.angular_rate[0] + "," + imumsg.angular_rate[1] + "," + imumsg.angular_rate[2] + "\n");
						if(allchkd == true)
							imuwrite.write(builder);
						if(CompareTime(imumsg.header.sec) == true && filterchkd == true)
						{
						 	imuwritefiltered.write(builder);	
						}
					}
					if(event.channel.contains("Pva") && pvachkd == true)
					{
						LcmGui.Pva pvamsg = new LcmGui.Pva(event.data);
						txttest.append("Pva msg found");
						if(pvacnt == 0)
						{	
							PvaIncomingData(pvamsg);
							pvacnt++;
						}
						if(count == 0)
						{
						       count++;
					       	       firsttime = pvamsg.header.sec;
						}
						builder = ("Pva-Sensor" + "," + pvamsg.header.sec + "," + pvamsg.header.nsec + "," + pvamsg.latitude + "," + pvamsg.longitude + "," + pvamsg.altitude_m + "," + pvamsg.velocity[0] +"," + pvamsg.velocity[1] + "," + pvamsg.velocity[2] + "," + pvamsg.attitude + "\n");
						if(allchkd == true)
							pvawrite.write(builder);
						if(CompareTime(pvamsg.header.sec) == true && filterchkd == true)
						{
							txttest.append("writing to pva");
						 	pvawritefiltered.write(builder);	
						}
					}
					//images
					//if(event.channel.contains("cam") && camchkd == true)
/*				//	{
						try{

						//LcmGui.Cam cammsg = new LcmGui.Cam(event.data);
							File testimg = new File("testimg.jpg");
							BufferedImage cammsg = ImageIO.read(testimg);
							if(imagefilecnt == 0)
							{
								new File("imgs").mkdir();
							//	File imgdir = new File("/home/phillip/lcm-gui/imgs");
							//	imgdir.mkdirs();
							}
							File picfile = new File("/home/phillip/Programs/lcm-gui/imgs/sample" + imagefilecnt + ".jpg");
							imagefilecnt++;
							ImageIO.write(cammsg, "jpg", picfile);
						}catch(FileNotFoundException a){}
				//	}*/
				}catch (EOFException e){
				//	txttest.append("\nHoly shit we reached the end"+ count+"\n");
					barowrite.close();
					barowritefiltered.close();
					gpswrite.close();
					gpswritefiltered.close();
					imuwrite.close();
					imuwritefiltered.close();
					pvawrite.close();
					pvawritefiltered.close();
					break;
				}
			}
		//	txttest.append("\nBaros found:\t" + (count + 1) +"\n");
			
	}catch(IOException a) {}
	}
	public static void deletedir(File file)
	{
		File[] cont = file.listFiles();
		if(cont != null)
		{
			for(File f:cont)
			{
				if(! Files.isSymbolicLink(f.toPath()))
					deletedir(f);
			}
		}	
		file.delete();
	}
	public static void BaroIncomingData(LcmGui.Baro bar)
	{
		BaroData.add(bar);		
	}
	public static void GpsIncomingData(LcmGui.Gps gps)
	{
		GpsData.add(gps);
	}
	public static void ImuIncomingData(LcmGui.Imu imu)
	{
		ImuData.add(imu);
	}
	public static void PvaIncomingData(LcmGui.Pva pva)
	{
		PvaData.add(pva);
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
	public void addComponents(Container pane, int counter)
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
			//change from 4,2 to 5,2 to add the all data or partial data options
			GridLayout Lout = new GridLayout(5,2);
			pane.setLayout(Lout);
			//top left
			pane.add(new JLabel("<html>Please select a LCM log file<br>Data will be read from this file."), BorderLayout.CENTER);
			JButton filebutton = new JButton("File Explorer");
			//top right
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
			JPanel Datafilterwindow = new JPanel();
			alldata = new JCheckBox("All Data");
			partialdata = new JCheckBox("Filtered Data");
			Datafilterwindow.add(alldata);
			Datafilterwindow.add(partialdata);
			JLabel dataprompt = new JLabel("<html>Please select what you would like to download:<br>All data or filtered by time window.</html>");
			pane.add(dataprompt);
			pane.add(Datafilterwindow);
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
							List<String> names = new ArrayList<String>();
							Component[] comps = btnwindow.getComponents();
						
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
										names.add(textname);
										
									}
								}
							}
							String[] arr = new String[names.size()];
							arr = names.toArray(arr);
							for (String part : arr)
							{
								if (part.contains("Baro"))
								{
									barochkd = true;
									txttest.append("\nGenerating "+ part +" files.\n");
								}
								if (part.contains("Gps"))
								{
									gpschkd = true;
									txttest.append("\nGenerating "+ part +" files.\n");
								}
								if (part.contains("Imu"))
								{
									imuchkd = true;
									txttest.append("\nGenerating "+ part +" files.\n");
								}
								if (part.contains("Pva"))
								{
									pvachkd = true;
									txttest.append("\nGenerating "+ part +" files.\n");
								}
							}
							if(alldata.isSelected() == true)
								allchkd = true;
							else
								allchkd = false;
							if(partialdata.isSelected() == true)
								filterchkd = true;
							else
								filterchkd = false;

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
							SendFormData(arr, in);
							ShowFormTwo();
							//what happens when you press submit
							//Runs LCMIncoming in the background so UI doesnt freeze
							(new Progress()).execute();
//							doInBackground();
//							LCMIncoming();
							//FillData(txttest);
							

						}
					});
			
		}
		else
		{
			//File Export Frame
			txttest.setSize(900,1800);
			txttest.setLineWrap(true);
			txttest.setEditable(false);
			txttest.setVisible(true);
			txttest.setRows(20);
			JScrollPane scroll = new JScrollPane (txttest);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			
			pane.add(scroll);
			FlowLayout flow = new FlowLayout();
			GridLayout dlgrid = new GridLayout(2, 0, 20, 20);
			pane.setLayout(flow);
			button = new JButton("Click to Download All Data as CSV");
		//	pane.add(button);
			JPanel dlholder = new JPanel();
			dlholder.setPreferredSize(new Dimension(800, 150));
			dl.setSize(900,200);
			dl.setStringPainted(true);
			dlholder.setLayout(dlgrid);
			JLabel dllabel = new JLabel("Download Progress");

			dlholder.add(dllabel);
			dlholder.add(dl);
			pane.add(dlholder);
			//Csv click button event
		//	button.addActionListener(new ActionListener(){
		//				public void actionPerformed(ActionEvent e)
		//				{
		//					txttest.append("Exporting All Data to CSV\n");
				//			try{
				//				LogWrite();
				//				filemake();	
				//			}catch (IOException a)
		//					{

		//					}
		//				}
		//			});
			button = new JButton("Click to Download Data Within Time Window as CSV");
		//	pane.add(button);
		//	button.addActionListener(new ActionListener(){
		//				public void actionPerformed(ActionEvent e)
		//				{
		//					txttest.append("Exporting Time Filtered Data to CSV\n");
		//					try{
		//		//				filemakefiltered();	
		//					}catch (IOException a)
		//					{
//
//							}
//						}
//					});
		}
	}
/*	public static void FillData(JTextArea txtbox)
	{
		//check to make sure data is within window timeinlow timeinhigh
		//check which sensors are active
		//display data from arrays holding types
		//Fills the textbox with the sensor data
		//
		LcmGui.Baro[] bars = new LcmGui.Baro[BaroData.size()];
		bars = BaroData.toArray(bars);
		LcmGui.Gps[] gps = new LcmGui.Gps[GpsData.size()];
		gps = GpsData.toArray(gps);
		LcmGui.Imu[] imus = new LcmGui.Imu[ImuData.size()];
		imus = ImuData.toArray(imus);
		LcmGui.Pva[] pvas = new LcmGui.Pva[PvaData.size()];
		pvas = PvaData.toArray(pvas);

//		for (LcmGui.Baro x : bars)
//			txttest.append(x.header.sec + "");
//		for (LcmGui.Gps x : gps)
///			txttest.append(x.header.sec + "");
//		for (LcmGui.Imu x : imus)
//			txttest.append(x.header.sec + "");
//		for (LcmGui.Pva x : pvas)
//			txttest.append(x.header.sec + "");
		String holder = "";
		for (String part : Sensors)
		{
			if (part == "Baro")
			{
				for (int i =0; i < BaroData.size(); i++)
				{
					//holder = holder + "Looping Baro\n";
				//	txttest.append(bars[i].header.sec + "\n");
					if (CompareTime(bars[i].header.sec) == true)
					{
						holder = holder + "Baro-Sensor:\t" + bars[i].header.sec + " s,";
						holder = holder + "" + bars[i].header.nsec + " ns, ";
						holder = holder + "" + bars[i].altitude_m + " m";
						holder = holder + "\n";
						BaroDataFiltered.add(bars[i]);
					}	
				}
			}
			if (part == "Gps")
			{
				for (int i = 0; i < GpsData.size(); i++)
				{
					if(CompareTime(gps[i].header.sec) == true)
					{
						holder = holder + "Gps-Sensor:\t" + gps[i].header.sec + " s, ";
						holder = holder + "" + gps[i].header.nsec + " ns,";
						holder = holder + "" + gps[i].latitude_deg + " degrees,";
						holder = holder + "" + gps[i].longitude_deg + " degrees, ";
						holder = holder + "" + gps[i].altitude_m + " m";
						holder = holder + "\n";
						GpsDataFiltered.add(gps[i]);
					}
				}
			}	
			if (part == "Imu")
			{
				
				for (int i = 0; i < ImuData.size(); i++)
				{
					if(CompareTime(imus[i].header.sec) == true)
					{
						holder = holder + "Imu-Sensor:\t" + imus[i].header.sec + " s, ";
						holder = holder + "" + imus[i].header.nsec + " ns, ";
						holder = holder + "{ " + imus[i].accel[0] + " , " + imus[i].accel[1] + " , " + imus[i].accel[2] + " } m/(s*s), ";
						holder = holder + "{ " + imus[i].angular_rate[0] + " , " + imus[i].angular_rate[1] + " , " + imus[i].angular_rate[2] + "} rad/hr";
						holder = holder + "\n";
						ImuDataFiltered.add(imus[i]);
					}
				}
			}
			if (part == "Pva")
			{
				for (int i = 0; i < PvaData.size(); i++)
				{
					if(CompareTime(pvas[i].header.sec) == true)
					{
						holder = holder + "Pva-Sensor:\t" + pvas[i].header.sec + " s, ";
						holder = holder + "" + pvas[i].header.nsec + " ns, ";
						holder = holder + "" + pvas[i].latitude + " degrees, ";
						holder = holder + "" + pvas[i].longitude + " degrees, ";
						holder = holder + "" + pvas[i].altitude_m + " m, ";
						holder = holder + "{ " + pvas[i].velocity[0] + " , " + pvas[i].velocity[1] + " , " + pvas[i].velocity[2] + " } m/(s*s)";
						holder = holder + "" + pvas[i].attitude;
						holder = holder + "\n";
						PvaDataFiltered.add(pvas[i]);
					}
				}
			}
		
		}
		txtbox.append(holder);

	}*/
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
		int a,b, c = 0;
		a = (int)time;
		double fractional = round(time - a, 2);
		String[] l = Double.toString(time).split("\\.");
		int intplace = l[0].length();
		int decplaces = l[1].length();
		if(decplaces <= 1) decplaces = decplaces + 1;
		b = (int)(fractional * Math.pow(10, decplaces));
		while(b > 60)
		{	
			a = a + 1;
			b = b - 60;
		}
		if (a >= 1) a = a*60;
		
	//	b = b*1000000000;
		c = a + b;
		String together = "";
		//together = ("" + a + "." + b);
		together = ("" + c);
		double out = Double.parseDouble(together);
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
/*		LcmGui.Baro[] bars = new LcmGui.Baro[BaroData.size()];
		bars = BaroData.toArray(bars);
		LcmGui.Gps[] gps = new LcmGui.Gps[GpsData.size()];
		gps = GpsData.toArray(gps);
		LcmGui.Imu[] imus = new LcmGui.Imu[ImuData.size()];
		imus = ImuData.toArray(imus);
		LcmGui.Pva[] pvas = new LcmGui.Pva[PvaData.size()];
		pvas = PvaData.toArray(pvas);
		if (firsttime <= 0 && checked == false)
		{
			long[] arr = new long[] {bars[0].header.sec, gps[0].header.sec, imus[0].header.sec, pvas[0].header.sec};
		//	txttest.append("First Values: Baro1:\t" + arr[0] +"Gps1:\t" + arr[1] + "Imu1:\t"+arr[2]+"Pva1:\t"+arr[3]);
			long min = 0;
			for(int i =0; i <arr.length; i++) {
				if (arr[i] < min)
				{
					min = arr[i];
				}
			}
			firsttime = min;
			txttest.append("\n"+ firsttime);
			checked = true;
		}
		*/
		//Checks the time window
		//JUST FOR TESTING
//		firsttime =0;
		txttest.append("\nHIgh: " + timeinhigh + "\nLow: " + timeinlow + "\nTested: " + timestamp + "\n");
		if((timestamp - firsttime)/1.00 <= (timeinhigh) && ((timestamp - firsttime)/1.00 >= (timeinlow)))
		{
			txttest.append("Result: true\n");
			return true;
			
		}
		else
		{
			txttest.append("Result: False\n");
			return false;
		}

		//
	//testing use of comparison
	//if (timeinlow*60 <= timestamp && timeinhigh*60 >= timestamp)
	//		return true;
	//	else
	//		return false;
	}

/*	public static void filemake() throws IOException
	{
		
		LcmGui.Baro[] bars = new LcmGui.Baro[BaroData.size()];
		bars = BaroData.toArray(bars);
		LcmGui.Gps[] gps = new LcmGui.Gps[GpsData.size()];
		gps = GpsData.toArray(gps);
		LcmGui.Imu[] imus = new LcmGui.Imu[ImuData.size()];
		imus = ImuData.toArray(imus);
		LcmGui.Pva[] pvas = new LcmGui.Pva[PvaData.size()];
		pvas = PvaData.toArray(pvas);
		
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
		for(int i =0; i < BaroData.size(); i++)
		{
			try {builder.append("Baro-Sensor" + "," + bars[i].header.sec + "," + bars[i].header.nsec + "," + bars[i].altitude_m + "\n");
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
		for (int i = 0; i < GpsData.size(); i++)
		{
			try{
				builder.append("Gps-Sensor" + "," + gps[i].header.sec + "," + gps[i].header.nsec + "," + gps[i].latitude_deg + "," + gps[i].longitude_deg + "," + gps[i].altitude_m + "\n");
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
		for (int i = 0; i < ImuData.size(); i++)
		{
			try{
				builder.append("Imu-Sensor" + "," + imus[i].header.sec + "," + imus[i].header.nsec + "," + imus[i].accel[0] + "," + imus[i].accel[1] + "," + imus[i].accel[2] + "," + imus[i].angular_rate[0] + "," + imus[i].angular_rate[1] + "," + imus[i].angular_rate[2] + "\n");
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
		for (int i = 0; i < PvaData.size(); i++)
		{
			try{
				builder.append("Pva-Sensor" + "," + pvas[i].header.sec + "," + pvas[i].header.nsec + "," + pvas[i].latitude + "," + pvas[i].longitude + "," + pvas[i].altitude_m + "," + pvas[i].velocity[0] +"," + pvas[i].velocity[1] + "," + pvas[i].velocity[2] + "," + pvas[i].attitude + "\n");
			}catch(NullPointerException m){
			}
		}
		pw.write(builder.toString());
		pw.close();

	}

	public static void filemakefiltered() throws IOException
	{
		LcmGui.Baro[] bars = new LcmGui.Baro[BaroDataFiltered.size()];
		bars = BaroDataFiltered.toArray(bars);
		LcmGui.Gps[] gps = new LcmGui.Gps[GpsDataFiltered.size()];
		gps = GpsDataFiltered.toArray(gps);
		LcmGui.Imu[] imus = new LcmGui.Imu[ImuDataFiltered.size()];
		imus = ImuDataFiltered.toArray(imus);
		LcmGui.Pva[] pvas = new LcmGui.Pva[PvaDataFiltered.size()];
		pvas = PvaDataFiltered.toArray(pvas);
	
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
		for(int i =0; i < BaroDataFiltered.size(); i++)
		{
			try {builder.append("Baro-Sensor" + "," + bars[i].header.sec + "," + bars[i].header.nsec + "," + bars[i].altitude_m + "\n");
			}catch (NullPointerException p){
			}
		}
		pw.write(builder.toString());
		pw.close();
//	
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
		for (int i = 0; i < GpsDataFiltered.size(); i++)
		{
			try{builder.append("Gps-Sensor" + "," + gps[i].header.sec + "," + gps[i].header.nsec + "," + gps[i].latitude_deg + "," + gps[i].longitude_deg + "," + gps[i].altitude_m + "\n");
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
		for (int i = 0; i < ImuDataFiltered.size(); i++)
		{
			try{builder.append("Imu-Sensor" + "," + imus[i].header.sec + "," + imus[i].header.nsec + "," + imus[i].accel[0] + "," + imus[i].accel[1] + "," + imus[i].accel[2] + "," + imus[i].angular_rate[0] + "," + imus[i].angular_rate[1] + "," + imus[i].angular_rate[2] + "\n");
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
		for (int i = 0; i < PvaDataFiltered.size(); i++)
		{
			try{builder.append("Pva-Sensor" + "," + pvas[i].header.sec + "," + pvas[i].header.nsec + "," + pvas[i].latitude + "," + pvas[i].longitude + "," + pvas[i].altitude_m + "," + pvas[i].velocity[0] +"," + pvas[i].velocity[1] + "," + pvas[i].velocity[2] + "," + pvas[i].attitude + "\n");
			}catch(NullPointerException m){
			}
		}
		pw.write(builder.toString());
		pw.close();

	}*/
	
	private void createGUI()
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
		frameFile.setPreferredSize(new Dimension(1000,600));
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

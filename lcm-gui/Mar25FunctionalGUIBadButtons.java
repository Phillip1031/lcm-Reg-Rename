import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.io.*;
import java.nio.file.*;
public class Test
{
	
	final static boolean shouldFill = true;
	final static boolean shouldWeightX = true;
	final static boolean RIGHT_TO_LEFT = false;

	public static void main(String[] args)
	{
		Test t = new Test();
		t.createGUI();
	/*	javax.swing.SwingUtilities.invokeLater(new Runnable() 
				{
					public void run()
					{
						createGUI();
			
					}
				});*/
		String[] dataLines =  new String[] {"Ex","Ph","Mi","Jo","Esc \nNewline"};
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
			btnwindow.add(new JCheckBox("Baro"),BorderLayout.CENTER );
			btnwindow.add(new JCheckBox("Baro"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("GPS"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("imu"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("mag"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("cam1"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("cam2"),BorderLayout.CENTER);
			btnwindow.add(new JCheckBox("All Data"),BorderLayout.CENTER);

			changeFont(btnwindow, f);
				
			pane.add(btnwindow, BorderLayout.CENTER);
			//pane.add(labelwindow, BorderLayout.CENTER);
			
			JLabel timepromt = new JLabel("<html>Enter the time after takeoff you would like data from<br>Format: hhmmss.ss-hhmmss.ss</html>");
			timepromt.setFont(new Font("Verdana", Font.PLAIN, 18));
			pane.add(timepromt);
			JFormattedTextField txtin = new JFormattedTextField("Enter Time Range Here: hhmmss.ss-hhmmss.ss");
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
						public void actionPerformed(ActionEvent e)
						{
							//Enter CODE HERE TOMORROW 3/26/21
							txttest.append("CSV Export Button Working");
							try{
								filemake();	
							}catch (IOException a)
							{

							}
						}
					});
			counter++;
		}
		else
		{
			//File Export Frame
			c.ipady = 0;
			c.weighty = 1.0;
			//Test Txt Area
			//
			JTextArea txttest = new JTextArea("Test \n\n\n\n\n\n\n\nn\n\n\n\nn\n\n\n\n\n\n\n\n\nn\n\n\n\n\n\n\n\n\n\n\n");
			txttest.setSize(700,700);
			txttest.setLineWrap(true);
			txttest.setEditable(false);
			txttest.setVisible(true);
			JScrollPane scroll = new JScrollPane (txttest);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			pane.add(scroll);
			GridLayout Lout = new GridLayout(3,0);
			pane.setLayout(new GridBagLayout());
			c.insets = new Insets(0,0,0,0);
			button = new JButton("Click to Download as CSV");
			c.fill = GridBagConstraints.HORIZONTAL;
			c.anchor = GridBagConstraints.PAGE_START;
			c.ipady = 0;
			c.gridx = 0;
			c.gridwidth = 1;
			c.gridheight = 1;
			c.gridy = 0;
			pane.add(button, c);
			//Csv click button event
			button.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e)
						{
							txttest.append("CSV Export Button Working");
							try{
								filemake();	
							}catch (IOException a)
							{

							}
						}
					});
		}
	}

	public static String escapeChars(String data)
	{
		String escapedData = data.replaceAll("\\R"," ");
		if (data.contains(",") || data.contains("\"") || data.contains("'"))
		{
			data = data.replace("\"","\"\"");
			escapedData = "\"" + data + "\"";
		}
		return escapedData;
	}
	public static String convertToCSV(String[] data)
	{
		//return Stream.of(data)
		//	.map(escapeChars(data))
		//	.collect(Collectors.joining(","));
		return "test";
	}
	public static void filemake() throws IOException
	{
		PrintWriter pw = null;
		try
		{
			pw = new PrintWriter(new File("Examplecsv.csv"));
		}catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		StringBuilder builder = new StringBuilder();
		String columns = "Id,Name";
		builder.append(columns + "\n");
		builder.append("123" + ",");
		builder.append("Jimbo");
		builder.append('\n');
		pw.write(builder.toString());
		pw.close();

	}
	
	private static void createGUI()
	{
		int counter =0;
		JFrame frame = new JFrame("Mavan Data Extraction");
		addComponents(frame.getContentPane(), counter);
		counter++;
		frame.setPreferredSize(new Dimension(1000,1000));
		frame.pack();
		frame.setVisible(true);
		
		
		JFrame frameFile = new JFrame("File exportation");
		frameFile.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponents(frameFile.getContentPane(), counter);
		frameFile.setPreferredSize(new Dimension(1000,1000));
		frameFile.pack();
		frameFile.setVisible(true);
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

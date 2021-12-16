import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;
import java.io.*;
public class Test
{
	
	final static boolean shouldFill = true;
	final static boolean shouldWeightX = true;
	final static boolean RIGHT_TO_LEFT = false;

	public static void main(String[] args)
	{
		javax.swing.SwingUtilities.invokeLater(new Runnable() 
				{
					public void run()
					{
						createGUI();
			
					}
				});
		String[] dataLines =  new String[] {"Ex","Ph","Mi","Jo","Esc \nNewline"};
		//dataLines.add(new String[] {"Bextex", "Michae", "Jimmy","\"here is a quote\""});
	}
	public static void addComponents(Container pane, int counter)
	{
		JButton button;
	//	pane.setPreferredSize(new Dimension(1500,1500));
		GridBagConstraints c = new GridBagConstraints();
		if (counter == 0)
		{
			
			GridLayout Lout = new GridLayout();
			pane.setLayout(Lout);
			if(RIGHT_TO_LEFT)
			{
				pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
			}
			if(shouldFill)
			{
				c.fill = GridBagConstraints.HORIZONTAL;
			}
		
			//Button creation
			button = new JButton("Button 1");
			if(shouldWeightX)
			{
				c.weightx = 0.5;
			}
			c.fill = GridBagConstraints.HORIZONTAL;
			c.gridx = 0;
			c.gridy = 0;
			pane.add(button, c);
			button = new JButton("Button 2");
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.gridx = 1;
			c.gridy = 0;
			pane.add(button, c);
			button = new JButton("Button 3");
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 0.5;
			c.gridx = 2;
			c.gridy = 0;
			pane.add(button, c);
			button = new JButton("Extra long silly name for no reason");
			c.fill = GridBagConstraints.HORIZONTAL;
			c.ipady = 40;
			c.weightx = 0.0;
			c.gridwidth = 3;
			c.gridx = 0;
			c.gridy = 1;
			pane.add(button, c);
			button = new JButton("5");
			c.fill = GridBagConstraints.HORIZONTAL;
			c.ipady = 0;
			c.weighty = 1.0;
			c.anchor = GridBagConstraints.PAGE_END;
			//c.insets = new Insets(10,0,0,0);
			c.gridx = 1;
			c.gridwidth = 2;
			c.gridy = 2;
			pane.add(button, c);
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
			/*JTextPane txt = new JTextPane();
			txt.setText("Testing \r\n Testing\r\n Testing \r\nTesting \r\nTestingnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnnn");
			//txt.setVerticalAlignment(JLabel.TOP);
			txt.setFont(new Font("Verdana", Font.PLAIN, 15));
			txt.setBorder(BorderFactory.createLineBorder(Color.black));
			c.anchor = GridBagConstraints.LINE_START;
			c.fill = GridBagConstraints.BOTH;
			//c.anchor = GridBagConstraints.WEST;
			c.insets = new Insets(10,0,0,0);
			//
			c.gridx = 2;
			c.gridy = 2;
			c.gridwidth = 3;
			c.gridheight = 1;
			pane.add(txt);
			
			//Filler
			//button = new JButton("Test");
			c.fill = GridBagConstraints.BOTH;
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 3;
			c.gridy = 0;
			c.gridheight = 1;
			c.gridwidth = 1;
			//button.setVisible(true);
			//pane.add(button, c);
			//button = new JButton("Test");
			c.gridx = 3;
			c.gridy = 1;
			c.gridwidth = 1;
			c.gridheight = 1;
			//pane.add(button, c);
			//button = new JButton("Test");
			c.gridx = 3;
			c.gridy = 2;
			c.gridwidth = 1;
			c.gridheight = 1;
			*///pane.add(button, c);
			button = new JButton("Click to Download as CSV");
			c.fill = GridBagConstraints.HORIZONTAL;
			//c.ipady = 0;
			//c.weighty = 1.0;
			c.anchor = GridBagConstraints.PAGE_START;
			//c.insets = new Insets(10,0,0,0);
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
							//filemake();	
						}
					});
		}
	}

	public String escapeChars(String data)
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
		//return Stream.of(data)
		//	.map(escapeChars(data))
		//	.collect(Collectors.joining(","));
		return "test";
	}
	public void filemake() throws IOException
	{
		File file = new File("examplecsv.csv");
		PrintWriter pw = null;
		try
		{
			pw = new PrintWriter(file);
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

		/*	FIleWriter output = new FIleWriter(file);
			CSVWriter write = new CSVWriter(output);
			//Header
			String[] head = {"Ex","Name","Address","Phone"};
			write.writeNext(head);
			//Values
			String[] values = {"1", "Jim", "Ballou", "123456789"};
			write.writeNext(values);
			String[] valuesb = {"21", "bJim", "bBallou", "2123456789"};
			write.writeNext(valuesb);
			String[] valuesc = {"31", "cJim", "cBallou", "3123456789"};
			write.writeNext(valuesc);
			write.close();

		}catch (Exception e) 
		{
			e.printStackTrace();
		}
		/*OutputStream f = new FileOutputStream("examplecsv.csv");
		File CSV = new File("examplecsv.csv");
		FileWriter out = "examplecsv.csv";
		File cvsout = new File(out);
		try (Printwriter pw = new PrintWriter(CSV))
		{
			dataLines.Stream().map(this::convertToCSV).forEach(pw::println);

		}
		assertTrue(csvout.exists());
		*/
	}
	
	private static void createGUI()
	{
		int counter =0;
		JFrame frame = new JFrame("Mavan Data Extraction");
		addComponents(frame.getContentPane(), counter);
		counter++;
		//frame.setPreferredSize(new Dimension(1500,1500));
		frame.setPreferredSize(new Dimension(1000,1000));
		frame.pack();
		frame.setVisible(true);
		
		
		JFrame frameFile = new JFrame("File exportation");
		frameFile.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addComponents(frameFile.getContentPane(), counter);
		//frameFile.setPreferredSize(new Dimension(1500,1500));
		frameFile.setPreferredSize(new Dimension(1000,1000));
		frameFile.pack();
		frameFile.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}	
}

package doc_gui;

import java.io.InputStream;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 

import java.util.Date;
import java.util.Enumeration;

import doc.mathobjects.GraphObject;

public class SerialTest implements SerialPortEventListener {
	SerialPort serialPort;
        /** The port we're normally going to use. */
	private static final String PORT_NAMES[] = { 
			"/dev/tty.usbserial-A9007UX1", // Mac OS X
			"/dev/tty.usbmodemfa131",
			"/dev/ttyUSB0", // Linux
			"/dev/ttyACM0", // linux on jason's laptop, had to add this entry to make it work
			"COM3", // Windows
			};
	/** Buffered input stream from the port */
	private InputStream input;
	/** The output stream to the port */
	private OutputStream output;
	/** Milliseconds to block while waiting for port open */
	private static final int TIME_OUT = 10000;
	/** Default bits per second for COM port. */
	private static final int DATA_RATE = 115200;
	
	private static FitnessApp app;
	
	public SerialTest(FitnessApp app){
		this.app = app;
	}

	public void initialize() {
		CommPortIdentifier portId = null;
		Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

		// iterate through, looking for the port
		while (portEnum.hasMoreElements()) {
			CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
			System.out.println("asdfasdfsdfasdf");
			for (String portName : PORT_NAMES) {
				if (currPortId.getName().equals(portName)) {
					portId = currPortId;
					break;
				}
			}
		}

		if (portId == null) {
			System.out.println("Could not find COM port.");
			return;
		}

		try {
			// open serial port, and use class name for the appName.
			serialPort = (SerialPort) portId.open(this.getClass().getName(),
					TIME_OUT);

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE,
					SerialPort.DATABITS_8,
					SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// open the streams
			input = serialPort.getInputStream();
			output = serialPort.getOutputStream();

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This should be called when you stop using the port.
	 * This will prevent port locking on platforms like Linux.
	 */
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		System.out.println("read input");
		if ( ! app.timer.isRunning())
			return;
		if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			try {
				int available = input.available();
				byte chunk[] = new byte[available];
				input.read(chunk, 0, available);
				
				String str = new String(chunk);

				GraphObject graph = null;
				double val;
				System.out.println(str);
				String[] inputs = str.split("\n");
				for ( String s : inputs) {
					if ( s.length() < 3)
						continue;
					graph = null;
					//System.out.println(s);
					val = Double.parseDouble(s.substring(1));
					switch(s.charAt(0)){
						case 'T':
							graph = app.skinTemperatureGraphData;
							val /= 100;
							break;
						case 'G': // skin conductance
							graph = app.skinConductanceGraphData;
							break;
						case 'S': // not used - signal
							
							break;
						
						case 'Q': // not needed
						
							break;
						case 'B':
							graph = app.heartRateGraphData;
							break;
					}
					if (graph != null){
						double currTime =  (new Date().getTime() - app.timeAtStart)/1000.0;
						graph.getLineGraphPoints().addValueWithString( "(" + currTime + "," + val + ")");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
}
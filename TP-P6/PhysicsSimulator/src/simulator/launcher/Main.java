package simulator.launcher;

import simulator.control.Controller;
import simulator.factories.*;
import simulator.model.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.SwingUtilities;

/*
 * Examples of command-line parameters:
 * 
 *  -h
 *  -i resources/examples/ex4.4body.txt -s 100
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100 -gl ftcg
 *  -i resources/examples/ex4.4body.txt -o resources/examples/ex4.4body.out -s 100 -gl nlug
 *
 */

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.model.Body;
import simulator.model.GravityLaws;
import simulator.view.MainWindow;

public class Main {

	// default values for some parameters
	//
	private final static Double _dtimeDefaultValue = 2500.0;
	private final static Integer _sDefaultValue = 150;
	private final static String _mDefaultValue = "batch";

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double _dtime = null;
	private static String _inFile = null;
	private static String _outFile = null;
	private static JSONObject _gravityLawsInfo = null;
	private static Integer _steps = null;
	public static String _BatchOrGUI = null;
	// factories
	private static Factory<Body> _bodyFactory;
	private static Factory<GravityLaws> _gravityLawsFactory;

	private static void init() {
		// initialize the bodies factory
		ArrayList<Builder<Body>> bodyBuilders = new ArrayList<>(); 
		bodyBuilders.add(new BasicBodyBuilder());
		bodyBuilders.add(new MassLosingBodyBuilder());
		
		_bodyFactory = new BuilderBasedFactory<Body>(bodyBuilders);

		// initialize the gravity laws factory
		ArrayList<Builder<GravityLaws>> gravityLawBuilders = new ArrayList<>();
		gravityLawBuilders.add(new NewtonUniversalGravitationBuilder());
		gravityLawBuilders.add(new FallingToCenterGravityBuilder());
		gravityLawBuilders.add(new NoGravityBuilder());
		
		_gravityLawsFactory = new BuilderBasedFactory<GravityLaws>(gravityLawBuilders);
	}

	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseModeOption(line);
			parseInFileOption(line);
			parseDeltaTimeOption(line);
			parseGravityLawsOption(line);
			parseOutFileOption(line);
			parseStepOption(line);
			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());
		
		// mode
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Execution Mode. Possible values: 'batch' (Batch mode), 'gui' "
				+ "(Graphical User Interface mode). Default value: \'" + _mDefaultValue + "\'.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("Bodies JSON input file.").build());

		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _dtimeDefaultValue + ".")
				.build());
		
		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg()
				.desc("Output file, where output is written. Default value: the standard output.").build());
		
		// steps
		cmdLineOptions.addOption(Option.builder("s").longOpt("steps").hasArg()
				.desc("An integer representing the number of simulation steps. Default value: " + _sDefaultValue + ".").build());
		
		// gravity laws -- there is a workaround to make it work even when
		// _gravityLawsFactory is null. 
		//
		String gravityLawsValues = "N/A";
		String defaultGravityLawsValue = "N/A";
		if (_gravityLawsFactory != null) {
			gravityLawsValues = "";
			for (JSONObject fe : _gravityLawsFactory.getInfo()) {
				if (gravityLawsValues.length() > 0) {
					gravityLawsValues = gravityLawsValues + ", ";
				}
				gravityLawsValues = gravityLawsValues + "'" + fe.getString("type") + "' (" + fe.getString("desc") + ")";
			}
			defaultGravityLawsValue = _gravityLawsFactory.getInfo().get(0).getString("type");
		}
		cmdLineOptions.addOption(Option.builder("gl").longOpt("gravity-laws").hasArg()
				.desc("Gravity laws to be used in the simulator. Possible values: " + gravityLawsValues
						+ ". Default value: '" + defaultGravityLawsValue + "'.")
				.build());

		return cmdLineOptions;
	}

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		_inFile = line.getOptionValue("i");
		if (_inFile == null && _BatchOrGUI.equals("batch")) {
			throw new ParseException("An input file of bodies is required");
		}
	}

	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", _dtimeDefaultValue.toString());
		try {
			_dtime = Double.parseDouble(dt);
			assert (_dtime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid delta-time value: " + dt);
		}
	}

	private static void parseGravityLawsOption(CommandLine line) throws ParseException {

		// this line is just a work around to make it work even when _gravityLawsFactory
		// is null, you can remove it when've defined _gravityLawsFactory
		if (_gravityLawsFactory == null)
			return;

		String gl = line.getOptionValue("gl");
		if (gl != null) {
			for (JSONObject fe : _gravityLawsFactory.getInfo()) {
				if (gl.equals(fe.getString("type"))) {
					_gravityLawsInfo = fe;
					break;
				}
			}
			if (_gravityLawsInfo == null) {
				throw new ParseException("Invalid gravity laws: " + gl);
			}
		} else {
			_gravityLawsInfo = _gravityLawsFactory.getInfo().get(0);
		}
	}
	
	private static void parseOutFileOption(CommandLine line) throws ParseException {
		_outFile = line.getOptionValue("o");
	}
	
	private static void parseStepOption(CommandLine line) throws ParseException {
		String s = line.getOptionValue("s", _sDefaultValue.toString());
		try {
			_steps = Integer.parseInt(s);
			assert(_steps > 0);
		}
		catch (Exception e) {
			throw new ParseException("Invalid steps value:" + s);
		}
	}

	private static void parseModeOption(CommandLine line) throws ParseException {
		_BatchOrGUI = line.getOptionValue("m", _mDefaultValue).toLowerCase();
		if (_BatchOrGUI == null) {
			throw new ParseException("An input file of bodies is required");
		}
		if (!(_BatchOrGUI.equals("batch") || _BatchOrGUI.equals("gui")))
			throw new ParseException("Invalid mode");
	}
	
	private static void startBatchMode() throws Exception {
		// create and connect components, then start the simulator
		// create simulator and controller
		PhysicsSimulator ps;
		Controller c;
		OutputStream o;
		
		// set gravity law to one specified by -gl
		ps = new PhysicsSimulator(_dtime, _gravityLawsFactory.createInstance(_gravityLawsInfo));
		c = new Controller(ps, _bodyFactory, _gravityLawsFactory);
		
		// create input and output streams from -i and -o
		InputStream i = new FileInputStream (_inFile);
		if (_outFile == null)
			o = System.out;
		else
			o = new FileOutputStream (_outFile);
		
		// add bodies to simulator
		c.loadBodies(i);
		
		// start simulation
		c.run(_steps, o);
	}
	
	private static void startGUIMode() throws Exception {
		PhysicsSimulator ps;
		Controller c;
		OutputStream o;
		
		// set gravity law to one specified by -gl
		ps = new PhysicsSimulator(_dtime, _gravityLawsFactory.createInstance(_gravityLawsInfo));
		c = new Controller(ps, _bodyFactory, _gravityLawsFactory);
		
		// create input stream from -i
		InputStream i;
		
		if (_inFile != null) {
			i = new FileInputStream (_inFile);
		
			// add bodies to simulator
			c.loadBodies(i);
		}
		
		SwingUtilities.invokeAndWait(new Runnable() {
			@Override
			public void run() {
				new MainWindow(c);
			}
		});
	}

	private static void start(String[] args) throws Exception {
		parseArgs(args);
		if (_BatchOrGUI.equals("batch"))
				startBatchMode();
		else if (_BatchOrGUI.equals("gui"))
			startGUIMode();
	}

	public static void main(String[] args) {
		try {
			init();
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}

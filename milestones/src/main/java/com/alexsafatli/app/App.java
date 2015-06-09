/* Entry point to application. */

package com.alexsafatli.app;
import java.io.*;
import java.lang.StringBuilder;
import java.util.ArrayList;
import org.pegdown.PegDownProcessor;
import org.pegdown.LinkRenderer;
import org.pegdown.ast.RootNode;
import org.apache.commons.cli.*;

public class App {

	private static PegDownProcessor processor = new PegDownProcessor();
	private static LinkRenderer linkRenderer = new LinkRenderer();
	private static ToLatexSerializer serializer;

	private static String renameExtension(String path, String toExt) {
		String target, currExt = getExtension(path);
		if (currExt.equals("")) {
			target = path + "." + toExt;
		} else {
			target = path.replaceAll(currExt,toExt);
		}
		return target;
	}

	private static String getExtension(String path) {
		String ext = "";
		int i = path.lastIndexOf('.');
		if (i > 0 && i < path.length() - 1) { 
			ext = path.substring(i+1).toLowerCase();
		}
		return ext;
	}

	private static char[] readFile(String path) {
		String all = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.getProperty("line.separator"));
				line = br.readLine();
			}
			all = sb.toString();
			br.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file: " + path);
		} catch (IOException ex) { 
			System.out.println("Unable to read file: " + path);
			ex.printStackTrace();
		}
		return all.toCharArray();
	}

	private static void writeMarkdown(char[] source, String toPath) {
		RootNode astRoot = processor.parseMarkdown(source);
		String latexSource = serializer.toLatex(astRoot);
		try { 
			BufferedWriter bw = new BufferedWriter(new FileWriter(toPath));
			bw.write(latexSource);
			bw.close();
		} catch (IOException ex) {
			System.out.println("Error writing to file: " + toPath);
			ex.printStackTrace();
		}
	}

	private static void markdownToLatex(String fromPath, String toPath) {
		char[] mdSource = readFile(fromPath);
		writeMarkdown(mdSource,toPath);
	}

    public static void main(String[] args) {
    	CommandLineParser parser = new DefaultParser();
    	Options opts = new Options();
    	opts.addOption("i",true,"specifies an include TeX file");
    	try {
    		CommandLine line = parser.parse(opts,args);
    	}
    	catch (ParseException exp) {
    		System.err.println("Parsing failed. Reason: " + exp.getMessage());
    		exit(1);
    	}
    	String include = "";
    	if (line.hasOption("i")) {
    		include = line.getParsedOptionValue("i");
    	}
    	serializer = new ToLatexSerializer(linkRenderer,include);
        for (String path : line.getArgs()) {
        	markdownToLatex(path,renameExtension(path,"tex"));
        }
    }
}
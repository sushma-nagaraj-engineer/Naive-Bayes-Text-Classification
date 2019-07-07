package com.mlhw2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

public class NaiiveBayes {
	static double prior_spam = 0.0;
	static double prior_ham = 0.0;
	static int scount = 0;
	static int hcount = 0;
	public static Set<String> stopwordList = new HashSet<String>();
	static HashMap<String, Double> conditionalprob_spam = new HashMap<String, Double>();
	static HashMap<String, Double> conditonalprob_ham = new HashMap<String, Double>();
	// i have stored identifying key words in this set
	public static Set<String> keys = new HashSet<String>();
	// i have stored identifying keywords for spam and ham
	public static TreeMap<String, Integer> spamkeys = new TreeMap<String, Integer>();
	public static TreeMap<String, Integer> hamkeys = new TreeMap<String, Integer>();

	// storing the frequency
	private static void fileKeys(File inputfile, int check) throws Exception {
		if (check == 1) {
			for (File a : inputfile.listFiles()) {
				@SuppressWarnings("resource")
				Scanner sc = new Scanner(a);
				while (sc.hasNext()) {
					String line = sc.nextLine();
					for (String in : line.toLowerCase().trim().split(" ")) {
						in = in.replaceAll("\\<.*?>", "");
						in = in.replaceAll("[0-9]+", "");
						in = in.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", "");

						in = in.replaceAll("\\'", "");
						in = in.replaceAll("-", "");
						in = in.replaceAll("\\.", "");

						if (!in.isEmpty()) {
							if (keys.contains(in)) {
								if (spamkeys.containsKey(in))
									spamkeys.put(in, spamkeys.get(in) + 1);
								else
									spamkeys.put(in, 1);
							}
						}
					}
				}

			}
		}
		// logic for ham if case is not one
		else {
			for (File b : inputfile.listFiles()) {
				@SuppressWarnings("resource")
				Scanner sc = new Scanner(b);
				while (sc.hasNext()) {
					String line = sc.nextLine();
					for (String in : line.toLowerCase().trim().split(" ")) {
						in = in.replaceAll("\\<.*?>", "");
						in = in.replaceAll("[0-9]+", "");
						in = in.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", "");
						in = in.replaceAll("\\'", "");
						in = in.replaceAll("-", "");
						in = in.replaceAll("\\.", "");
						if (!in.isEmpty()) {
							if (keys.contains(in)) {
								if (hamkeys.containsKey(in))
									hamkeys.put(in, hamkeys.get(in) + 1);
								else
									hamkeys.put(in, 1);
							}
						}
					}
				}

			}
		}

	}

	private static void read(File inputa) throws Exception {

		for (File file : inputa.listFiles()) {

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				String line = scanner.nextLine();
				for (String in : line.toLowerCase().trim().split(" ")) {
					in = in.replaceAll("\\<.*?>", "");
					in = in.replaceAll("[0-9]+", "");
					in = in.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", "");

					in = in.replaceAll("\\'", "");
					in = in.replaceAll("-", "");
					in = in.replaceAll("\\.", "");

					if (!in.isEmpty()) {
						keys.add(in);
					}
				}
			}

		}
	}

	public static void counter(int case1) {// first couned for spam
		if (case1 == 1) {
			for (Entry<String, Integer> e1 : spamkeys.entrySet()) {
				scount += e1.getValue();
			}

		} else {
			for (Entry<String, Integer> e2 : hamkeys.entrySet()) {
				hcount += e2.getValue();
			}
		}

	}

	public static void trainingAlgo(File trainspam, File trainham)
			throws Exception {
		// Calculating Prior Probabilities
		double S = 1.0 * (trainspam.listFiles().length)
				/ (trainspam.listFiles().length + trainham.listFiles().length);
		double H = 1.0 - prior_spam;
		prior_spam = Math.log(S);
		prior_ham = Math.log(H);

		counter(1);
		counter(0);

		for (String s : keys) {

			if (spamkeys.containsKey(s)) {

				double a = (spamkeys.get(s) + 1.0)
						/ (scount + keys.size() + 1.0);
				double b = Math.log(a);
				conditionalprob_spam.put(s, b);
			}
		}
		for (String s : keys) {

			if (hamkeys.containsKey(s)) {

				double x = (hamkeys.get(s) + 1.0)
						/ (hcount + keys.size() + 1.0);
				double y = Math.log(x);
				conditonalprob_ham.put(s, y);
			}

		}

	}

	public static void main(String[] args) throws Exception {

		if (args.length != 3) {
			// Checking if the number of arguments entered is six or not

			System.err.println("You need to enter 6 command line arguments."
					+ " Please refer to the readme file for more information.");
			return;
		}
		// giving training and test data sets as inputs
		try{
		String training_set = args[0];
		File spamdata = new File(training_set + "/train/spam");
		File hamdata = new File(training_set + "/train/ham");
		String testing_set = args[1];

		File testdata_spam = new File(testing_set + "/test/spam");
		File testdata_ham = new File(testing_set + "/test/ham");
		String condition = args[2];
		File stopwords = new File("stopwords.txt");

		read(spamdata);
		read(hamdata);

		if (condition.equals("yes")) {
			System.out.println("Filtering stop words");
			Scanner s = null;
			try {
				s = new Scanner(stopwords);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			while (s.hasNext()) {
				String sw = s.next();
				stopwordList.add(sw);
			}
			

			for (String str : stopwordList) {
				if (keys.contains(str)) {
					keys.remove(str);
				}
			}
		}
		fileKeys(spamdata, 1);
		fileKeys(hamdata, 0);

		trainingAlgo(spamdata, hamdata);

		double correctS = readTest(testdata_spam, condition, 1);
		double correctH = readTest(testdata_ham, condition, 0);

		double accuracy = 0.0;
		double tot = spamdata.listFiles().length + hamdata.listFiles().length;
		accuracy = ((double) correctH + (double) correctS) / tot;
		System.out.println("Naive Bayes Accuracy is: " + accuracy * 100);

	}
		catch (NumberFormatException e1) {
			System.err
					.println("Argument entered must be an integer or double type.");
			System.exit(1);
		}
	}
	
	public static double readTest(File inputb, String condition, int case3)
			throws Exception {
		// logic for spam
		if (case3 == 1) {
			double spamcorrect = 0;
			int k = 0;
			for (File file : inputb.listFiles()) {
				if (algorithm(file, prior_ham, prior_spam, stopwordList,
						condition) == 1) {
					k = k+1;
					spamcorrect += 1.0;
				}
			}

			return spamcorrect;
		}
		// logic for ham
		else {
			double hamcorrect = 0;
			int j = 0;
			for (File file : inputb.listFiles()) {
				j = j + 1;
				if (algorithm(file, prior_ham, prior_spam, stopwordList,
						condition) == 0) {
					hamcorrect += 1.0;
				}
			}

			return hamcorrect;
		}

	}

	public static int algorithm(File file, double prior_ham, double prior_spam,
			Set<String> stopwordList, String filtering) throws Exception {

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(file);
		double spam1 = 0;
		double ham1 = 0;
		while (scanner.hasNext()) {
			String line = scanner.nextLine();
			if (filtering.equals("yes")) {
				for (String in : line.toLowerCase().split(" ")) {
					in = in.replaceAll("\\<.*?>", "");
					in = in.replaceAll("[0-9]+", "");
					in = in.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", "");

					in = in.replaceAll("\\'", "");
					in = in.replaceAll("-", "");
					in = in.replaceAll("\\.", "");

					if (!stopwordList.contains(in)) {
						if (conditionalprob_spam.containsKey(in)) {
							spam1 += conditionalprob_spam.get(in);
						} else {

							spam1 += Math
									.log(1.0 / (scount + keys.size() + 1.0));

						}
						if (conditonalprob_ham.containsKey(in))
							ham1 += conditonalprob_ham.get(in);
						else
							ham1 += Math
									.log(1.0 / (hcount + keys.size() + 1.0));

					}
				}
			} else {
				for (String in : line.toLowerCase().split(" ")) {
					in = in.replaceAll("\\<.*?>", "");
					in = in.replaceAll("[0-9]+", "");
					in = in.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]", "");
					in = in.replaceAll("\\'", "");
					in = in.replaceAll("-", "");
					in = in.replaceAll("\\.", "");

					if (conditionalprob_spam.containsKey(in)) {
						spam1 += conditionalprob_spam.get(in);
					} else {

						spam1 += Math.log(1.0 / (scount + keys.size() + 1.0));

					}
					if (conditonalprob_ham.containsKey(in)) {
						ham1 += conditonalprob_ham.get(in);
					} else {
						ham1 += Math.log(1.0 / (hcount + keys.size() + 1.0));
					}

				}
			}

		}

		spam1 = spam1 + prior_spam;
		ham1 = ham1 + prior_ham;

		if (spam1 > ham1) {
			return 1;
		}

		else {
			return 0;
		}

	}

}
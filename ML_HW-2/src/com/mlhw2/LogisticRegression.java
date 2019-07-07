package com.mlhw2;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

public class LogisticRegression 
{	// using hash map as it is the appropriate data structure
		static HashMap<String, Double> pattern = new HashMap<String, Double>();
		static double alpha;
		static double lambda;
		static int iteration;
		static double w0 = 0.1;
		//number of files in testing data 
		static int p=0;
		static int p1=0;
		
	public static Set<String> keywords1 = new HashSet<String>();
	public static Set<String> list_stopword = new HashSet<String>();
	public static HashMap<String, Integer> key_spam = new HashMap<String, Integer>();
	public static HashMap<String, Integer> key_ham = new HashMap<String, Integer>();

	public static HashMap<String,HashMap<String, Integer> >key_spam2 = new HashMap<String, HashMap<String,Integer>>();	
	public static HashMap<String,HashMap<String, Integer> >key_ham2 = new HashMap<String, HashMap<String,Integer>>();

	

	private static void read(File input) throws Exception 
	{

		for(File j: input.listFiles()){

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(j);
			while(scanner.hasNext()){
				String line = scanner.nextLine();
				for(String inp : line.toLowerCase().trim().split(" "))
				{// replacing digits, spaces between words, sgml tags,
					// special characters and symbol with null
					inp=inp.replaceAll("\\<.*?>","");	
					inp=inp.replaceAll("[0-9]+",""); 
					inp=inp.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]","");	
					inp=inp.replaceAll("\\'","");	
					inp=inp.replaceAll("-","");	
					inp=inp.replaceAll("\\.","");	 
					if(!inp.isEmpty()){
						keywords1.add(inp);
					}
				}
			}
			

		}
	}
	static Set<String> hm_mail = new HashSet<String>();
	static Set<String> sm_mail = new HashSet<String>();
	static Set<String> abc = new HashSet<String>();
	private static int counting(String xyz, String t) {
		int freq = 0;
		if(sm_mail.contains(xyz)){

			try {
				for(Entry<String, Integer> n: key_spam2.get(xyz).entrySet()){
					if(n.getKey().equals(t)){
						freq = n.getValue();
						return freq;
					}
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		else if(hm_mail.contains(xyz)){
			try {
				for(Entry<String, Integer> m: key_ham2.get(xyz).entrySet()){
					if(m.getKey().equals(t)){
						freq = m.getValue();
						return freq;
					}
				}
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		
		return 0;
	}

	
	
	private static double computeNet(String pqr) {

		if(sm_mail.contains(pqr)){
			double weight1 = w0;
			try{
				for(Entry<String, Integer> f: key_spam2.get(pqr).entrySet()){
					weight1 = weight1+(f.getValue()* pattern.get( f.getKey() ));
				}	
			}
			catch(Exception e){
				e.printStackTrace();
			}

			return (logisticAlgo(weight1) );
		}
		else{
			double weight = w0;
			try{
				for(Entry<String, Integer> g: key_ham2.get(pqr).entrySet()){
					weight = weight+(g.getValue()* pattern.get( g.getKey() ));
				}	
			}
			catch(Exception e){
				e.printStackTrace();
				
			}
			return (logisticAlgo(weight) );
		}

	}

	private static double logisticAlgo(double weight1) {
	//restricting exponential  values to -100 and computing sigmoid function
		if(weight1<-100){
			return 0.0;
		}
		else if(weight1>100){

			return 1.0;
		}
		else{
			return (1.0 /(1.0+ Math.exp(-weight1))); 
		}
	}

	
	
	public static double check(File inputs,int cases,String selection) throws Exception
		{
		//logic for spam mails
		if(cases==1)
		{
		int k1 = 0 ;
		for(File test_file : inputs.listFiles()){
			p1 = p1+1;
			HashMap<String, Integer> hamtest = new HashMap<String, Integer>();
			Scanner sc = new Scanner(test_file);
			while(sc.hasNext())
			{
				String line = sc.nextLine();
				for(String inp: line.toLowerCase().trim().split(" "))
				{// replacing digits, spaces between words, sgml tags,
					// special characters and symbol with null
					inp=inp.replaceAll("\\<.*?>","");	
					inp=inp.replaceAll("[0-9]+",""); 
					inp=inp.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]","");	
					
					inp=inp.replaceAll("\\'","");	
					inp=inp.replaceAll("-","");
					inp=inp.replaceAll("\\.","");	 
					if(hamtest.containsKey(inp))
						hamtest.put(inp, hamtest.get(inp)+1);
					else
						hamtest.put(inp, 1);
					
				}	
			}
			sc.close();
			
			if(selection.equals("yes")){
				for(String stopword: list_stopword){
					if(hamtest.containsKey(stopword)){
						hamtest.remove(stopword);
					}
				}
			}
			
			int result2 = test(hamtest);
			if(result2== 1)
				k1++;


		}
	
		return k1;
		}
		else
		{
		int k2 = 0 ;
		p = inputs.listFiles().length;

		for(File testfile : inputs.listFiles())
		{
			HashMap<String, Integer> hamtest = new HashMap<String, Integer>();
			Scanner sc = new Scanner(testfile);
			while(sc.hasNext()){
				String line = sc.nextLine();
				for(String inp: line.toLowerCase().trim().split(" "))
				{// replacing digits, spaces between words, sgml tags,
					// special characters and symbol with null
					inp=inp.replaceAll("\\<.*?>","");	
					inp=inp.replaceAll("[0-9]+",""); 
					inp=inp.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]","");	
					
					inp=inp.replaceAll("\\'","");	
					inp=inp.replaceAll("-","");	
					inp=inp.replaceAll("\\.","");	 

					if(hamtest.containsKey(inp)){
						hamtest.put(inp, hamtest.get(inp)+1);
					}else{
						hamtest.put(inp, 1);
					}
				}	
			}
			sc.close();
			int re = test(hamtest);
			if(re == 0){
				k2++;

			}
		}
		
		return k2;
		}
		}
		
		public static int test(HashMap<String, Integer> v) {
		double result = 0;
		for(Entry<String, Integer> m :v.entrySet()){
			if(pattern.containsKey(m.getKey())){
				result += (m.getValue()* pattern.get(m.getKey()));
			}
		}
		result=result+w0;
		if(result<0)
			return 0;
		else
			return 1;
	}
		
	private static void keyFile(File inputs2,int cases) throws Exception 
	{
		//logic for spam mails
		if(cases==1)
		{
		for(File ml1: inputs2.listFiles())
		{
			HashMap<String, Integer> in1 = new HashMap<String, Integer>();

			sm_mail.add(ml1.getName());
			abc.add(ml1.getName());
			Scanner sc = new Scanner(ml1);
			while(sc.hasNext()){
				String line = sc.nextLine();

				for(String inp: line.toLowerCase().trim().split(" ")){
					// replacing digits, spaces between words, sgml tags,
					// special characters and symbol with null
					inp=inp.replaceAll("\\<.*?>","");	
					inp=inp.replaceAll("[0-9]+",""); 
					inp=inp.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]","");	
					inp=inp.replaceAll("\\'","");	
					inp=inp.replaceAll("-","");	
					inp=inp.replaceAll("\\.","");	 
					if(keywords1.contains(inp)){

						if(key_spam.containsKey(inp)){
							key_spam.put(inp, key_spam.get(inp)+1);
						}else{
							key_spam.put(inp, 1);
						}

						if(in1.containsKey(inp)){
							in1.put(inp, in1.get(inp)+1);
						}
						else{
							in1.put(inp, 1);
						}
					}

					key_spam2.put(ml1.getName(), in1);
				}
			}
			sc.close();
		}
		}
		//logic for ham
		else 
		{
			for(File ml: inputs2.listFiles())
			{
			HashMap<String, Integer> in2 = new HashMap<String, Integer>();
			hm_mail.add(ml.getName());
			abc.add(ml.getName());

			@SuppressWarnings("resource")
			Scanner sc = new Scanner(ml);
			while(sc.hasNext()){
				String line = sc.nextLine();
				for(String inp: line.toLowerCase().trim().split(" ")){
					// replacing digits, spaces between words, sgml tags,
					// special characters and symbol with null
					inp=inp.replaceAll("\\<.*?>","");	
					inp=inp.replaceAll("[0-9]+",""); 
					inp=inp.replaceAll("[+^:,?;=%#&~`$!@*_)/(}{]","");	
					inp=inp.replaceAll("\\'","");	
					inp=inp.replaceAll("-","");	
					inp=inp.replaceAll("\\.","");	 
					
					if(!inp.isEmpty()){

						if(keywords1.contains(inp)){
							if(key_ham.containsKey(inp))
								key_ham.put(inp, key_ham.get(inp)+1);
							else
								key_ham.put(inp, 1);
							

						}	
					}	

					if(!inp.isEmpty()){

						if(keywords1.contains(inp)){
							if(in2.containsKey(inp)){
								in2.put(inp, in2.get(inp)+1);
							}else{
								in2.put(inp, 1);
							}
						}
					}
					key_ham2.put(ml.getName(), in2);
				}
			}
			
			}
		}

	}

	public static void main(String[] args) throws Exception 
	{
		if (args.length != 6) {
			// Checking if the number of arguments entered is six or not
			
				System.err.println("You need to enter 6 command line arguments."
						+ " Please refer to the readme file for more information.");
				return;
			}
		try{
		//taking all inputs from command line arguments
		String training_set = args[0];
		File train_spam = new File(training_set+"/train/spam");
		File train_ham = new File(training_set+"/train/ham");
		String testing_set = args[1];
		File test_spam = new File(testing_set+"/test/spam");
		File test_ham = new File(testing_set+"/test/ham");
		String selection = args[2];
		alpha = Double.parseDouble(args[3]);
		lambda = Double.parseDouble(args[4]);
		iteration = Integer.parseInt(args[5]);
		   

		
		read(train_spam);
		read(train_ham);

		// enhancing accuracy by removing stop words
		if(selection.equals("yes"))
		{
			System.out.println("After removing stop words the accuracy is");
			File stopwords = new File("stopwords.txt");
			Scanner sta =null;
			try 
			{
				sta = new Scanner(stopwords);
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			}
			while(sta.hasNext())
			{
				String sw = sta.next();
				list_stopword.add(sw);
			}
			
			for(String sw : list_stopword)
			{
				if(keywords1.contains(sw))				
					keywords1.remove(sw);
			}

		}

		
		keyFile(train_spam,1);
		keyFile(train_ham,0);

		trainingMethod();
	
		double q=check(test_spam,1,selection);
		double r=check(test_ham,0,selection);
		System.out.println("Accuracy on test data is : "+((q+r)/(p+p1))*100);
		
	
	}
		// Handling number format exception that might occur if either the first
		// or the second argument is not a integer
		catch (NumberFormatException e1) {
			System.err
					.println("Argument entered must be an integer or double type.");
			System.exit(1);
		}
}
	
	// random function is used for giving random values to weights	
	public static void trainingMethod() {

		
		for(String o:keywords1){
		
			double r =  2*Math.random()-1;
			pattern.put(o, r);
		}
		
			for(String t : keywords1){
				double q = 0;

				for(String u : abc){
					double result;
					int counter1 = counting(u, t);
					if(sm_mail.contains(u)){
						result = 1; 
					}
					else{
						result = 0;
					}
					double output = computeNet(u);
					double u1 = (result - output);
					q = q + counter1*u1;
				}
				
				double new_weight = pattern.get(t) + alpha*(q -(lambda*pattern.get(t)));
				pattern.put(t, new_weight);
			}
		
	
	}
}	
package m_insurance;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import weka.filters.unsupervised.attribute.NominalToBinary;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;
import tech.tablesaw.api.Table;

import tech.tablesaw.plotly.Plot;
import tech.tablesaw.plotly.api.BoxPlot;
import tech.tablesaw.plotly.api.ScatterPlot;
import tech.tablesaw.plotly.components.Figure;
import tech.tablesaw.plotly.components.Layout;
import tech.tablesaw.plotly.traces.ScatterTrace;

import tech.tablesaw.plotly.traces.Trace;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.trees.RandomForest;
import weka.core.AttributeStats;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;



@SuppressWarnings("unused")
public class Insurance{
	
	static Instances filters(Instances data) throws Exception {
			
			
			NominalToBinary data_filter=new NominalToBinary();
			String[] option_ = new String[1];
			option_[0] = "-A";
			data_filter.setOptions(option_);
			data_filter.setInputFormat(data);
			data=Filter.useFilter(data, data_filter);
			
			
			AttributeSelection filter = new AttributeSelection();  // package weka.filters.supervised.attribute!
			CfsSubsetEval eval = new CfsSubsetEval();
			BestFirst search = new BestFirst();
			search.setOptions(null);
			filter.setEvaluator(eval);
			filter.setSearch(search);
			filter.setInputFormat(data);
			data= Filter.useFilter(data, filter);
			
			
			
		return data;
		
	}

	static void explore(Table insurance) {
		System.out.println(insurance.structure());
		System.out.println(insurance.summary());
		System.out.println(insurance.first(10));
		System.out.println(insurance.missingValueCounts());
		
		
	}
	
	static void plot(Instances result) throws Exception {
		
	      result.setClassIndex(result.numAttributes() - 1);
	      ThresholdCurve tc = new ThresholdCurve();
	      // method visualize
	      VisualizePanel vmc = new VisualizePanel();
	      
	      vmc.setName(result.relationName());
	      PlotData2D tempd = new PlotData2D(result);
	      tempd.setPlotName(result.relationName());
	      tempd.addInstanceNumberAttribute();
	      // specify which points are connected
	      boolean[] cp = new boolean[result.numInstances()];
	      for (int n = 1; n < cp.length; n++)
	        cp[n] = false;
	      tempd.setConnectPoints(cp);
	      // add plot
	      vmc.addPlot(tempd);
	      // method visualizeClassifierErrors
	      String plotName = vmc.getName(); 
	      final javax.swing.JFrame jf = 
	        new javax.swing.JFrame("Weka Classifier Visualize: "+plotName);
	      jf.setSize(500,400);
	      jf.getContentPane().setLayout(new BorderLayout());

	      jf.getContentPane().add(vmc, BorderLayout.CENTER);
	      jf.addWindowListener(new java.awt.event.WindowAdapter() {
	        public void windowClosing(java.awt.event.WindowEvent e) {
	        jf.dispose();
	        }
	      });

	      jf.setVisible(true);
	    }

//	static void plot(Table insurance) {
//	
//		List<String> name = insurance.columnNames();
//		int option;
//		int x;
//		int y;
//		@SuppressWarnings("resource")
//		Scanner obj=new Scanner(System.in);
//		System.out.println("select the columns for the plot\n");
//		do {
//			System.out.println("1."+name.get(0)+"\n2."+name.get(1)+"\n3."+name.get(2)+"\n4."+name.get(3)+"\n5."+name.get(4)+"\n6."+name.get(5)+"\n7."+name.get(6));
//			x = obj.nextInt();
//			y = obj.nextInt();
//			Trace trace1= ScatterTrace.builder(insurance.column(x-1),insurance.column(y-1)).build();
//			Layout layout = Layout.builder()
//					.title(insurance.column(x-1)+" v/s "+insurance.column(y-1))
//					.height(1000)
//					.width(1000)
//					.build();
//			Figure fig2= new Figure(layout,trace1);
//			Plot.show(fig2);
//			System.out.println("exit press 1");
//			option = obj.nextInt();
//			
//		}while(option != 1);
//		System.out.println("Box Plot");
//		do {
//			System.out.println("1."+name.get(0)+"\n2."+name.get(1)+"\n3."+name.get(2)+"\n4."+name.get(3)+"\n5."+name.get(4)+"\n6."+name.get(5)+"\n7."+name.get(6));
//			x = obj.nextInt();
//			y = obj.nextInt();
//			String title=insurance.column(x-1)+" v/s "+insurance.column(y-1);
//			Plot.show(BoxPlot.create(title,insurance, name.get(x-1),name.get(y-1)));
//			
//			System.out.println("exit press 1");
//			option = obj.nextInt();
//			
//		}while(option != 1);
//	}
	

	static Instances preprocess(Instances data) throws Exception {
		System.out.println("A briefing of Attribute\n");
		
			
			if (data.classIndex()==-1) {
				data.setClassIndex(data.numAttributes() - 1);
			}
			int numAttr = data.numAttributes() - 1;
			
			//description about the attributes
			for (int i = 0; i < numAttr; i++) {
				
				if (data.attribute(i).isNominal()) {
					System.out.println("The "+(i+1)+"th Attribute is Nominal");	
					
					int n = data.attribute(i).numValues();
					System.out.println("The "+(i+1)+"th Attribute has: "+n+" values");
				}			
				
				
				AttributeStats as = data.attributeStats(i);
				int dC = as.distinctCount;
				System.out.println("The "+i+"th Attribute has: "+dC+" distinct values");
				if (data.attribute(i).isNumeric()){
					System.out.println("The "+i+"th Attribute is Numeric");	
				    weka.experiment.Stats s = as.numericStats;
				    System.out.println("The "+i+"th Attribute has min value: "+s.min+" and max value: "+s.max+" and mean value: "+s.mean);
				}
				
			}
			
			//filtering and attribute selection
			
			Instances new_data=filters(data);
			System.out.println("Before Filter\n");
			System.out.println(data.toSummaryString());
			System.out.println("After Filtering and attribute selection\n");
			System.out.println(new_data.toSummaryString());
			return new_data;

		
	}
	
	static void model_train(Instances data) throws Exception {

				int option;
				System.out.println("1.Linear regression \n2.MultilayerPerceptron \n3.Random Forest");
				@SuppressWarnings("resource")
				Scanner obj= new Scanner(System.in);
				option =obj.nextInt();
				if(option == 1) {
						LinearRegression lr = new LinearRegression();
						lr.buildClassifier(data);
						weka.core.SerializationHelper.write("regression.model", lr);
						System.out.println(lr);
					}
				else if (option==2) {
					MultilayerPerceptron mp=new MultilayerPerceptron();
					mp.setHiddenLayers("a");
					mp.setLearningRate(0.1);
					mp.setMomentum(0.1);
					mp.setSeed(2);
					mp.buildClassifier(data);
					weka.core.SerializationHelper.write("multilayer.model",mp);
					System.out.println(mp);
				}
				else if (option==3) {
					RandomForest forest= new RandomForest();
					forest.setSeed(2);
					forest.buildClassifier(data);
					forest.setPrintClassifiers(true);
					weka.core.SerializationHelper.write("randomforest.model",forest);
					System.out.println(forest);
					
				}
			
			//output model
		
		
	}
	
	static void testing(Instances data) throws Exception {
		
				 
				LinearRegression model = (LinearRegression) weka.core.SerializationHelper.read("regression.model");
				Classifier scheme=model;
				int option;
				System.out.println("Test\n1.Regression\n2.Multilayer Perceptron\n3.Random forest");
				@SuppressWarnings("resource")
				Scanner obj= new Scanner(System.in);
				option =obj.nextInt();
				
				if (option == 1) {
				model = (LinearRegression) weka.core.SerializationHelper.read("regression.model");
				scheme = model;
				}
				else if (option == 2) {
				
				MultilayerPerceptron model2 = (MultilayerPerceptron) weka.core.SerializationHelper.read("multilayer.model");
				scheme = model2;

				}
				
				else if (option == 3) {
					RandomForest forest= (RandomForest)  weka.core.SerializationHelper.read("randomforest.model");
					scheme  = forest;
				}
				Evaluation eval = new Evaluation(data);
				Random rand1 = new Random(1);
				int folds1= 10;
				

				eval.crossValidateModel(scheme,data,folds1,rand1);

				eval.evaluateModel(scheme,data);
				
				System.out.println(eval.toSummaryString());
				ArrayList<Prediction> pred = eval.predictions();
				double[] diff =new double[] {2000,3000,4000,5000,6000};
				int[] count = new int[] {0,0,0,0,0};
				
				for(int i=0 ; i < 100; i++) {
//					System.out.println("actual input"+data.get(i).classValue());
//					System.out.println("pred--"+pred.get(i).predicted());
					double predic= eval.evaluateModelOnceAndRecordPrediction(scheme, data.get(i));
					if (abs(data.get(i).classValue()-predic) < diff[0]) {
						count[0]++;
						
					}
					else if (abs(data.get(i).classValue()-predic) < diff[1]) {
					
						count[1]++;
						
					}
					else if (abs(data.get(i).classValue()-predic) < diff[2]) {
						count[2]++;
						
					}
					else if (abs(data.get(i).classValue()-predic) < diff[3]) {
						count[3]++;
						
					}
					else {
						count[4]++;
					}
				}
				 for (int i = 0;i<4;i++) {
					 System.out.println("number of pridictions having error less than "+diff[i]+" is "+count[i]+"\n");
				 }
				 System.out.println("number of pridictions having error more than "+diff[4]+" is "+count[4]+"\n");

					
				}
				
				
				
		
	
	
	
	private static double abs(double d) {
		
		if(d<0) {
			return -d;
		}
		return d;
	}
	
	

	public static void main(String args[]) throws Exception {
		Scanner obj=new Scanner(System.in);
		int num;
		try {
			String path=args[0];
			Table insurance=Table.read().csv(path);
			CSVLoader source = new CSVLoader();
			source.setSource(new File(path));
			Instances data= source.getDataSet();
			
			Instances new_data = null;
			Instances train_set = null;
			Instances test_set = null;
			int flag=1;
			
			data.setClassIndex(data.numAttributes()-1);
			do {			
			System.out.println("1.show dataset \n2.plot data \n3.Preprocess\n4.model train\n5.testing\n6.exit");
			num=obj.nextInt();
			switch(num) {
			case 1:explore(insurance);
					break;
			case 2:plot(data);
					break;
			case 3:new_data=preprocess(data);
					break;
			case 4:
				if (flag==1) {
			      int seed = 2;
				  int folds = 5;
				  Random rand = new Random(seed);   // create seeded number generator
				  Instances randData = new Instances(new_data);   // create copy of original data
				  randData.randomize(rand);  
				  train_set = randData.trainCV(folds, 3, rand);
				  test_set = randData.testCV(folds, 3);
				  System.out.println("Train-Test Split...\n");
				  System.out.println(train_set.toSummaryString());
				  System.out.println(test_set.toSummaryString());
				  flag=0;
				}
				  model_train(train_set);
				  break;
			case 5:testing(test_set);
					break;
			default:
					break;
			}
			
			}while(num!=6);
			obj.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

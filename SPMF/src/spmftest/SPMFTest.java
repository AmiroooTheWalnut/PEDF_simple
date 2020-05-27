/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spmftest;

import Esmaieeli.Utilities.TaskThreading.CPUUpperLowerBounds;
import Esmaieeli.Utilities.TaskThreading.TaskSpreader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.Item;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.Sequence;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.database.SequenceStatsGenerator;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.CPT.CPTPlus.CPTPlusPredictor;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.DG.DGPredictor;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.LZ78.LZ78Predictor;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.Markov.MarkovAllKPredictor;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.Markov.MarkovFirstOrderPredictor;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.Predictor;
import ca.pfv.spmf.algorithms.sequenceprediction.ipredict.predictor.TDAG.TDAGPredictor;
import ca.pfv.spmf.algorithms.sequential_rules.rulegrowth.AlgoERMiner;
import ca.pfv.spmf.test.MainTestERMiner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author user
 */
public class SPMFTest {

    public boolean isParallel = true;

    double knownSeqPercentage = 90;
    int maxPredictionLength = 10;
    int numCPUs = 15;

    SPMFTest() {
        try {
            runCPTPlus();
            runDG();
            runPPM();
            runAKOM();
//            runTDAG();
            runLZ78();
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(SPMFTest.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(SPMFTest.class.getName()).log(Level.SEVERE, null, ex);
        }

//        try {
//            //        MainTestERMiner mainTestERMiner = new MainTestERMiner();
//            runERMiner();
//        } catch (IOException ex) {
//            Logger.getLogger(SPMFTest.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    public void runLZ78() throws UnsupportedEncodingException, IOException {
        String inputPath = "roadTrafficFinesLogSPMF_DATA_50000.csv";

        SequenceDatabase trainingSet = new SequenceDatabase();
        trainingSet.loadFileSPMFFormat(inputPath, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

//        // Print the training sequences to the console
//        System.out.println("--- Training sequences ---");
//        for (Sequence sequence : trainingSet.getSequences()) {
//            System.out.println(sequence.toString());
//        }
//        System.out.println();
//
//        // Print statistics about the training sequences
//        SequenceStatsGenerator.prinStats(trainingSet, " training sequences ");

        // Train the prediction model
        LZ78Predictor predictionModel = new LZ78Predictor("LZ78");
        predictionModel.Train(trainingSet.getSequences());

        PredictionSequences predictions=getPredictions(predictionModel,trainingSet);

        System.out.println("Sum event inconformity LZ78: " + getSumError(predictions.knownSeq, predictions.predictedSeq, predictions.realSeq));
    }

    public void runTDAG() throws UnsupportedEncodingException, IOException {
        String inputPath = "roadTrafficFinesLogSPMF_DATA_50000.csv";

        SequenceDatabase trainingSet = new SequenceDatabase();
        trainingSet.loadFileSPMFFormat(inputPath, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

//        // Print the training sequences to the console
//        System.out.println("--- Training sequences ---");
//        for (Sequence sequence : trainingSet.getSequences()) {
//            System.out.println(sequence.toString());
//        }
//        System.out.println();
//
//        // Print statistics about the training sequences
//        SequenceStatsGenerator.prinStats(trainingSet, " training sequences ");

        // Train the prediction model
        TDAGPredictor predictionModel = new TDAGPredictor("TDAG");
        predictionModel.Train(trainingSet.getSequences());

        PredictionSequences predictions=getPredictions(predictionModel,trainingSet);

        System.out.println("Sum event inconformity TDAG: " + getSumError(predictions.knownSeq, predictions.predictedSeq, predictions.realSeq));
    }

    public void runAKOM() throws UnsupportedEncodingException, IOException {
        String inputPath = "roadTrafficFinesLogSPMF_DATA_50000.csv";

        SequenceDatabase trainingSet = new SequenceDatabase();
        trainingSet.loadFileSPMFFormat(inputPath, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

//        // Print the training sequences to the console
//        System.out.println("--- Training sequences ---");
//        for (Sequence sequence : trainingSet.getSequences()) {
//            System.out.println(sequence.toString());
//        }
//        System.out.println();
//
//        // Print statistics about the training sequences
//        SequenceStatsGenerator.prinStats(trainingSet, " training sequences ");

        // The following line is to set optional parameters for the prediction model. 
        // Here we set the order of the markov model to 5.
        String optionalParameters = "order:4";

        // Train the prediction model
        MarkovAllKPredictor predictionModel = new MarkovAllKPredictor("AKOM", optionalParameters);
        predictionModel.Train(trainingSet.getSequences());

        PredictionSequences predictions=getPredictions(predictionModel,trainingSet);

        System.out.println("Sum event inconformity AKOM All-k Order Markov: " + getSumError(predictions.knownSeq, predictions.predictedSeq, predictions.realSeq));
    }

    public void runPPM() throws UnsupportedEncodingException, IOException {
        String inputPath = "roadTrafficFinesLogSPMF_DATA_50000.csv";

        SequenceDatabase trainingSet = new SequenceDatabase();
        trainingSet.loadFileSPMFFormat(inputPath, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

//        // Print the training sequences to the console
//        System.out.println("--- Training sequences ---");
//        for (Sequence sequence : trainingSet.getSequences()) {
//            System.out.println(sequence.toString());
//        }
//        System.out.println();
//
//        // Print statistics about the training sequences
//        SequenceStatsGenerator.prinStats(trainingSet, " training sequences ");

        // Train the prediction model
        MarkovFirstOrderPredictor predictionModel = new MarkovFirstOrderPredictor("PPM");
        predictionModel.Train(trainingSet.getSequences());

        PredictionSequences predictions=getPredictions(predictionModel,trainingSet);

        System.out.println("Sum event inconformity PPM markovian sequence prediction: " + getSumError(predictions.knownSeq, predictions.predictedSeq, predictions.realSeq));
    }

    public void runDG() throws UnsupportedEncodingException, IOException {
        String inputPath = "roadTrafficFinesLogSPMF_DATA_50000.csv";

        SequenceDatabase trainingSet = new SequenceDatabase();
        trainingSet.loadFileSPMFFormat(inputPath, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

//        // Print the training sequences to the console
//        System.out.println("--- Training sequences ---");
//        for (Sequence sequence : trainingSet.getSequences()) {
//            System.out.println(sequence.toString());
//        }
//        System.out.println();
//
//        // Print statistics about the training sequences
//        SequenceStatsGenerator.prinStats(trainingSet, " training sequences ");

        // The following line is to set optional parameters for the prediction model. 
        String optionalParameters = "lookahead:2";

        // Train the prediction model
        DGPredictor predictionModel = new DGPredictor("DG", optionalParameters);
        predictionModel.Train(trainingSet.getSequences());

        PredictionSequences predictions=getPredictions(predictionModel,trainingSet);

        System.out.println("Sum event inconformity DG: " + getSumError(predictions.knownSeq, predictions.predictedSeq, predictions.realSeq));
    }

    public void runCPTPlus() throws UnsupportedEncodingException, IOException {
        // Load the set of training sequences
//		String inputPath = fileToPath("Data.txt");
        String inputPath = "roadTrafficFinesLogSPMF_DATA_50000.csv";

        SequenceDatabase trainingSet = new SequenceDatabase();
        trainingSet.loadFileSPMFFormat(inputPath, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

//        // Print the training sequences to the console
//        System.out.println("--- Training sequences ---");
//        for (Sequence sequence : trainingSet.getSequences()) {
//            System.out.println(sequence.toString());
//        }
//        System.out.println();
//
//        // Print statistics about the training sequences
//        SequenceStatsGenerator.prinStats(trainingSet, " training sequences ");

        // The following line is to set optional parameters for the prediction model. 
        // We want to 
        // activate the CCF and CBS strategies which generally improves its performance (see paper)
        String optionalParameters = "CCF:false CBS:false CCFmin:1 CCFmax:13 CCFsup:2 splitMethod:0 splitLength:12 minPredictionRatio:1.0 noiseRatio:0.0";
        // Here is a brief description of the parameter used in the above line:
        //  CCF:true  --> activate the CCF strategy
        //  CBS:true -->  activate the CBS strategy
        //  CCFmax:6 --> indicate that the CCF strategy will not use pattern having more than 6 items
        //  CCFsup:2 --> indicate that a pattern is frequent for the CCF strategy if it appears in at least 2 sequences
        //  splitMethod:0 --> 0 : indicate to not split the training sequences    1: indicate to split the sequences
        //  splitLength:4  --> indicate to split sequence to keep only 4 items, if splitting is activated
        //  minPredictionRatio:1.0  -->  the amount of sequences or part of sequences that should match to make a prediction, expressed as a ratio
        //  noiseRatio:1.0  -->   ratio of items to remove in a sequence per level (see paper). 

        // Train the prediction model
        CPTPlusPredictor predictionModel = new CPTPlusPredictor("CPT+", optionalParameters);
        predictionModel.Train(trainingSet.getSequences());

        PredictionSequences predictions=getPredictions(predictionModel,trainingSet);

        System.out.println("Sum event inconformity CPT+: " + getSumError(predictions.knownSeq, predictions.predictedSeq, predictions.realSeq));
    }
    
    private PredictionSequences getPredictions(Predictor predictionModel, SequenceDatabase trainingSet)
    {
        SequenceDatabase knownSeq = new SequenceDatabase();
        List knownSeqList = Collections.synchronizedList(new ArrayList(trainingSet.getSequences().size()));
        SequenceDatabase realSeq = new SequenceDatabase();
        List realSeqList = Collections.synchronizedList(new ArrayList(trainingSet.getSequences().size()));
        SequenceDatabase predictedSeq = new SequenceDatabase();
        List predictedSeqList = Collections.synchronizedList(new ArrayList(trainingSet.getSequences().size()));

        if (isParallel == true) {
            CPUUpperLowerBounds[] bounds = TaskSpreader.spreadTasks(numCPUs, trainingSet.getSequences().size());
            Thread threads[] = new Thread[numCPUs];
            for (int i = 0; i < threads.length; i++) {
                final int passed_i = i;
                threads[i] = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (int seq = bounds[passed_i].startIndex; seq < bounds[passed_i].endIndex; seq++) {
                            Sequence sequence = new Sequence(seq);
                            Sequence real = new Sequence(seq);
                            for (int item = 0; item < (int) Math.max(trainingSet.getSequences().get(seq).size() * ((float) knownSeqPercentage) / (100f), 1); item++) {
                                sequence.addItem(trainingSet.getSequences().get(seq).get(item));
                            }
                            for (int item = (int) Math.max(trainingSet.getSequences().get(seq).size() * ((float) knownSeqPercentage) / (100f), 1); item < trainingSet.getSequences().get(seq).size(); item++) {
                                real.addItem(trainingSet.getSequences().get(seq).get(item));
                            }
                            knownSeqList.add(new CustomSequence(sequence));
                            realSeqList.add(new CustomSequence(real));
                            Sequence predictedTempSeq = new Sequence(seq);
                            for (int pred = 0; pred < maxPredictionLength; pred++) {
                                Sequence thePrediction = predictionModel.Predict(sequence);
                                sequence.addItem(new Item(thePrediction.get(0).val));
                                predictedTempSeq.addItem(new Item(thePrediction.get(0).val));
                                if (thePrediction.get(0).val == 13) {
                                    break;
                                }
                            }
                            predictedSeqList.add(new CustomSequence(predictedTempSeq));
                        }
                    }
                });
                threads[i].start();
            }
            for (int i = 0; i < threads.length; i++) {
                try {
                    threads[i].join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(SPMFTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            ArrayList<CustomSequence> tempKnownSeqList = castArrayList(new ArrayList(knownSeqList));
            Collections.sort(tempKnownSeqList);
            ArrayList<CustomSequence> tempPredictedSeqList = castArrayList(new ArrayList(predictedSeqList));
            Collections.sort(tempPredictedSeqList);
            ArrayList<CustomSequence> tempRealSeqList = castArrayList(new ArrayList(realSeqList));
            Collections.sort(tempRealSeqList);

            ArrayList<Sequence> knownSeqListSorted = castArrayList(tempKnownSeqList);
            ArrayList<Sequence> predictedSeqListSorted = castArrayList(tempPredictedSeqList);
            ArrayList<Sequence> realSeqListSorted = castArrayList(tempRealSeqList);

            knownSeq.setSequences(knownSeqListSorted);
            predictedSeq.setSequences(predictedSeqListSorted);
            realSeq.setSequences(realSeqListSorted);
        } else {
            for (int seq = 0; seq < trainingSet.getSequences().size(); seq++) {
                Sequence sequence = new Sequence(seq);
                Sequence real = new Sequence(seq);
                for (int item = 0; item < (int) Math.max(trainingSet.getSequences().get(seq).size() * ((float) knownSeqPercentage) / (100f), 1); item++) {
                    sequence.addItem(trainingSet.getSequences().get(seq).get(item));
                }
                for (int item = (int) Math.max(trainingSet.getSequences().get(seq).size() * ((float) knownSeqPercentage) / (100f), 1); item < trainingSet.getSequences().get(seq).size(); item++) {
                    real.addItem(trainingSet.getSequences().get(seq).get(item));
                }
                knownSeqList.add(new Sequence(sequence));
                realSeqList.add(new Sequence(real));
                Sequence predictedTempSeq = new Sequence(seq);
                for (int pred = 0; pred < maxPredictionLength; pred++) {
                    Sequence thePrediction = predictionModel.Predict(sequence);
                    sequence.addItem(new Item(thePrediction.get(0).val));
                    predictedTempSeq.addItem(new Item(thePrediction.get(0).val));
                    if (thePrediction.get(0).val == 13) {
                        break;
                    }
                }
                predictedSeqList.add(new CustomSequence(predictedTempSeq));
                System.out.println(seq + " from " + trainingSet.getSequences().size());
            }
            knownSeq.setSequences(knownSeqList);
            realSeq.setSequences(realSeqList);
            predictedSeq.setSequences(predictedSeqList);
        }
        return new PredictionSequences(knownSeq,realSeq,predictedSeq);
    }

    private int getSumError(SequenceDatabase knownSeq, SequenceDatabase predictedSeq, SequenceDatabase realSeq) {
        int eventError[] = new int[knownSeq.size()];

        for (int i = 0; i < predictedSeq.size(); i++) {
            eventError[i] = 0;
            if (realSeq.getSequences().get(i).size() > predictedSeq.getSequences().get(i).size()) {
                for (int j = 0; j < predictedSeq.getSequences().get(i).size(); j++) {
                    if (predictedSeq.getSequences().get(i).get(j).val != realSeq.getSequences().get(i).get(j).val) {
                        eventError[i] = eventError[i] + 1;
                    }
                }
                for (int j = predictedSeq.getSequences().get(i).size(); j < realSeq.getSequences().get(i).size(); j++) {
                    eventError[i] = eventError[i] + 1;
                }
            } else {
                eventError[i] = 0;
                for (int j = 0; j < realSeq.getSequences().get(i).size(); j++) {
                    if (predictedSeq.getSequences().get(i).get(j).val != realSeq.getSequences().get(i).get(j).val) {
                        eventError[i] = eventError[i] + 1;
                    }
                }
                for (int j = realSeq.getSequences().get(i).size(); j < predictedSeq.getSequences().get(i).size(); j++) {
                    eventError[i] = eventError[i] + 1;
                }
            }
        }
        int sumEventInconformities = 0;
        for (int i = 0; i < eventError.length; i++) {
            sumEventInconformities = sumEventInconformities + eventError[i];
        }
        return sumEventInconformities;
    }

    public void runERMiner() throws IOException {
//        String input = fileToPath("contextPrefixSpan.txt");  // the database
        String output = ".//output.txt";  // the path for saving the frequent itemsets found

        //  Applying ERMiner algorithm with minsup = 3 sequences and minconf = 0.5
        int minsup_relative = 3;
        double minconf = 0.8;
        AlgoERMiner algo = new AlgoERMiner();

//		// This optional parameter allows to specify the maximum number of items in the 
//		// left side (antecedent) of rules found:
//		algo.setMaxAntecedentSize(1);  // optional
////
////		// This optional parameter allows to specify the maximum number of items in the 
////		// right side (consequent) of rules found:
//		algo.setMaxConsequentSize(3);  // optional
        algo.runAlgorithm("roadTrafficFinesLogSPMF_DATA_50000.csv", output, minsup_relative, minconf);

        // If you want to use an absolute support (percentage value), use
        // the following lines instead:
//		   double minsup_absolute = 0.75;  // it means 75 %
//		   AlgoERMiner algo = new AlgoERMiner();
//		   algo.runAlgorithm(minsup_absolute, minconf, input, output);
        // print statistics
        algo.printStats();

    }

    public static String fileToPath(String filename)
            throws UnsupportedEncodingException {
        URL url = MainTestERMiner.class.getResource(filename);
        return java.net.URLDecoder.decode(url.getPath(), "UTF-8");
    }

    public static <newType, oldType> ArrayList<newType> castArrayList(ArrayList<oldType> list) {
        ArrayList<newType> newlyCastedArrayList = new ArrayList<newType>();
        for (oldType listObject : list) {
            newlyCastedArrayList.add((newType) listObject);
        }
        return newlyCastedArrayList;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SPMFTest sPMFTest = new SPMFTest();
    }
}
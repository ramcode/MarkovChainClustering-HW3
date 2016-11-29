import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;
import java.util.Map.Entry;

/**
 * Created by VenkataRamesh on 11/28/2016.
 */
public class MCLClustering {

    private String fileName;
    private double[][] transitionMatrix;
    private int expansionParam;
    private int inflationParam;
    private Map<String, Integer> nodeMap;
    private static final double PRECISION = 0.000001;


    public MCLClustering(String fileName, int expansionParm, int inflationParam) throws Exception {
        this.fileName = fileName;
        this.expansionParam = expansionParm;
        this.inflationParam = inflationParam;
    }


    public void generateTransitionMatrix() throws Exception {
        Path filePath = Paths.get("data/",fileName);
        Stream<String> rowList = Files.lines(filePath, StandardCharsets.UTF_8);
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        nodeMap = new HashMap<String, Integer>();
        int rows = lines.size();
        int nodeId = 0;
        for (int i = 0; i < rows; i++) {
            String[] nodes = lines.get(i).split("\\s+");
            if (!nodeMap.containsKey(nodes[0])) {
                nodeMap.put(nodes[0], nodeId++);
            }
            if (!nodeMap.containsKey(nodes[1])) {
                nodeMap.put(nodes[1], nodeId++);
            }
        }
        //System.out.println(nodeMap);
        this.transitionMatrix = new double[nodeMap.size()][nodeMap.size()];
        for (int i = 0; i < rows; i++) {
            String[] nodes = lines.get(i).split("\\s+");
            int rowIdx = nodeMap.get(nodes[0]);
            int colIdx = nodeMap.get(nodes[1]);
            transitionMatrix[rowIdx][colIdx] = 1;
            transitionMatrix[colIdx][rowIdx] = 1;
        }
    }


    public void runMCL() throws Exception {
        generateTransitionMatrix();
        addSelfLoops();
        normalizeMatrix(transitionMatrix);
        int iterations = 1;
        while (!checkConvergernce(transitionMatrix)) {
            System.out.println("Running MCL iteration: " + iterations);
            double[][] expandedMatrix = expandMatrix(transitionMatrix);
            double[][] inflatedMatrix = inflateMatrix(expandedMatrix);
            pruneMatrix(inflatedMatrix);
            transitionMatrix = inflatedMatrix;
            iterations++;
        }
        System.out.println("Markov Chain Clustering converged after: " + (iterations - 1) + " iterations");
        System.out.println("Analyzing clusters and generating file...");
        //generateCLUFile(transitionMatrix, fileName);
        generateClustersAndWriteToFile(transitionMatrix, fileName);
        //printMatrix(transitionMatrix);
    }

    private void addSelfLoops() {
        for (int i = 0; i < transitionMatrix.length; i++) {
            for (int j = 0; j < transitionMatrix.length; j++) {
                if (i == j) {
                    transitionMatrix[i][j] = 1;
                }
            }
        }
    }

    private boolean checkConvergernce(double[][] inputMatrix) {
        Arrays.stream(inputMatrix).forEach(x -> {
            Arrays.stream(x).forEach(y -> {
                y = new BigDecimal(y).setScale(5, RoundingMode.HALF_UP).doubleValue();
            });
        });
        int matLen = inputMatrix.length;
        double[][] convergedMatrix = new double[matLen][matLen];
        for (int i = 0; i < matLen; i++) {
            for (int j = 0; j < matLen; j++) {
                for (int k = 0; k < matLen; k++) {
                    convergedMatrix[i][j] += (inputMatrix[i][k]) * (inputMatrix[k][j]);
                }
            }
        }
        Arrays.stream(convergedMatrix).forEach(x -> {
            Arrays.stream(x).forEach(y -> {
                y = new BigDecimal(y).setScale(5, RoundingMode.HALF_UP).doubleValue();
            });
        });
        return Arrays.deepEquals(inputMatrix, convergedMatrix);
    }


    public void pruneMatrix(double[][] inputMatrix) {
        for (int i = 0; i < inputMatrix.length; i++) {
            for (int j = 0; j < inputMatrix.length; j++) {
                if (inputMatrix[i][j] < PRECISION) {
                    inputMatrix[i][j] = 0.0;
                }
            }
        }
    }

    public void generateClustersAndWriteToFile(double[][] transitionMatrix, String fileName) throws Exception {

        FileWriter fw = null;
        try {
            fw = new FileWriter(new File("output/" + fileName.split("\\.")[0] + ".clu"));
            fw.write("*Vertices " + String.valueOf(transitionMatrix.length));

            HashMap<String, List<Integer>> clusterMap = new HashMap<String,List<Integer>>();
            int key = 1;

            for ( int i = 0; i < transitionMatrix.length; i++ ) {

                StringBuilder vertices= new StringBuilder();
                List<Integer> verticesList = new ArrayList<Integer>();

                for ( int j = 0; j < transitionMatrix.length; j++ ) {

                    if ( transitionMatrix[i][j] > 0 ) {

                        vertices.append(String.valueOf(j));
                        verticesList.add(j);

                    }

                }


                if ( !clusterMap.containsKey(vertices) && verticesList.size() > 0 ) {

                    System.out.println(vertices);

                    clusterMap.put(vertices.toString(), verticesList);

                }


            }

            System.out.println("Cluster Size = " + clusterMap.size());


            Iterator<Entry<String,List<Integer>>> iter = clusterMap.entrySet().iterator();


            while ( iter.hasNext() ) {

                Entry<String, List<Integer>> pair = iter.next();
                List<Integer> list = pair.getValue();
                int size = list.size();


                while ( size > 0 ) {

                    fw.write(System.lineSeparator());
                    fw.write(String.valueOf(key));
                    size--;

                }

                key++;

            }


        } catch (Exception e) {

            e.printStackTrace();

        } finally {
            fw.close();
        }


    }



    public void generateCLUFile(double[][] transitionMatrix, String fileName) throws Exception {
        FileWriter fw = null;
        try {
            fw = new FileWriter(new File("output/" + fileName.split("\\.")[0] + ".clu"));
            fw.write("*Vertices " + String.valueOf(transitionMatrix.length));
            int clusterCounter = 0;
            for (int i = 0; i < transitionMatrix.length; i++) {
                for (int j = 0; j < transitionMatrix.length; j++) {
                    if (transitionMatrix[i][j] != 0) {
                        clusterCounter++;
                        break;
                    }
                }
                for (int j = 0; j < transitionMatrix.length; j++) {
                    if (transitionMatrix[i][j] != 0) {
                        fw.write(System.lineSeparator());
                        fw.write(String.valueOf(clusterCounter));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fw.close();
        }
    }

    public double[][] expandMatrix(double[][] inputMatrix) {
        int matLen = inputMatrix.length;
        double[][] expandedMatrix = new double[matLen][matLen];
        int e = expansionParam;

        if(e>1)
        {
            for(int i=0;i<matLen;i++)
            {
                for(int j=0;j<matLen;j++)
                {
                    for(int k=0;k<matLen;k++)
                    {
                        expandedMatrix[i][j] += (transitionMatrix[i][k])*(transitionMatrix[k][j]);
                    }
                }
            }
            if(e==2)
            {
                return expandedMatrix;
            }
        }

        for(int x = e;x>2;x--)
        {
            double[][] interimMatrix = new double[matLen][matLen];
            for(int i=0;i<matLen;i++)
            {
                for(int j=0;j<matLen;j++)
                {
                    for(int k=0;k<matLen;k++)
                    {
                        interimMatrix[i][j] += (expandedMatrix[i][k])*(transitionMatrix[k][j]);
                    }
                }
            }
            expandedMatrix = interimMatrix;
        }
        return expandedMatrix;
    }


    public double[][] inflateMatrix(double[][] expandedMatrix) {
        int matLen = expandedMatrix.length;
        double[][] inflatedMatrix = new double[matLen][matLen];
        for (int i = 0; i < matLen; i++) {
            for (int j = 0; j < matLen; j++) {
                inflatedMatrix[i][j] = Math.pow(expandedMatrix[i][j], inflationParam);
            }
        }
        normalizeMatrix(inflatedMatrix);
        return inflatedMatrix;
    }

    public void normalizeMatrix(double[][] inputMatrix) {
        for (int j = 0; j < inputMatrix.length; j++) {
            double colSum = 0;
            for (int i = 0; i < inputMatrix.length; i++) {
                colSum += inputMatrix[i][j];
            }
            for (int i = 0; i < inputMatrix.length; i++) {
                inputMatrix[i][j] = inputMatrix[i][j] / colSum;
            }
        }
    }

    public void printMatrix(double[][] inMatrix)
    {
        int matLen = inMatrix.length;
        for(int i=0;i<matLen;i++)
        {
            for(int j=0;j<matLen;j++)
            {
                System.out.print(inMatrix[i][j]+" ");
            }
            System.out.println("\n");
        }
    }
}


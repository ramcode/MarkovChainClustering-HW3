import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Created by VenkataRamesh on 11/28/2016.
 */
public class MCLClustering {

    private String fileName;
    private double[][] transitionMatrix;
    private double expansionParam;
    private double inflationParam;
    private Map<String, Integer> nodeMap;

    public MCLClustering(String fileName, double expansionParm, double inflationParam) throws Exception {
        this.fileName = fileName;
        this.expansionParam = expansionParm;
        this.inflationParam = inflationParam;
    }


    public double[][] generateTransitionMatrix(String fileName) throws Exception {
        Path filePath = null;
        try {
            filePath = Paths.get(fileName);
            Stream<String> rowList = Files.lines(filePath, StandardCharsets.UTF_8);
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            nodeMap = new HashMap<String, Integer>();
            int rows = lines.size();
            int nodeId = 0;
            for (int i = 0; i < rows; i++) {
                String[] nodes = lines.get(i).split(" ");
                if (!nodeMap.containsKey(nodes[0])) {
                    nodeMap.put(nodes[0], nodeId++);
                }
                if (!nodeMap.containsKey(nodes[1])) {
                    nodeMap.put(nodes[1], nodeId++);
                }
            }
            System.out.println(nodeMap);
            this.transitionMatrix = new double[nodeMap.size()][nodeMap.size()];
            for (int i = 0; i < rows; i++) {
                String[] nodes = lines.get(i).split(" ");
                int rowIdx = nodeMap.get(nodes[0]);
                int colIdx = nodeMap.get(nodes[1]);
                transitionMatrix[rowIdx][colIdx] = 1;
                transitionMatrix[colIdx][rowIdx] = 1;
            }
        } catch (Exception ex) {
        }
        return transitionMatrix;
    }

    public void runMCL(String filePath) throws Exception {
        generateTransitionMatrix(filePath);

    }

    private void addSelfLoops(){
        for(int i=0; i<transitionMatrix.length;i++){
            for(int j=0; j<transitionMatrix.length;j++){
                if(i==j){
                    transitionMatrix[i][j] = 1;
                }
            }
        }
    }

    private boolean checkConvergernce(){
        return false;
    }


    public void generateCLUFile(double[][] transitionMatrix) {
        try {
            Log logger = new Log("cluster.clu");
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
                        logger.log(String.valueOf(clusterCounter));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

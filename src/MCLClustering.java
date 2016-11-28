import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by VenkataRamesh on 11/28/2016.
 */
public class MCLClustering {

    private String fileName;
    private double[][] transitionMatrix;
    private double expansionParm;
    private double inflationParam;

    public MCLClustering(String fileName, double expansionParm, double inflationParam) throws Exception {
        this.fileName = fileName;
        this.expansionParm = expansionParm;
        this.inflationParam = inflationParam;
        this.transitionMatrix = generateTransitionMatrix(fileName);
    }


    public double[][] generateTransitionMatrix(String fileName) throws Exception {
        Path filePath = null;
        try {
            filePath = Paths.get(fileName);
            Stream<String> genes = Files.lines(filePath, StandardCharsets.UTF_8);
            List<String> geneData = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            /*int rows = geneData.size();
            this.objectCount = rows;
            int columns = geneData.get(0).split("\t").length;
            clusterIndex = columns;
            this.attributeCount = columns - 2;
            dataMatrix = new double[rows][columns + 2];
            for (int i = 0; i < rows; i++) {
                String[] geneAttributes = geneData.get(i).split("\t");
                for (int j = 0; j < columns - 1; j++) {
                    if (j == 0) dataMatrix[i][j] = Double.parseDouble(geneAttributes[j]);
                    else dataMatrix[i][j] = Double.parseDouble(geneAttributes[j + 1]);
                }
                dataMatrix[i][columns - 1] = Double.parseDouble(geneAttributes[1]);
                dataMatrix[i][clusterIndex] = 0;
                dataMatrix[i][columns + 1] = 0;
                this.visited = columns + 1;*/
        } catch (Exception ex) {

        }

        return new double[10][];
    }

}

package edu.wpi.checksims.algorithm.output;

import edu.wpi.checksims.submission.Submission;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Output a similarity matrix as HTML
 */
public class SimilarityMatrixAsHTMLPrinter implements SimilarityMatrixPrinter {
    private final String LVL_9_COLOR = "#FF0000";
    private final String LVL_8_COLOR = "#FF4000";
    private final String LVL_7_COLOR = "#FF8000";
    private final String LVL_6_COLOR = "#FFBF00";
    private final String LVL_5_COLOR = "#FFFF00";
    private final String LVL_4_COLOR = "#BFFF00";
    private final String LVL_3_COLOR = "#80FF00";
    private final String LVL_2_COLOR = "#40FF00";
    private final String LVL_1_COLOR = "#00FF00";
    private final String LVL_0_COLOR = "#00FF40";
    private final String LVL_UNEXPECTED_COLOR = "#FF00FF";
    private final String LVL_SAME_SUBMISSION = "#0000FF";

    private static SimilarityMatrixAsHTMLPrinter instance;

    private SimilarityMatrixAsHTMLPrinter() {}

    public static SimilarityMatrixAsHTMLPrinter getInstance() {
        if(instance == null) {
            instance = new SimilarityMatrixAsHTMLPrinter();
        }

        return instance;
    }

    private String generateCSS() {
        StringBuilder b = new StringBuilder();

        b.append("<style>\n");

        // Add border to tables
        b.append("table, th, td {\n");
        b.append("\tborder: 1px solid black;\n");
        b.append("}\n");

        // Remove overlapping collapsed borders
        b.append("table {\n");
        b.append("\tborder-collapse: collapse;\n");
        b.append("}\n");

        // Center text
        b.append("td, th {\n");
        b.append("\ttext-align: center;\n");
        b.append("\tpadding: 15px;\n");
        b.append("}\n");

        // Lvl unexpected
        b.append("td.unexpected {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_UNEXPECTED_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl same submission
        b.append("td.same {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_SAME_SUBMISSION);
        b.append(";\n");
        b.append("}\n");

        // Lvl 9
        b.append("td.lvl9 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_9_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl 8
        b.append("td.lvl8 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_8_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl 7
        b.append("td.lvl7 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_7_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl 6
        b.append("td.lvl6 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_6_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl 5
        b.append("td.lvl5 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_5_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl 4
        b.append("td.lvl4 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_4_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl 3
        b.append("td.lvl3 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_3_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl 2
        b.append("td.lvl2 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_2_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl 1
        b.append("td.lvl1 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_1_COLOR);
        b.append(";\n");
        b.append("}\n");

        // Lvl 0
        b.append("td.lvl0 {\n");
        b.append("\tbackground-color: ");
        b.append(LVL_0_COLOR);
        b.append(";\n");
        b.append("}\n");

        b.append("</style>\n");

        return b.toString();
    }

    private String generateHTMLHeader(String title) {
        StringBuilder b = new StringBuilder();

        b.append("<!DOCTYPE html>\n");
        b.append("<head>\n");
        b.append("<title>\n\t");
        b.append(title);
        b.append("\n</title>\n");
        b.append(generateCSS());
        b.append("</head>\n");
        b.append("<body>\n");

        return b.toString();
    }

    private String generateHTMLFooter() {
        StringBuilder b = new StringBuilder();

        b.append("</body>\n");
        b.append("</html>\n");

        return b.toString();
    }

    private String generateTableFromSimilarityTable(List<Submission> submissionList, float[][] similarityMatrix) {
        StringBuilder b = new StringBuilder();
        int matrixSize = submissionList.size();
        DecimalFormat formatter = new DecimalFormat("###.00");

        b.append("<table>\n");

        // Print X axis
        b.append("<tr>\n");
        b.append("<td></td>\n");
        for(int i = 0; i < matrixSize; i++) {
            b.append("<th>");
            b.append(submissionList.get(i).getName());
            b.append("</th>\n");
        }
        b.append("</tr>\n");

        for(int i = 0; i < matrixSize; i++) {
            b.append("<tr>\n");
            b.append("<th>");
            b.append(submissionList.get(i).getName());
            b.append("</th>\n");

            for(int j = 0; j < matrixSize; j++) {
                if(i == j) {
                    b.append("<td class=\"same\"></td>\n");
                } else {
                    b.append(tableCellFromFloat(formatter, similarityMatrix[i][j]));
                }
            }

            b.append("</tr>\n");
        }

        b.append("</table>\n");

        return b.toString();
    }

    private String tableCellFromFloat(DecimalFormat formatter, float f) {
        StringBuilder b = new StringBuilder();

        b.append("\t<td class=\"");

        String lvl;

        if(f > 1.0) {
            lvl = "unexpected";
        } else if(f >= 0.9) {
            lvl = "lvl9";
        } else if(f >= 0.8) {
            lvl = "lvl8";
        } else if(f >= 0.7) {
            lvl = "lvl7";
        } else if(f >= 0.6) {
            lvl = "lvl6";
        } else if(f >= 0.5) {
            lvl = "lvl5";
        } else if(f >= 0.4) {
            lvl = "lvl4";
        } else if(f >= 0.3) {
            lvl = "lvl3";
        } else if(f >= 0.2) {
            lvl = "lvl2";
        } else if(f >= 0.1) {
            lvl = "lvl1";
        } else if(f >= 0.0) {
            lvl = "lvl0";
        } else {
            lvl = "unexpected";
        }

        b.append(lvl);

        b.append("\">");
        b.append(formatter.format(100 * f));
        b.append("</td>\n");

        return b.toString();
    }

    @Override
    public String getName() {
        return "html";
    }

    @Override
    public String printMatrix(SimilarityMatrix matrix) {
        StringBuilder b = new StringBuilder();

        b.append(generateHTMLHeader("Similarity Matrix of Submissions"));
        b.append(generateTableFromSimilarityTable(matrix.getSubmissions(), matrix.getResults()));
        b.append(generateHTMLFooter());

        return b.toString();
    }
}

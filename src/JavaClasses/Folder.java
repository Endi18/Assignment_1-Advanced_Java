package JavaClasses;

import org.w3c.dom.Text;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Folder extends Thread{
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00000");
    private static final Map<String, String> languagePrefixNames = new HashMap<>();

    private final File folder;
    private final Histogram histogram;
    private final TextFileProperty mysteryTextFile;
    private final int nGramModel;

    private double similarity;
    private double angle;
    private String fullNameOfTheLanguage;

    public Folder(File folder, TextFileProperty mainFolder, int nGramModel){
        this.folder = folder;
        this.nGramModel = nGramModel;
        this.mysteryTextFile = mainFolder;
        this.histogram = new Histogram();
        this.similarity = -10;
        this.angle = -10;
        this.fullNameOfTheLanguage = null;
    }

    public double getSimilarity() {
        return similarity;
    }

    public double getAngle() {
        return angle;
    }

    public String getFullNameOfTheLanguage() {
        return fullNameOfTheLanguage;
    }

    @Override
    public void run(){

        updateLanguagePrefixNames();

        List<TextFileProperty> textFileList = Arrays.stream(Objects.requireNonNull
                (folder.list((file, fileName) -> fileName.endsWith(".txt"))))
                .map(child -> new TextFileProperty(new File(folder, child), nGramModel, histogram))
                .collect(Collectors.toList());

        if(textFileList.size() == 0) {
            System.out.println("No Text File Found");
            return;
        }

        new ExecutorThreadPool().executeAndAwait(textFileList);


        try{
            mysteryTextFile.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        double A_X_B = setA_X_B(mysteryTextFile);
        System.out.println(A_X_B);
        setSimilarityAndAngle(mysteryTextFile.getVectorValue(), textFileList.get(0).getVectorValue(), A_X_B);

        showLanguageSimilarityAndAngle();
    }

    private void updateLanguagePrefixNames(){
        languagePrefixNames.put("al", "Albanian");
        languagePrefixNames.put("de", "German");
        languagePrefixNames.put("en", "English");
        languagePrefixNames.put("fr", "French");
        languagePrefixNames.put("gr", "Greek");
        languagePrefixNames.put("it", "Italian");
    }

    private void setSimilarityAndAngle(double mysteryVector, double languageVector, double A_X_B){
        similarity = Double.parseDouble(decimalFormat.format(A_X_B / (mysteryVector * languageVector)));
        angle = Double.parseDouble(decimalFormat.format(Math.toDegrees((Math.acos(similarity)))));
    }

    private double setA_X_B(TextFileProperty mysteryTextFile){
        return mysteryTextFile.getHistogram()
                .entrySet()
                .stream()
                .filter(pointer -> histogram.get(pointer.getKey()) != null)
                .mapToDouble(point -> histogram.get(point.getKey()) * point.getValue())
                .sum();
    }

    private void showLanguageSimilarityAndAngle(){
        if(languagePrefixNames.containsKey(folder.getName()))
            fullNameOfTheLanguage = languagePrefixNames.get(folder.getName());
        else
            fullNameOfTheLanguage = folder.getName();

        System.out.printf("Language:%-16sSimilarity:%-15.5fAngle:%-10.5f\n", fullNameOfTheLanguage, similarity, angle);
    }
}
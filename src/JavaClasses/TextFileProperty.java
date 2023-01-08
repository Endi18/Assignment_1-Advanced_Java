package JavaClasses;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TextFileProperty extends Thread{
    private final File file;
    private final int nGramModel;
    private double vectorValue;
    private final Histogram histogram;

    public TextFileProperty(File file, int nGramModel, Histogram histogram){
        this.file = file;
        this.nGramModel = nGramModel;
        vectorValue = -999;
        this.histogram = histogram;
    }

    public TextFileProperty(File file, int nGramModel){
        this.file = file;
        this.nGramModel = nGramModel;
        this.histogram = new Histogram();
        vectorValue = -999;
    }

    public double getVectorValue(){
        if(vectorValue == -999) {
            synchronized (this) {
                if (vectorValue == -999)
                    vectorValue = Math.sqrt(histogram.values().stream()
                            .mapToDouble(frequency -> Math.pow(frequency, 2))
                            .sum());
                return vectorValue;
            }
        }
        return vectorValue;
    }

    public Histogram getHistogram(){
        return histogram;
    }

    @Override
    public void run(){

        try {
            String allText = Files.readString(Paths.get(file.getAbsolutePath()));

            if (allText.isEmpty())
                return; // if text file is empty

            String text = removeDigitsPunctuation(allText);
            populateHistogram(text);

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private List<String> tokenize(String word, int nGramModel){
        List<String> allTokens = new ArrayList<>();

        if(word.length() >= nGramModel){
            allTokens = IntStream.range(0, word.length() - nGramModel + 1)
                    .mapToObj(index -> word.substring(index, index + nGramModel))
                    .collect(Collectors.toList());
        }

        return allTokens;
    }

    private String removeDigitsPunctuation(String text){
        return text.toLowerCase()
                .replaceAll("\\p{Punct}", " ")
                .replaceAll("\\d", " ")
                .replaceAll("\\s{2,}", " ")
                .trim();
    }

    public void populateHistogram(String text){
        List<String> words = List.of(text.split(" "));
        synchronized (this) {
            words.stream()
                    .flatMap(word -> tokenize(word, nGramModel).stream())
                    .forEach(histogram::putTokens);
        }
    }

    @Override
    public String toString() {
        return "TextFileProperty{" +
                "file=" + file +
                ", nGramModel=" + nGramModel +
                ", vectorValue=" + vectorValue +
                '}';
    }
}

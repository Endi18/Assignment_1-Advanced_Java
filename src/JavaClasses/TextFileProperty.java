package JavaClasses;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TextFileProperty extends Thread{
    private final File file;
    private final Histogram histogram;
    private final int nGramModel;
    private double vectorValue;

    public TextFileProperty(File file, Histogram histogram, int nGramModel){
        this.file = file;
        this.histogram = histogram;
        this.nGramModel = nGramModel;
        vectorValue = -1;
    }

    public TextFileProperty(File file, int nGramModel){
        this.file = file;
        this.nGramModel = nGramModel;
        this.histogram = new Histogram();
        vectorValue = -1;
    }

    private void findTheValueOfVector(){
        double sumOfSquare = histogram.values().stream()
                .mapToDouble(frequency -> frequency * frequency)
                .sum();

        vectorValue = Math.sqrt(sumOfSquare);
    }

    synchronized  public double getVectorValue(){
        if(vectorValue == -1)
            findTheValueOfVector();
        return vectorValue;
    }

    public Histogram getHistogram(){
        return histogram;
    }

    @Override
    public void run(){
        try(Scanner read = new Scanner(file)) {
            read.useDelimiter("\\Z");

            if (!read.hasNext())
                return; // if text file is empty

            String text = read.next();

            text = text
                    .toLowerCase()
                    .replaceAll("\\p{Punct}", " ")
                    .replaceAll("\\d", " ")
                    .replaceAll("\\s{2,}", " ")
                    .trim();

            List<String> words = List.of(text.split(" "));
            words.stream()
                    .flatMap(word -> tokenize(word, nGramModel).stream())
                    .forEach(histogram::putTokens);

        } catch (FileNotFoundException e ){
            e.printStackTrace();
        }
    }

    synchronized public List<String> tokenize(String word, int nGramModel){
        List<String> allTokens = new ArrayList<>();

        if(word.length() >= nGramModel){
            allTokens = IntStream.range(0, word.length() - nGramModel + 1)
                    .mapToObj(number -> word.substring(number, number + nGramModel))
                    .collect(Collectors.toList());
        }
        return allTokens;
    }
}

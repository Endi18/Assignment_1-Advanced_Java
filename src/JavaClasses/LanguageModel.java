package JavaClasses;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class LanguageModel {
    private final List<String> languageSimilarToMysteryFile = new ArrayList<>();

    public static LanguageModel languageModel;
    private final File mainFolder;

    public LanguageModel(File mainFolder) {
        this.mainFolder = mainFolder;
    }

    public List<String> getLanguageSimilarToMysteryFile() {
        return languageSimilarToMysteryFile;
    }

    public File retrieveMysteryFile() {
        return new File(mainFolder, "mystery.txt");
    }

    public static LanguageModel getLanguageModel(){
        return languageModel;
    }

    public static boolean isLanguageModelCreated(String path){
        File folder = new File(path);

        if(checkFolder(folder)){
            languageModel = new LanguageModel(folder);
            return true;
        }
        return false;
    }

    private static boolean checkFolder(File folder){

        if(!folder.exists() || !folder.canRead() || !(new File(folder, "mystery.txt").exists())){
            System.out.println("Sorry, there is an error");
            return false;
        }

        return true;
    }

    public void documentDistance(int nGramModel, TextFileProperty mysteryModel){
        String regex = "^[a-z]{2}$";
        Predicate<String> is2LetterWord = folder -> folder.matches(regex);

        List<Folder> knownLanguages = Arrays.stream(
                        Objects.requireNonNull(mainFolder.list((parent, child) -> new File(parent, child).isDirectory())))
                .filter(is2LetterWord)
                .map(child -> new Folder(new File(mainFolder, child), mysteryModel, nGramModel))
                .collect(Collectors.toList());

        if(knownLanguages.isEmpty()){
            languageSimilarToMysteryFile.add("No Language Found");
            return;
        }
        else if (knownLanguages.size() < 2){
            languageSimilarToMysteryFile.add("There MUST be at least 2 subfolder/languages");
        }

        new ExecutorThreadPool().executeAndAwait(knownLanguages);

        List<Folder> result = getLanguagesWithHighestSimilarity(knownLanguages);

        saveTheLanguageWithHighestSimilarity(result);

    }

    public List<Folder> getLanguagesWithHighestSimilarity(List<Folder> knownLanguages){
        double maxValue = knownLanguages.stream()
                .filter(language -> !Double.isNaN(language.getSimilarity()))
                .mapToDouble(Folder::getSimilarity)
                .max().orElse(-999);

        return knownLanguages.stream()
                .filter(language -> language.getSimilarity() == maxValue)
                .toList();
    }

    private void saveTheLanguageWithHighestSimilarity(List<Folder> result){
        int numberOfLanguagesWithHighestSimilarity = result.size();

        if(numberOfLanguagesWithHighestSimilarity == 1)
            languageSimilarToMysteryFile.add("\nThere is " + numberOfLanguagesWithHighestSimilarity +
                    " language with the same highest similarity value.");
        else
            languageSimilarToMysteryFile.add("\nThere are " + numberOfLanguagesWithHighestSimilarity +
                    " languages with the same highest similarity value.");

        result.forEach(language ->
                languageSimilarToMysteryFile.add((String.format("Nearest Language with mystery.txt is: %s Language with Similarity: %.5f and Angle: %.5f.",
                        language.getFullNameOfTheLanguage(), language.getSimilarity(), language.getAngle())))
        );
    }
}
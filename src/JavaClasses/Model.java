package JavaClasses;

public class Model extends Thread{
    private final String mainFolderPath;
    private final int nGramModel;

    public Model(String mainFolderPath, int nGramModel){
        this.mainFolderPath = mainFolderPath;
        this.nGramModel = nGramModel;
    }

    @Override
    public void run(){

        if(!LanguageModel.isLanguageModelCreated(mainFolderPath)){
            System.out.println("Program Terminated");
            System.exit(-1);
        }

        TextFileProperty mysteryFile = new TextFileProperty(LanguageModel.getLanguageModel().retrieveMysteryFile(), nGramModel);
        mysteryFile.start();

        LanguageModel.getLanguageModel().documentDistance(nGramModel, mysteryFile);

        LanguageModel.getLanguageModel().getLanguageSimilarToMysteryFile().forEach(System.out::println);
    }

}

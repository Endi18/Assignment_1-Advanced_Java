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

    public static void main(String[] args) {

        int nGramModel = 0;
        String folderPath = "";

        if(args.length==2) {
            try {
                nGramModel = Integer.parseInt(args[1]);
                if(nGramModel < 1 || nGramModel > 3) throw new Exception();
                folderPath = args[0];
            } catch (Exception e) {
                System.out.println("Wrong Inputs");
            }
        }
        else if(args.length == 1){
            folderPath = args[0];
            nGramModel = 2;
        }
        else {
            System.out.println("Wrong input!");
            System.exit(-1);
        }

        new Model(folderPath, nGramModel).start();
      //  new Model("..\\Assignment_1-Advanced_Java\\Local Folder", 2).start();
    }
}

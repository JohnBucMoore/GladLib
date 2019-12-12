import edu.duke.FileResource;
import edu.duke.URLResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GladLibMap {
    private HashMap<String, ArrayList<String>> wordMap;
    private static HashMap<String, String> myLabelSource;
    private ArrayList<String> usedWords;
    private int replacedWords;

    private Random myRandom;

    private static String dataSourceURL = "http://dukelearntoprogram.com/course3/data";
    private static String dataSourceDirectory = "GladLibData/datalong";

    public GladLibMap(){
        wordMap = new HashMap<>();
        myLabelSource = new HashMap<>();
        usedWords = new ArrayList<>();
        myRandom = new Random();
        replacedWords = 0;
        initializeFromSource();
    }

    private void getProperties() {
        FileResource props = new FileResource();
        for (String prop : props.lines()) {
            int propIndex = prop.indexOf(':');
            String label = prop.substring(0, propIndex);
            String source = prop.substring(propIndex+1);
            myLabelSource.put(label, source);
        }
    }

    private void initializeFromSource() {
        getProperties();
        for(String s : myLabelSource.keySet()) {
            ArrayList<String> list = readIt(myLabelSource.get(s));
            wordMap.put(s, list);
        }
    }

    private String randomFrom(ArrayList<String> source){
        int index = myRandom.nextInt(source.size());
        return source.get(index);
    }

    private String getSubstitute(String label) {
        if (label.equals("number")){
            return ""+myRandom.nextInt(50)+5;
        }
        if (wordMap.containsKey(label)) {
            return randomFrom(wordMap.get(label));
        }
        return "**UNKNOWN**";
    }

    private String processWord(String w){
        int first = w.indexOf("<");
        int last = w.indexOf(">",first);
        if (first == -1 || last == -1){
            return w;
        }
        String prefix = w.substring(0,first);
        String suffix = w.substring(last+1);
        String sub = getSubstitute(w.substring(first+1,last));
        while (usedWords.contains(sub)) {
            sub = getSubstitute(w.substring(first+1,last));
        }
        usedWords.add(sub);
        replacedWords++;
        return prefix+sub+suffix;
    }

    private void printOut(String s, int lineWidth){
        int charsWritten = 0;
        for(String w : s.split("\\s+")){
            if (charsWritten + w.length() > lineWidth){
                System.out.println();
                charsWritten = 0;
            }
            System.out.print(w+" ");
            charsWritten += w.length() + 1;
        }
    }

    private String fromTemplate(String source){
        String story = "";
        if (source.startsWith("http")) {
            URLResource resource = new URLResource(source);
            for(String word : resource.words()){
                story = story + processWord(word) + " ";
            }
        }
        else {
            FileResource resource = new FileResource(source);
            for(String word : resource.words()){
                story = story + processWord(word) + " ";
            }
        }
        return story;
    }

    private ArrayList<String> readIt(String source){
        ArrayList<String> list = new ArrayList<>();
        if (source.startsWith("http")) {
            URLResource resource = new URLResource(source);
            for(String line : resource.lines()){
                list.add(line);
            }
        }
        else {
            FileResource resource = new FileResource(source);
            for(String line : resource.lines()){
                list.add(line);
            }
        }
        return list;
    }

    public int totalWordsInMap() {
        int total = 0;
        for (String wordList : wordMap.keySet()) {
            total += wordList.length();
        }
        return total;
    }

    public void makeStory(){
        System.out.println("\n");
        String story = fromTemplate("GladLibData/datalong/madtemplate2.txt");
        printOut(story, 60);
        System.out.println("\n"+replacedWords);
        System.out.println(totalWordsInMap());
        usedWords.clear();
    }

    public static void main(String[] args) {
        GladLibMap gl = new GladLibMap();
        gl.makeStory();
    }

}


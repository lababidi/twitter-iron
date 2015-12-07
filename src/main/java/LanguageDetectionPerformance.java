import com.carrotsearch.labs.langid.LangIdV3;
import com.google.common.collect.HashBasedTable;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObjectFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import org.jetbrains.annotations.NotNull;
import twitter.*;
import twitter.Properties;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LanguageDetectionPerformance implements Runnable {
    //    private final Pattern textPattern;
    private final LangIdV3 langID;
    BlockingQueue<String> queue;
    TextObjectFactory textObjectFactory;
    LanguageDetector languageDetector;
    double detectorCorrect, detectorWrong;
    Pattern emoticons;
    Pattern urlPattern;

    HashBasedTable<String, String, Integer> detectorMatrix;
    HashBasedTable<String, String, Integer> idMatrix;
    private JsonConvertor jsonConvertor;

    public LanguageDetectionPerformance(BlockingQueue<String> queue) {
        detectorCorrect = 0;
        detectorWrong = 0;
        this.queue = queue;
//        textPattern = Pattern.compile("\"text\":\\s*\"(.+?)\".*\"lang\":\\s*\"(.+?)\"");
        detectorMatrix = HashBasedTable.create();
        idMatrix = HashBasedTable.create();
        try {
            languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .withProfiles(new LanguageProfileReader().readAllBuiltIn())
                    .shortTextAlgorithm(300)
                    .build();
            textObjectFactory = CommonTextObjectFactories.forDetectingShortCleanText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.langID = new LangIdV3();
        jsonConvertor = new JsonConvertor();
        String regex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        urlPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        emoticons = Pattern.compile("\\p{InEmoticons}");
    }


    @Override
    public void run() {
        int num = 0;
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String json = queue.take();
                Message message = jsonConvertor.convert(json);
                if (message != null) {
                    ++num;
                    String text = massageMessage(message), lang = getLanguage(message);

                    List<DetectedLanguage> languageOpt = languageDetector.getProbabilities(textObjectFactory.forText(text));
                    String detLang;
                    detLang = languageOpt.size() == 0 ? "und" : languageOpt.get(0).getLocale().getLanguage();
//                        if(!detLang.equals(lang) && languageOpt.size()>1){detLang = languageOpt.get(1).getLocale().getLanguage();}
                    updateMatrix(detectorMatrix, lang, detLang);
                    System.out.println(detLang + " " + lang + " === " + text);
                    System.out.println("detector");
                    printFullMatrix(detectorMatrix);

                    langID.classify(text, true);
                    List<com.carrotsearch.labs.langid.DetectedLanguage> results = new ArrayList<>(langID.rank(true));
                    Collections.sort(results, (o1, o2) -> Float.compare(o2.confidence, o1.confidence));
                    List<String> detLangs = results.stream()
                            .map(com.carrotsearch.labs.langid.DetectedLanguage::getLangCode)
                            .collect(Collectors.toList());

                    detLang = results.size() == 0 ? "und" : detLangs.get(0);
//                        if(!detLang.equals(lang) && detLangs.size()>1){detLang = detLangs.get(1);}
                    updateMatrix(idMatrix, lang, detLang);
                    System.out.println(detLang + " " + lang + " === " + text);
                    System.out.println("langid");
                    printFullMatrix(idMatrix);

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @NotNull
    private String getLanguage(Message message) {
        String lang = message.lang;
        if (lang.equals("in"))
            lang = "id";
        if (lang.equals("iw"))
            lang = "he";
        return lang;
    }

    private String massageMessage(Message message) {
        String text = urlPattern.matcher(emoticons.matcher(message.text).replaceAll("")).replaceAll("");
        for (Hashtag tag : message.entities.hashtags) {
            text = text.replace("#" + tag.text, "");
        }
        for (UserMentions mentions : message.entities.userMentions) {
            text = text.replace("@" + mentions.screenName, "");
        }
        return text;
    }

    private void updateMatrix(HashBasedTable<String, String, Integer> matrix, String trueLang, String detLang) {
        int val = matrix.get(trueLang, detLang) == null ? 0 : matrix.get(trueLang, detLang);
        matrix.put(trueLang, detLang, val + 1);
    }

    private void printMatrix(HashBasedTable<String, String, Integer> matrix) {
        for (String row : matrix.rowKeySet()) {
            int cor = 0, wrong = 0;
            for (Map.Entry<String, Integer> item : matrix.row(row).entrySet()) {
                if (item.getKey().equals(row))
                    cor = item.getValue();
                else
                    wrong += item.getValue();
            }
            System.out.println(row + " " + cor + " " + wrong + " " + Math.round(100 * (double) cor / (cor + wrong)));
        }
    }

    private void printFullMatrix(HashBasedTable<String, String, Integer> matrix) {
        ArrayList<String> langs = new ArrayList<>(matrix.rowKeySet());
        Collections.sort(langs);
        System.out.print("\t");
        for (String row : langs) {
            System.out.print(row + "\t");
        }
        System.out.println();

        for (String row : langs) {
            System.out.print(row+"\t");
            int cor = 0, wrong = 0;
            for (String col: langs) {
                int val = matrix.contains(row, col)? matrix.get(row, col): 0;
                System.out.print( val + "\t");
                if(col.equals(row))
                    cor = val;
                else
                    wrong += val;
            }
            double sum = cor + wrong == 0 ? 1 : cor + wrong;
            System.out.print( Math.round(100 * (double) cor / sum) + "%");
            System.out.println();
        }
    }

    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Streaming");
        parser.addArgument("-p", "--prop");
        Namespace ns;
        try {
            ns = parser.parseArgs(args);
            String propFileName = ns.getString("prop");

            BlockingQueue<String> queue = new LinkedBlockingQueue<>();
            Properties p = new Properties(propFileName);
            Streaming streaming = new Streaming(queue, p);
            LanguageDetectionPerformance detection = new LanguageDetectionPerformance(queue);
            streaming.go();
            System.out.print("go");
            new Thread(detection).start();
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }
}

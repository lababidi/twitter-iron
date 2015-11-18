import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import twitter.Properties;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LanguageDetectionPerformance implements Runnable{
    private final Pattern textPattern;
    BlockingQueue<String> queue;
    TextObjectFactory textObjectFactory;
    LanguageDetector languageDetector;
    double correct, wrong;

    public LanguageDetectionPerformance(BlockingQueue<String> queue) {
        correct = 0;
        wrong = 0;
        this.queue = queue;
        textPattern = Pattern.compile("\"text\":\\s*\"(.+?)\".*\"lang\":\\s*\"(.+?)\"");

        List<LanguageProfile> languageProfiles = null;
        try {
            languageProfiles = new LanguageProfileReader().readAllBuiltIn();
//build language detector:
            languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
                    .withProfiles(languageProfiles)
                    .build();

//create a text object factory
            textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();
        } catch (IOException e) {
            e.printStackTrace();
        }
        }



    @Override
    public void run() {
        while( !Thread.currentThread().isInterrupted()) {
            try {
                String json = queue.take();

                Matcher textMatch = textPattern.matcher(json);

                while (textMatch.find()){
                    String text = textMatch.group(1);
                    String lang = textMatch.group(2);
//                    System.out.println(text);

                    TextObject textObject = textObjectFactory.forText(text);
                    try {
                        Optional<LdLocale> languageOpt = languageDetector.detect(text);
                        LdLocale language;
                        if (languageOpt.isPresent() )
                        {
                            language = languageOpt.get();
                            if(!language.getLanguage().equals(lang)) {
                                System.out.println(language.getLanguage() + " " + lang + " === " + text);
                                wrong += 1;
                            }
                            else correct += 1;
                            double sum = correct + wrong;
                            System.out.println(correct/sum + " " + wrong/sum);
                        }
                    } catch (IllegalArgumentException e){

                        System.out.println(" " + lang + " === " +text);
                    }


                }



            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String[]args){
        ArgumentParser parser = ArgumentParsers.newArgumentParser("Streaming");
        parser.addArgument("-p","--prop");
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

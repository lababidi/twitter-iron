import com.carrotsearch.labs.langid.LangIdV3;
import com.optimaize.langdetect.DetectedLanguage;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObjectFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by mahmoud on 11/19/15.
 */
public class SimpleLanguageDetector {

    TextObjectFactory textObjectFactory;
    LanguageDetector languageDetector;

    public SimpleLanguageDetector() {

        try {
            List<LanguageProfile> languageProfiles = new LanguageProfileReader().readAllBuiltIn();
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

    public String detect(String text){
        List<DetectedLanguage> languageOpt = languageDetector.getProbabilities(text);
        return languageOpt.size()>0?languageOpt.get(0).getLocale().getLanguage(): "und";
    }

    public static void main(String[] args){
        SimpleLanguageDetector detector = new SimpleLanguageDetector();
        LangIdV3 langId = new LangIdV3();
        String s = "\u0441\u0435\u0433\u043e\u0434\u043d\u044f \u0441\u044a\u0435\u043b\u0430 \u0433\u0440\u0435\u0447\u043a\u0438 \u0431\u0435\u0437  \u0434\u0430\u0436\u0435 \u043d\u0435 \u0445\u043e\u0447\u0435\u0442\u0441\u044f \u0441\u043b\u0430\u0434\u043a\u043e\u0433\u043e\n\u0441\u0442\u0440\u0435\u0441\u0441 \u043f\u0440\u0438\u0432\u0435\u0442\u0438\u043a\n";
        s = "Yeah he's amazing \ud83d\udcaa\ud83d\udc97 #1DHarry\n https://t.co/YgKO9moCEs";
//        s = "今回の強化詐欺騒動について俺は、他人事とまでは言わずとも、自分が具体的な損害をこうむることはあるまいと思っていた。鍛冶屋ネズハに剣を預けなければいいだけの話だ、と。";
//        s = "";
        s = "Missing Barcelona already...";
        System.out.println(detector.detect(s));
        System.out.println(langId.classify(s, true));
    }
}

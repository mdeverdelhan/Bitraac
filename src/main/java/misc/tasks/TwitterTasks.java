package misc.tasks;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import misc.model.TrendDesign;
import misc.repositories.TrendDesignRepository;
import misc.services.TwitterService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.w3c.dom.Document;
import org.xhtmlrenderer.simple.Graphics2DRenderer;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Component
@EnableScheduling
public class TwitterTasks {

    @Inject
    private TrendDesignRepository trendDesignRepository;

    @Inject
    private TwitterService twitterService;

    @Scheduled(fixedDelay = 5000)
    void storeTrends() {
        String trendName = twitterService.getTrends().get(0);
        String tweetText = twitterService.getMostPopularTweetText(trendName);
        TrendDesign newDesign = new TrendDesign();
        newDesign.setName(trendName);
        newDesign.setText(tweetText);
        newDesign.setCreatedDate(new Date());
        trendDesignRepository.save(newDesign);
        System.out.println("design count: " + trendDesignRepository.count());
        buildImage(tweetText);
    }

    private void buildImage(String message) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("XHTML");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context ctx = new Context();
        ctx.setVariable("message", message);
        String htmlContent = templateEngine.process("shirt", ctx);

        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(new StringReader(htmlContent)));
            Graphics2DRenderer gr = new Graphics2DRenderer();
            gr.setDocument(doc, "");
            BufferedImage image = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
            Graphics2D imageGraphics = (Graphics2D) image.getGraphics();
            imageGraphics.setColor(Color.white);
            imageGraphics.fillRect(0, 0, 800, 600);
            gr.layout(imageGraphics, new Dimension(800, 600));
            gr.render(imageGraphics); //Now output the image to PNG using the ImageIO libraries.
            ImageIO.write(image, "png", new File("test.png"));
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(TwitterTasks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TwitterTasks.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(TwitterTasks.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}

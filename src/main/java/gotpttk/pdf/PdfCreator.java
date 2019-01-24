package gotpttk.pdf;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import gotpttk.entities.*;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

@Component
public class PdfCreator {

    private Category category;
    private List<BookRoute> bookRoutes;
    private List<Excursion> excursions;
    private String summary;
    private boolean isAchieved;

    private boolean shouldGenerateBookRoutes;
    private boolean shouldGenerateExcursions;
    private boolean shouldGenerateSummary;
    private boolean shouldGenerateRemainingRoutes;



    public PdfCreator() {
    }

    public void setShouldGenerateBookRoutes(boolean shouldGenerateBookRoutes) {
        this.shouldGenerateBookRoutes = shouldGenerateBookRoutes;
    }

    public void setShouldGenerateExcursions(boolean shouldGenerateExcursions) {
        this.shouldGenerateExcursions = shouldGenerateExcursions;
    }

    public void setShouldGenerateSummary(boolean shouldGenerateSummary) {
        this.shouldGenerateSummary = shouldGenerateSummary;
    }

    public void setShouldGenerateRemainingRoutes(boolean shouldGenerateRemainingRoutes) {
        this.shouldGenerateRemainingRoutes = shouldGenerateRemainingRoutes;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setBookRoutes(List<BookRoute> bookRoutes) {
        this.bookRoutes = bookRoutes;
    }

    public void setExcursions(List<Excursion> excursions) {
        this.excursions = excursions;
    }

    public void setAchieved(boolean achieved) {
        isAchieved = achieved;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getFile(){
        Document document = new Document();
        String path = "";
        try {
            BaseFont bfTimes = BaseFont.createFont(FontFactory.HELVETICA,"UTF-8", BaseFont.EMBEDDED);
            Font font = new Font(bfTimes, 12, Font.BOLD);

            File file = File.createTempFile("dossier_" + 3 + "_", ".pdf");
            path = file.getPath();
            PdfWriter.getInstance(document, new FileOutputStream(path));
            document.open();
            Paragraph paragraph = new Paragraph("Podsumowanie zdobycia odznaki: " + category.getName(), font);
            document.add(paragraph);
            document.add(new Paragraph("\n"));
            if (shouldGenerateBookRoutes){
                PdfPTable table = new PdfPTable(4);
                table.addCell("Z punktu");
                table.addCell("Do punktu");
                table.addCell("Data przejscia");
                table.addCell("Przyznane punkty");

                for (var bookRoute : bookRoutes){
                    boolean isFromStart = bookRoute.getIsFromStartToEnd();
                    Route route = bookRoute.getRoute();

                    if (isFromStart){
                        table.addCell(route.getStartingPoint().getName());
                        table.addCell(route.getEndPoint().getName());
                    }
                    else{
                        table.addCell(route.getEndPoint().getName());
                        table.addCell(route.getStartingPoint().getName());
                    }
                    table.addCell(bookRoute.getDateOfCompletion().toString());
                    table.addCell(Integer.toString(bookRoute.getPointsAwarded()));
                }
                document.add(table);
            }

            if (shouldGenerateExcursions){
                document.add(new Paragraph("Przebyte wycieczki"));
                document.add(new Paragraph("Podczas zdobywania tej odznaki, nie przebyto zadnej wycieczki"));
            }

            if (shouldGenerateRemainingRoutes){
                /// todo remaining routes
            }

            if(shouldGenerateSummary){
                document.add(new Paragraph("Stopien spelnienia warunkow do zdobycia odznaki: "));
                document.add(new Paragraph(summary));
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        finally {
            document.close();
        }
        return path;
    }

    public void reset() {
        shouldGenerateRemainingRoutes = false;
        shouldGenerateExcursions = false;
        shouldGenerateSummary = false;
        shouldGenerateBookRoutes = false;
    }
}
